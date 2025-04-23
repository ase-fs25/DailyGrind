import React, { useState } from 'react';
import { Box, Card, CardContent, Avatar, Typography } from '@mui/material';
import { mockProfiles } from '../../mockData/mockProfiles';
import { mockPosts } from '../../mockData/mockPosts';
import { Profile } from '../../types/profile';
import { PostType } from '../../types/post';
import FriendPopup from '../common/FriendPopup';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [selectedProfile, setSelectedProfile] = useState<Profile | null>(null);
  const [open, setOpen] = useState(false);

  const handleOpen = (profile: Profile) => {
    setSelectedProfile(profile);
    setOpen(true);
  };

  const handleClose = () => {
    setSelectedProfile(null);
    setOpen(false);
  };

  const userPosts: PostType[] = selectedProfile
    ? mockPosts.filter((post) => post.user_id === String(selectedProfile.userId)).slice(0, 2)
    : [];

  return (
    <Box className="friends-list-container">
      {mockProfiles.map((profile) => (
        <Card key={profile.userId} className="friend-card" onClick={() => handleOpen(profile)}>
          <Avatar src={profile.profileInfo.profilePicture} className="friend-avatar" />
          <CardContent className="friend-card-content">
            <Typography variant="h6" className="friend-username">
              {profile.username}
            </Typography>
          </CardContent>
        </Card>
      ))}

      <FriendPopup open={open} onClose={handleClose} profile={selectedProfile} posts={userPosts} />
    </Box>
  );
};

export default FriendsList;
