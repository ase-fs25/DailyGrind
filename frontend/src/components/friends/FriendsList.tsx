import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Avatar,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { mockProfiles } from '../../mockData/mockProfiles';
import { mockPosts } from '../../mockData/mockPosts';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [open, setOpen] = useState(false);

  const handleOpen = (profile) => {
    setSelectedProfile(profile);
    setOpen(true);
  };

  const handleClose = () => {
    setSelectedProfile(null);
    setOpen(false);
  };

  const userPosts =
    selectedProfile && mockPosts.filter((post) => post.user_id === String(selectedProfile.userId)).slice(0, 2);

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

      <Dialog
        open={open}
        onClose={handleClose}
        fullWidth
        maxWidth="md"
        slotProps={{
          backdrop: {
            timeout: 600,
            style: {
              backgroundColor: 'rgba(255, 255, 255, 0.5)',
              backdropFilter: 'blur(4px)',
            },
          },
        }}
      >
        <DialogTitle className="popup-header" style={{ position: 'relative' }}>
          {selectedProfile?.username || 'Profile'}
          <IconButton onClick={handleClose} className="close-button" style={{ position: 'absolute', right: 8, top: 8 }}>
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent className="popup-content">
          <Box className="friend-profile-section" display="flex" flexDirection="column" alignItems="center" mb={2}>
            <Avatar
              src={selectedProfile?.profileInfo.profilePicture}
              alt={selectedProfile?.username}
              style={{ width: 120, height: 120, marginBottom: 16 }}
            />
            <Typography variant="h6">{selectedProfile?.username}</Typography>
            <Typography variant="body1">Location: {selectedProfile?.profileInfo.location}</Typography>
            <Typography variant="body1">Education: {selectedProfile?.profileInfo.education}</Typography>
            <Typography variant="body1">Work Experience: {selectedProfile?.profileInfo.workExperience}</Typography>
          </Box>

          <Box className="posts-section">
            <Typography variant="h6">Posts</Typography>
            {userPosts && userPosts.length > 0 ? (
              userPosts.map((post) => (
                <Box key={post.post_id} className="post-item">
                  <Typography variant="subtitle1" className="post-title">
                    {post.title}
                  </Typography>
                  <Typography variant="body2" className="post-content">
                    {post.content}
                  </Typography>
                </Box>
              ))
            ) : (
              <Typography variant="body2">No posts available.</Typography>
            )}
          </Box>
        </DialogContent>
      </Dialog>
    </Box>
  );
};

export default FriendsList;
