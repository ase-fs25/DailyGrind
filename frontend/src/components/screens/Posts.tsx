import { useState, useEffect } from 'react';
import { Box, Typography, Card, IconButton, CircularProgress } from '@mui/material';
import Header from '../common/Header';
import DeleteIcon from '@mui/icons-material/Delete';
import { getUserPosts, deletePost } from '../../helpers/postHelper';
import postsStore from '../../stores/postsStore';
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/posts.css';

const Posts = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (postsStore.getPosts().length > 0) {
      setLoading(false);
    } else {
      fetchPosts();
    }
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const fetchedPosts = await getUserPosts();

      const sortedPosts = [...fetchedPosts].sort((a, b) => {
        return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
      });

      postsStore.setPosts(sortedPosts);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch posts:', err);
      setError('Failed to load posts. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (postId: string) => {
    try {
      await deletePost(postId);
      postsStore.removePost(postId);
    } catch (err) {
      console.error('Error deleting post:', err);
      setError('Failed to delete post. Please try again later.');
    }
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${date.getFullYear()}`;
  };

  return (
    <Box className="screen-container">
      <Header />
      <Box className="post-content">
        {error && (
          <Box mb={3}>
            <Typography color="error">{error}</Typography>
          </Box>
        )}

        {loading ? (
          <Box display="flex" justifyContent="center" alignItems="center" height="60vh">
            <CircularProgress />
          </Box>
        ) : postsStore.getPosts().length === 0 ? (
          <Box textAlign="center" my={4}>
            <Typography>No posts found. Create your first post!</Typography>
          </Box>
        ) : (
          <Box className="post-grid">
            {postsStore.getPosts().map((post) => (
              <Box key={post.postId} className="post-item">
                <Card className="personal-post-card">
                  <div className="personal-post-header">
                    <Typography variant="h6" className="personal-post-title">
                      {post.title} - {formatDate(post.timestamp)}
                    </Typography>
                    <IconButton edge="end" onClick={() => handleDelete(post.postId)} aria-label="delete">
                      <DeleteIcon />
                    </IconButton>
                  </div>
                  <Typography variant="body1" className="personal-post-content">
                    {post.content}
                  </Typography>
                </Card>
              </Box>
            ))}
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default Posts;
