import { Box, Typography, Card, CircularProgress, Avatar } from '@mui/material';

import Header from '../common/Header';

import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';
import postsStore from '../../stores/postsStore';
import { useEffect, useState } from 'react';
import { FeedPost } from '../../types/post';
import userStore from '../../stores/userStore';

const Feed = () => {
  const [feedPosts, setFeedPosts] = useState<FeedPost[]>(postsStore.getFeedPosts());
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchPosts = () => {
      setLoading(true);

      const timeoutId = setTimeout(() => {
        setFeedPosts(postsStore.getFeedPosts());
        setLoading(false);
        userStore.setFeedHasLoaded(true);
      }, 1500);

      return () => clearTimeout(timeoutId);
    };

    if (!userStore.getFeedHasLoaded()) {
      fetchPosts();
    }
  }, []);

  const formatDate = (timestamp: string) => {
    const ts = Number(timestamp);
    const ms = timestamp.length === 10 ? ts * 1000 : ts;

    const date = new Date(ms);
    if (isNaN(date.getTime())) {
      return 'Invalid date';
    }
    return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${date.getFullYear()}`;
  };

  if (loading) {
    return (
      <Box className="screen-container">
        <Header />
        <Box className="feed-content" display="flex" justifyContent="center" alignItems="center" height="100vh">
          <CircularProgress style={{ color: '#9c27b0' }} />
        </Box>
      </Box>
    );
  }

  return (
    <Box className="screen-container">
      <Header />
      <Box className="feed-content">
        <Box className="feed-grid">
          {feedPosts.map((post) => (
            <Box key={post.post.postId} className="feed-item">
              <Card className="post-card">
                <div className="post-card-header">
                  <div className="post-title-wrapper">
                    <Avatar
                      src={post.user.profilePictureUrl}
                      alt={`${post.user.firstName} ${post.user.lastName}`}
                      sx={{ width: 50, height: 50, boxShadow: '0 4px 8px rgba(0,0,0,0.1)', mr: '8px' }}
                    />
                    <Typography variant="h6" className="post-title">
                      {post.post.title}
                    </Typography>
                    <Typography className="post-user">by {post.user.firstName + ' ' + post.user.lastName}</Typography>
                  </div>

                  <Typography variant="subtitle2" className="post-timestamp">
                    {formatDate(post.post.timestamp)}
                  </Typography>
                </div>

                <Typography variant="body1" className="post-content">
                  {post.post.content}
                </Typography>
              </Card>
            </Box>
          ))}
          {feedPosts.length > 0 && (
            <Typography className="sub-feed-message">Expand your network to see more posts!</Typography>
          )}
          {feedPosts.length === 0 && (
            <Typography className="sub-feed-message">
              Oh no, you do not have any friends yet. Expand your network to see more posts!
            </Typography>
          )}
        </Box>
      </Box>
    </Box>
  );
};

export default Feed;
