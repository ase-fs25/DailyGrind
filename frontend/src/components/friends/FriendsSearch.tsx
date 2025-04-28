import React, { useState } from 'react';
import { Box, TextField, Button, Typography, CircularProgress } from '@mui/material';
import { searchUsers, sendFriendRequest, UserProfile } from '../../helpers/friendsHelper';
import '../../styles/components/friends/friendsSearch.css';

const FriendsSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [profiles, setProfiles] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(false);

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
      setProfiles(results);
    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSendFriendRequest = async (userId: string) => {
    try {
      await sendFriendRequest(userId);
      alert('Friend request sent!');
    } catch (error) {
      console.error('Send friend request error:', error);
      alert('Failed to send friend request.');
    }
  };

  return (
    <Box className="search-container">
      {/* Search Input */}
      <TextField
        label="Search People"
        variant="outlined"
        value={searchTerm}
        onChange={handleSearchChange}
        className="search-input"
      />

      {/* Loading spinner */}
      {loading && (
        <Box display="flex" justifyContent="center" mt={2}>
          <CircularProgress />
        </Box>
      )}

      {/* Search results */}
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
                  onClick={() => handleSendFriendRequest(user.userId)}
                >
                  Add Friend
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
