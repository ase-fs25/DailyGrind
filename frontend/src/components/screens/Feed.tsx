import { Box, Typography, Card } from '@mui/material';
import { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../common/Header';
import userStore from '../../stores/userStore';
import { loginUser } from '../../helpers/loginHelpers';
import { getAuthToken } from '../../helpers/authHelper';
import { requestNotificationPermission, subscribeUserToPush } from '../../helpers/pushNotificationHelpers';

import { mockPosts } from '../../mockData/mockPosts';

import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';

const Feed = () => {
  const navigate = useNavigate();
  const initialized = useRef(false);

  // TODO This makes the screen flashing again
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;

    console.log('Service Worker Registration');
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker
        .register('/service-worker.js')
        .then(() => {
          if (Notification.permission !== 'denied') {
            return requestNotificationPermission();
          }
          return null;
        })
        .then((permission) => {
          console.log('Permission result: ', permission);
          if (permission === 'granted') {
            return subscribeUserToPush();
          }
          return null;
        })
        .then(() => {})
        .catch((error) => console.error('Service worker or notification error:', error));
    }
  }, []);

  useEffect(() => {
    if (userStore.getUser().userId === '') {
      (async () => {
        try {
          const authToken = await getAuthToken();

          const userInfo = await fetch('http://localhost:8080/users/me', {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${authToken}`,
            },
          });

          if (userInfo.ok && authToken) {
            const userInfoRaw = await userInfo.text();

            if (userInfoRaw) {
              loginUser(userInfoRaw, authToken);
            } else {
              navigate('/registration', { replace: true });
            }
          }
        } catch (e) {
          console.error('post‑auth check failed', e);
        }
      })();
    }
  }, [navigate]);

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
