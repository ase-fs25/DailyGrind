// import { useState } from 'react';
// import { Box, Typography, Card, IconButton } from '@mui/material';
// import Header from '../common/Header';
// import DeleteIcon from '@mui/icons-material/Delete';
//
// import { mockPersonalPosts } from '../../mockData/mockPersonalPosts';
// import '../../styles/components/screens/screen.css';
// import '../../styles/components/screens/posts.css';
//
// const Posts = () => {
//   const [posts, setPosts] = useState(
//     mockPersonalPosts.sort((a, b) => {
//       return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
//     }),
//   );
//
//   const handleDelete = (postId: string) => {
//     // TODO Here we should delete the post with the given postId in the backend
//     setPosts(posts.filter((post) => post.postId !== postId));
//   };
//
//   const formatDate = (timestamp: string) => {
//     const date = new Date(timestamp);
//     return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
//       .toString()
//       .padStart(2, '0')}-${date.getFullYear()}`;
//   };
//
//   return (
//     <Box className="screen-container">
//       <Header />
//       <Box className="post-content">
//         <Box className="post-grid">
//           {posts.map((post) => (
//             <Box key={post.postId} className="post-item">
//               <Card className="personal-post-card">
//                 <div className="personal-post-header">
//                   <Typography variant="h6" className="personal-post-title">
//                     {post.title} - {formatDate(post.timestamp)}
//                   </Typography>
//                   <IconButton edge="end" onClick={() => handleDelete(post.postId)} aria-label="delete">
//                     <DeleteIcon />
//                   </IconButton>
//                 </div>
//                 <Typography variant="body1" className="personal-post-content">
//                   {post.content}
//                 </Typography>
//               </Card>
//             </Box>
//           ))}
//         </Box>
//       </Box>
//     </Box>
//   );
// };
//
// export default Posts;

import { useState, useEffect } from 'react';
import { Box, Typography, Card, IconButton, CircularProgress, Button } from '@mui/material';
import Header from '../common/Header';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { getUserPosts, deletePost } from '../../helpers/postHelper';
import { Post } from '../../types/post';
import AddPostPopup from '../common/AddPostPopup';
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/posts.css';

const Posts = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [openDialog, setOpenDialog] = useState(false);

  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const fetchedPosts = await getUserPosts();

      const sortedPosts = [...fetchedPosts].sort((a, b) => {
        return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
      });

      setPosts(sortedPosts);
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
      setPosts(posts.filter((post) => post.postId !== postId));
    } catch (err) {
      console.error('Error deleting post:', err);
      setError('Failed to delete post. Please try again later.');
    }
  };

  const handlePostCreated = (newPost: Post) => {
    setPosts((prevPosts) => [newPost, ...prevPosts]);
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
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h4">My Posts</Typography>
          <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={() => setOpenDialog(true)}>
            Add New Post
          </Button>
        </Box>

        {error && (
          <Box mb={3}>
            <Typography color="error">{error}</Typography>
          </Box>
        )}

        {loading ? (
          <Box display="flex" justifyContent="center" alignItems="center" height="60vh">
            <CircularProgress />
          </Box>
        ) : posts.length === 0 ? (
          <Box textAlign="center" my={4}>
            <Typography>No posts found. Create your first post!</Typography>
          </Box>
        ) : (
          <Box className="post-grid">
            {posts.map((post) => (
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

        {/* AddPostPopup  */}
        <AddPostPopup open={openDialog} onClose={() => setOpenDialog(false)} onPostCreated={handlePostCreated} />
      </Box>
    </Box>
  );
};

export default Posts;
