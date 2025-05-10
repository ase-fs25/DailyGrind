import { Dialog, DialogTitle, DialogContent, IconButton, Box, Typography, Button } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { Post } from '../../types/post';
import { UserProfile } from '../../helpers/friendsHelper';
import { removeFriend } from '../../helpers/friendsHelper';
import { UserJob, UserEducation } from '../../types/user';
import JobsSection from './JobsSection';
import EducationSection from './EducationSection';
import '../../styles/components/common/friendPopup.css';

interface FriendPopupProps {
  open: boolean;
  onClose: () => void;
  user: UserProfile | null;
  posts: Post[];
  education: UserEducation[];
  jobs: UserJob[];
}

const FriendPopup = ({ open, onClose, user, posts, education, jobs }: FriendPopupProps) => {
  if (!user) return null;

  const handleRemoveFriend = async () => {
    try {
      await removeFriend(user.userId);
      onClose();
    } catch (error) {
      console.error('Failed to remove friend:', error);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      scroll="paper"
      slotProps={{
        backdrop: { className: 'popup-backdrop' },
        paper: { className: 'popup-dialog-paper' },
      }}
    >
      <DialogTitle className="popup-header">
        <Button className="remove-btn" onClick={handleRemoveFriend}>
          Remove
        </Button>
        <Typography variant="h6" className="popup-title">
          {user.firstName} {user.lastName}
        </Typography>
        <div className="profile-close-button-wrapper">
          <IconButton onClick={onClose} className="profile-close-button">
            <CloseIcon />
          </IconButton>
        </div>
      </DialogTitle>

      <DialogContent className="popup-content">
        <Typography variant="h6" className="location">
          Location: {user.location || 'N/A'}
        </Typography>

        <EducationSection education={education} onChange={() => {}} readOnly />

        <JobsSection jobs={jobs} onChange={() => {}} readOnly />

        <Box className="posts-heading">
          <Typography variant="h6">Posts</Typography>
          {posts.length > 0 ? (
            posts.map((post) => (
              <Box key={post.postId} className="post-box">
                <Typography fontWeight="bold">{post.title}</Typography>
                <Typography>{post.content}</Typography>
              </Box>
            ))
          ) : (
            <Typography className="no-posts">No posts available.</Typography>
          )}
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default FriendPopup;
