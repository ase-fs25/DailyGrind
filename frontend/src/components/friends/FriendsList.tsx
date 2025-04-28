import { useState } from 'react';
import { Box, Card, CardContent, Typography } from '@mui/material';
import { mockProfiles } from '../../mockData/mockProfiles';
import { mockPosts } from '../../mockData/mockPosts';
import { User } from '../../types/user';
import { Post } from '../../types/post';
import FriendPopup from '../common/FriendPopup';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [selectedProfile, setSelectedProfile] = useState<User | null>(null);
  const [open, setOpen] = useState(false);

  const handleOpen = (user: User) => {
    setSelectedProfile(user);
    setOpen(true);
  };

  const handleClose = () => {
    setSelectedProfile(null);
    setOpen(false);
  };

  const userPosts: Post[] = selectedProfile
    ? mockPosts.filter((post) => post.postId === String(selectedProfile.userId)).slice(0, 2)
    : [];

  return (
    <Box className="friends-list-container">
      {mockProfiles.map((user) => (
        <Card key={user.userId} className="friend-card" onClick={() => handleOpen(user)}>
          <CardContent className="friend-card-content">
            <Typography variant="h6" className="friend-username">
              {user.firstName + ' ' + user.lastName}
            </Typography>
          </CardContent>
        </Card>
      ))}

      <FriendPopup open={open} onClose={handleClose} user={selectedProfile} posts={userPosts} />
    </Box>
  );
};

export default FriendsList;
