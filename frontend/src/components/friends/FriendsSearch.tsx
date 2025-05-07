import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Typography, CircularProgress } from '@mui/material';
import {
  searchUsers,
  sendFriendRequest,
  fetchFriends,
  UserProfile,
} from '../../helpers/friendsHelper';
import '../../styles/components/friends/friendsSearch.css';
import userStore from '../../stores/userStore';

const FriendsSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [profiles, setProfiles] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(false);
  const [friends, setFriends] = useState<UserProfile[]>([]);

  useEffect(() => {
    const loadFriends = async () => {
      try {
        const friendsList = await fetchFriends();
        setFriends(friendsList);
      } catch (err) {
        console.error('Failed to load friends:', err);
      }
    };

    loadFriends();
  }, []);

  const handleSearchChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchTerm(value);

    if (value.trim() === '') {
      setProfiles([]);
      return;
    }

    try {
      setLoading(true);
      const results = await searchUsers(value);
      const currentUserId = userStore.getUser().userId;

      const enriched = results
        .filter((user) => user.userId !== currentUserId)
        .map((user) => {
          const isAlreadyFriend = friends.some((f) => f.userId === user.userId);
          return {
            ...user,
            hasPendingRequest: user.hasPendingRequest ?? false,
            isAlreadyFriend,
          };
        });

      setProfiles(enriched);
    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSendFriendRequest = async (userId: string) => {
    try {
      await sendFriendRequest(userId);
      setProfiles((prev) =>
        prev.map((user) =>
          user.userId === userId ? { ...user, hasPendingRequest: true } : user
        )
      );
    } catch (error) {
      console.error('Send friend request error:', error);
    }
  };

  return (
    <Box className="search-container">
      <TextField
        label="Search People"
        variant="outlined"
        value={searchTerm}
        onChange={handleSearchChange}
        className="search-input"
      />

      {loading && (
        <Box display="flex" justifyContent="center" mt={2}>
          <CircularProgress />
        </Box>
      )}

      {!loading && searchTerm.trim() !== '' && (
        <Box className="search-results">
          {profiles.length === 0 ? (
            <Typography variant="subtitle2" align="center" color="textSecondary">
              No users found.
            </Typography>
          ) : (
            profiles.map((user) => (
              <Box key={user.userId} className="search-result-item">
                <Typography variant="subtitle1">
                  {user.firstName} {user.lastName}
                </Typography>
                <Button
                  variant="outlined"
                  color="secondary"
                  size="small"
                  disabled={user.hasPendingRequest || user.isAlreadyFriend}
                  onClick={() => handleSendFriendRequest(user.userId)}
                >
                  {user.isAlreadyFriend
                    ? 'Already a Friend'
                    : user.hasPendingRequest
                    ? 'Friend Request Sent'
                    : 'Add Friend'}
                </Button>
              </Box>
            ))
          )}
        </Box>
      )}
    </Box>
  );
};

export default FriendsSearch;
