import { Box, Typography, Card } from '@mui/material';
import { useEffect } from 'react';

import Header from '../common/Header';
import { mockPosts } from '../../mockData/mockPosts';
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';

const Feed = () => {

    useEffect(() => {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/serviceWorker.ts')
                .then(() => {
                    if (Notification.permission !== 'denied') {
                        return requestNotificationPermission();
                    }
                })
                .then(permission => {
                    if (permission === 'granted') {
                        return subscribeUserToPush();
                    }
                })
                .catch(error => console.error('Service worker or notification error:', error));
        }
    }, []);

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${date.getFullYear()}`;
  };

  const registerServiceWorker = async () => {
      if('serviceWorker' in navigator) {
          try {
              await navigator.serviceWorker.register('/serviceWorker.ts');
          } catch (error) {
              console.error('Failed to register service worker', error);
          }
      }
  }

  const requestNotificationPermission = async (): Promise<string> => {
      return new Promise(function (resolve, reject) {
          const permissionResult = Notification.requestPermission(function (result) {
              return resolve(result);
          });

          if (permissionResult) {
              permissionResult.then(resolve, reject);
          }
      }).then(function (permissionResult) {
          if (permissionResult !== 'granted') {
              throw new Error("We weren't granted permission.");
          }
          return permissionResult;
      });
  }

    const subscribeUserToPush = () => {
        return navigator.serviceWorker.ready
            .then(function (registration) {
                const subscribeOptions = {
                    userVisibleOnly: true,
                    applicationServerKey: 'Public VAPID key',
                };

                return registration.pushManager.subscribe(subscribeOptions);
            })
            .then(function (pushSubscription) {
                console.log(
                    'Received PushSubscription: ',
                    JSON.stringify(pushSubscription),
                );
                return pushSubscription;
            });
    }

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
