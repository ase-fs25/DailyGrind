import { Box, Typography, Card } from '@mui/material';

import Header from '../common/Header';
import { mockPosts } from '../../mockData/mockPosts';
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';
import { useEffect } from 'react';
import { fetchAuthSession } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import userStore from '../../stores/userStore';

const Feed = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Only fetch user information when not already in userStore
    console.log(userStore);
    if (userStore.getUser().userId === '') {
      (async () => {
        try {
          const session = await fetchAuthSession();
          const authToken = session.tokens?.accessToken.toString();
          console.log('auth token: ', authToken);

          const res = await fetch('http://localhost:8080/users/me', {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${authToken}`,
            },
          });

          if (res.ok) {
            const text = await res.text();

            if (text) {
              const userData = JSON.parse(text);
              console.log('User exists:', userData);

              userStore.setUser({
                userId: userData.userId,
                email: userData.email,
                firstName: userData.firstName,
                lastName: userData.lastName,
                birthday: userData.birthday,
                location: userData.location,
                jobs: userData.jobs || [],
                education: userData.education || [],
              });
            } else {
              console.log('User authenticated but not registered');
              navigate('/registration', { replace: true });
            }
          }
        } catch (e) {
          console.error('post‑auth check failed', e);
        }
      })();
    }
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
