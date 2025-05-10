import { Box, Typography, Card } from '@mui/material';

import Header from '../common/Header';

import { mockPosts } from '../../mockData/mockPosts';

import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';

const Feed = () => {
  console.log('Feed component rendered');
  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${date.getFullYear()}`;
  };

  return (
    <Box className="screen-container">
      <Header />
      <Box className="feed-content">
        <Box className="feed-grid">
          {mockPosts.map((post) => (
            <Box key={post.postId} className="feed-item">
              <Card className="post-card">
                <div className="post-card-header">
                  <Typography variant="h6" className="post-title">
                    {post.title}
                  </Typography>
                  <Typography variant="subtitle2" className="post-timestamp">
                    {formatDate(post.timestamp)}
                  </Typography>
                </div>

                <Typography variant="body1" className="post-content">
                  {post.content}
                </Typography>
              </Card>
            </Box>
          ))}
        </Box>
      </Box>
    </Box>
  );
};

export default Feed;
