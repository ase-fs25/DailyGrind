import { Dialog, DialogTitle, DialogContent, IconButton, Box, Typography } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { Post } from '../../types/post';
import { UserProfile } from '../../helpers/friendsHelper';
import { removeFriend } from '../../helpers/friendsHelper';
import '../../styles/components/common/friendPopup.css';

interface FriendPopupProps {
  open: boolean;
  onClose: () => void;
  user: UserProfile | null;
  posts: Post[];
}


const FriendPopup = ({ open, onClose, user, posts }: FriendPopupProps) => {
  if (!user) return null;

  const handleRemoveFriend = async () => {
    try {
      await removeFriend(user.userId);
      onClose(); 
    } catch (error) {
      console.error('Failed to remove friend:', error);
      alert('Failed to remove friend. Please try again.');
    }
  };

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
        {user.firstName + ' ' + user.lastName}
        <IconButton onClick={onClose} className="close-button">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent className="popup-content">
        <Box className="friend-profile-section">
          <Typography variant="h6" className="profile-name">
            {user.firstName} {user.lastName}
          </Typography>
          <Typography variant="body1" className="profile-info">
            Location: {user.location || 'N/A'}
          </Typography>
          <Typography variant="body1" className="profile-info">
            Education: TODO
          </Typography>
          <Typography variant="body1" className="profile-info">
            Work Experience: TODO
          </Typography>
        </Box>

        <Box className="posts-section">
          <Typography variant="h6" className="posts-heading">
            Posts
          </Typography>
          {posts.length > 0 ? (
            posts.map((post) => (
              <Box key={post.postId} className="post-item">
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

        <Box className="remove-friend-wrapper">
          <button className="remove-friend-button" onClick={handleRemoveFriend}>
            Remove Friend
          </button>
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default FriendPopup;
