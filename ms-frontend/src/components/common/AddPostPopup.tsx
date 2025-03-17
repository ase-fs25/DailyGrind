import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, Button, DialogActions, TextField } from '@mui/material';

import '../../styles/components/common/addPostPopup.css';
import { mockPersonalPosts } from '../../mockData/mockPersonalPosts';

interface AddPostPopupProps {
  open: boolean;
  onClose: () => void;
}

const AddPostPopup: React.FC<AddPostPopupProps> = ({ open, onClose }) => {
  const [postTitle, setPostTitle] = useState('');
  const [postContent, setPostContent] = useState('');

  const isFormValid = postTitle.trim().length > 0 && postContent.trim().length > 0;

  const addPost = () => {
    // TODO Here we should add the post to the backend
    if (!isFormValid) return;

    mockPersonalPosts.push({
      post_id: Math.random().toString(36).substr(2, 9),
      user_id: '1',
      title: postTitle,
      content: postContent,
      timestamp: new Date().toISOString(),
    });

    setPostTitle('');
    setPostContent('');
    onClose();
  };
  return (
    <Dialog
      open={open}
      onClose={onClose}
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
      <DialogTitle className="add-post-header">Add your daily Post</DialogTitle>
      <DialogContent className="add-post-content">
        <TextField
          label="Post Title"
          variant="outlined"
          fullWidth
          value={postTitle}
          onChange={(e) => setPostTitle(e.target.value)}
          className="post-title-field"
        />
        <TextField
          label="Post Content"
          variant="outlined"
          fullWidth
          multiline
          rows={4}
          value={postContent}
          onChange={(e) => setPostContent(e.target.value)}
          className="post-text-field"
        />
      </DialogContent>
      <DialogActions className="add-post-actions">
        <Button onClick={onClose} color="secondary">
          Cancel
        </Button>
        <Button
          onClick={addPost}
          color="primary"
          variant="contained"
          disabled={!isFormValid}
          sx={{
            backgroundColor: '#7b1fa2',
            '&:hover': { backgroundColor: '#9c27b0' },
          }}
        >
          Add Post
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddPostPopup;
