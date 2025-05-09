// TODO This should be avoided

import { useEffect, useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import { createPost } from '../../helpers/postHelper';
import '../../styles/components/common/addPostPopup.css';
import postStore from '../../stores/postsStore';
import { inspireMeData } from '../../constants/inspireMe';

interface AddPostPopupProps {
  open: boolean;
  onClose: () => void;
}

const AddPostPopup = ({ open, onClose }: AddPostPopupProps) => {
  const [postTitle, setPostTitle] = useState('');
  const [postContent, setPostContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [placeholder, setPlaceholder] = useState('');

  const isFormValid = postTitle.trim().length > 0 && postContent.trim().length > 0;

  useEffect(() => {
    if (open) {
      const random = inspireMeData[Math.floor(Math.random() * inspireMeData.length)];
      setPlaceholder(random);
    }
  }, [open]);

  const handleClose = () => {
    setPostTitle('');
    setPostContent('');
    onClose();
  };

  const addPost = async () => {
    if (!isFormValid) return;

    try {
      const newPost = await createPost(postTitle, postContent);

      postStore.addPost(newPost);

      setPostTitle('');
      setPostContent('');
      onClose();
    } catch (error) {
      console.error('Error creating post:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      className="add-post-popup"
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
      <DialogTitle className="add-post-header">What do you want to post today?</DialogTitle>
      <DialogContent className="add-post-content">
        <TextField
          label="Add Title"
          variant="outlined"
          fullWidth
          value={postTitle}
          onChange={(e) => setPostTitle(e.target.value)}
          sx={{marginTop: '8px'}}
        />
        <TextField
          label="Add Content"
          placeholder={placeholder}
          variant="outlined"
          fullWidth
          multiline
          rows={4}
          value={postContent}
          onChange={(e) => setPostContent(e.target.value)}
        />
      </DialogContent>
      <DialogActions className="add-post-actions">
        <Button onClick={handleClose} color="secondary">
          Cancel
        </Button>
        <Button
          onClick={addPost}
          color="primary"
          variant="contained"
          disabled={!isFormValid || isSubmitting}
          sx={{
            backgroundColor: '#7b1fa2',
            '&:hover': { backgroundColor: '#9c27b0' },
          }}
        >
          {isSubmitting ? 'Creating...' : 'Add Post'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddPostPopup;
