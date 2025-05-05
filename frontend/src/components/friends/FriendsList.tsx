import React, { useState, useEffect } from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import { Post } from '../../types/post';
import { User } from '../../types/user';
import FriendPopup from '../common/FriendPopup';
import { fetchFriends, UserProfile } from '../../helpers/friendsHelper';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [friends, setFriends] = useState<UserProfile[]>([]);
  const [selectedProfile, setSelectedProfile] = useState<User | null>(null);
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

  const handleOpen = (user: User) => {
    setSelectedProfile(user);
    setOpen(true);
  };

  const handleClose = () => {
    setSelectedProfile(null);
    setOpen(false);
  };

  const userPosts: Post[] = []; // TODO: implement if needed

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
