import React from 'react';
import { Dialog, DialogTitle, DialogContent, IconButton, Avatar, Box, Typography } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { PostType } from '../../types/post';
import { Profile } from '../../types/profile';
import '../../styles/components/common/friendPopup.css';

interface FriendPopupProps {
  open: boolean;
  onClose: () => void;
  profile: Profile | null;
  posts: PostType[];
}

const FriendPopup: React.FC<FriendPopupProps> = ({ open, onClose, profile, posts }) => {
  if (!profile) return null;

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="md"
      scroll="paper"
      slotProps={{
        backdrop: { className: 'popup-backdrop' },
        paper: { className: 'popup-dialog-paper' },
      }}
    >
      <DialogTitle className="popup-header">
        {profile.username}
        <IconButton onClick={onClose} className="close-button">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent className="popup-content">
        <Box className="friend-profile-section">
          <Avatar src={profile.profileInfo.profilePicture} alt={profile.username} className="profile-avatar-large" />
          <Typography variant="h6" className="profile-name">
            {profile.username}
          </Typography>
          <Typography variant="body1" className="profile-info">
            Location: {profile.profileInfo.location}
          </Typography>
          <Typography variant="body1" className="profile-info">
            Education: {profile.profileInfo.education}
          </Typography>
          <Typography variant="body1" className="profile-info">
            Work Experience: {profile.profileInfo.workExperience}
          </Typography>
        </Box>

        <Box className="posts-section">
          <Typography variant="h6" className="posts-heading">
            Posts
          </Typography>
          {posts.length > 0 ? (
            posts.map((post) => (
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
            <Typography variant="body2" className="no-posts">
              No posts available.
            </Typography>
          )}
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default FriendPopup;
