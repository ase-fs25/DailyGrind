import { Button, Card, Dialog, DialogContent, IconButton, TextField, Typography } from '@mui/material';
import { addCommentForPost, deleteCommentForPost, formatDate } from '../../helpers/postHelper';
import '../../styles/components/common/commentsPopup.css';
import { FeedPost, PostComments } from '../../types/post';
import { useEffect, useState } from 'react';
import userStore from '../../stores/userStore';
import DeleteIcon from '@mui/icons-material/Delete';

interface CommentsPopupProps {
  open: boolean;
  post: FeedPost;
  comments: PostComments[];
  onClose: () => void;
}

const CommentsPopup = ({ open, onClose, post, comments }: CommentsPopupProps) => {
  const [newComment, setNewComment] = useState('');
  const [postComments, setPostComments] = useState<PostComments[]>(comments);
  const user = userStore.getUser();

  useEffect(() => {
    setPostComments(comments);
  }, [comments]);

  const handleClose = () => {
    onClose();
  };

  const addComment = async () => {
    const commentObject = {
      comment: {
        commentId: '',
        userId: '',
        content: newComment,
        timestamp: '',
      },
      user: user,
    };

    await addCommentForPost(post.post.postId, newComment);
    setPostComments((prevComments) => [...prevComments, commentObject]);
    setNewComment('');
  };

  const deleteComment = async (postId: string, commentId: string) => {
    await deleteCommentForPost(postId, commentId);
    setPostComments((prev) => prev.filter((comment) => comment.comment.commentId !== commentId));
  };

  const createAuthorPost = () => {
    if (user.userId === post.user.userId) {
      return 'by me'
    } else {
      return `by ${post.user.firstName + ' ' + post.user.lastName}`
    }
  }

  const createAuthorComment = (comment: PostComments) => {
    if (user.userId === comment.comment.userId) {
      return 'From me'
    } else {
      return `From ${comment.user.firstName} ${comment.user.lastName}`
    }
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      className="comments-popup"
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
      <Card className="comment-post-card">
        <div className="comment-post-card-header">
          <div className="comment-post-title-wrapper">
            <Typography variant="h6" className="comment-post-title">
              {post.post.title}
            </Typography>
            <Typography className="comment-post-user">{createAuthorPost()}</Typography>
          </div>

          <Typography variant="subtitle2" className="post-timestamp">
            {formatDate(post.post.timestamp)}
          </Typography>
        </div>

        <Typography variant="body1" className="comment-post-content">
          {post.post.content}
        </Typography>
        <div className="like-wrapper">
          <Typography variant="body1">{post.post.likeCount} Likes</Typography>
        </div>
      </Card>
      <DialogContent className="comment-section">
        {postComments.length === 0 && <Typography className="no-comments">This post has no comments yet.</Typography>}
        {postComments.length > 0 && (
          <div className="comment-content-wrapper">
            {postComments.map((comment) => (
              <div key={comment.comment.commentId} className="single-comment-wrapper">
                <div className="single-comment-content-wrapper">
                  <div>
                    <div className="comment-author">
                      {createAuthorComment(comment)}
                    </div>
                    <div>{comment.comment.content}</div>
                  </div>
                  {user.userId === comment.comment.userId && (
                    <IconButton
                      edge="end"
                      onClick={() => deleteComment(post.post.postId, comment.comment.commentId)}
                      aria-label="delete"
                      sx={{ width: '36px', height: '36px' }}
                      color="secondary"
                    >
                      <DeleteIcon />
                    </IconButton>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </DialogContent>
      <div className="add-comment-wrapper">
          <TextField
            label="Add Comment"
            variant="outlined"
            fullWidth
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            className="add-comment-input"
            color="secondary"
          />
          <div>
            <Button
              variant="contained"
              color="primary"
              fullWidth
              disabled={newComment.length === 0}
              onClick={addComment}
              sx={{
                backgroundColor: '#7b1fa2',
                '&:hover': { backgroundColor: '#9c27b0' },
                fontSize: '10px',
              }}
            >
              Add Comment
            </Button>
          </div>
        </div>
    </Dialog>
  );
};

export default CommentsPopup;
