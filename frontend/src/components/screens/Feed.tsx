import { Box, Typography, Card } from '@mui/material';

import Header from '../common/Header';
import { mockPosts } from '../../mockData/mockPosts';
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';
import { useEffect } from 'react';
import { fetchAuthSession } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';

const Feed = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // TODO Only check this once when the user is logged in
    (async () => {
      try {
        const session = await fetchAuthSession();
        const authToken = session.tokens?.accessToken.toString();

        const res = await fetch('http://localhost:8080/users/me', {
          headers: { Authorization: 'Bearer ' + authToken! },
        });
        const { exists } = await res.json();

        if (exists) {
          // TODO Put user information in profile storage (with the new setters)
          console.log('User exists');
        } else {
          navigate('/registration', { replace: true });
        }
      } catch (e) {
        console.error('post‑auth check failed', e);
      }
    })();
  }, [navigate]);

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
            <Box key={post.post_id} className="feed-item">
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
