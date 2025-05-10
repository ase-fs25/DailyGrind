import React, { useState, useEffect } from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import { Post } from '../../types/post';
import FriendPopup from '../common/FriendPopup';
import { fetchFriends, UserProfile } from '../../helpers/friendsHelper';
import { getPostsByUserId } from '../../helpers/postHelper';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [friends, setFriends] = useState<UserProfile[]>([]);
  const [selectedProfile, setSelectedProfile] = useState<UserProfile | null>(null);
  const [userPosts, setUserPosts] = useState<Post[]>([]);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const loadFriends = async () => {
      try {
        const data = await fetchFriends();
        setFriends(data);
      } catch (error) {
        console.error('Failed to fetch friends:', error);
      }
    };

    loadFriends();
  }, []);

  const handleOpen = async (user: UserProfile) => {
    setSelectedProfile(user);
    setOpen(true);

    try {
      const posts = await getPostsByUserId(user.userId);
      setUserPosts(posts);
    } catch (error) {
      console.error(`Failed to load posts for user ${user.userId}:`, error);
      setUserPosts([]);
    }
  };

  const handleClose = () => {
    setSelectedProfile(null);
    setUserPosts([]);
    setOpen(false);
  };

  return (
    <Box className="friends-list-container">
      {friends.map((user) => (
        <Card key={user.userId} className="friend-card" onClick={() => handleOpen(user)}>
          <CardContent className="friend-card-content">
            <Typography variant="h6" className="friend-username">
              {user.firstName} {user.lastName}
            </Typography>
          </CardContent>
        </Card>
      ))}

      <FriendPopup open={open} onClose={handleClose} user={selectedProfile} posts={userPosts} />
    </Box>
  );
};

export default FriendsList;
