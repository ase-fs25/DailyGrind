import { Box, Typography, Card } from '@mui/material';
import { useEffect } from 'react';

import Header from '../common/Header';
import { mockPosts } from '../../mockData/mockPosts';
import {mockVapidKeys} from "../../mockData/mockVapidKeys";
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';

const Feed = () => {

    useEffect(() => {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/service-worker.js')
                .then(() => {
                    if (Notification.permission !== 'denied') {
                        return requestNotificationPermission();
                    }
                    return undefined; // Permission was denied
                })
                .then((permission) => {
                    if (permission === 'granted') {
                        return subscribeUserToPush();
                    }
                })
                .catch(error => console.error('Service worker or notification error:', error));
        }
    }, []);

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
    };

    const subscribeUserToPush = async () => {
        try {
            const registration = await navigator.serviceWorker.ready;

            let subscription: PushSubscription | null = await registration.pushManager.getSubscription();

            if (subscription) {
                console.log('Received existing PushSubscription: ', JSON.stringify(subscription));
            } else {
                const subscribeOptions = {
                    userVisibleOnly: true,
                    applicationServerKey: mockVapidKeys.publicKey,
                };
                subscription = await registration.pushManager.subscribe(subscribeOptions);
                console.log('Created new PushSubscription: ', JSON.stringify(subscription));
            }
            return subscription;
        } catch (error) {
            console.error('Error subscribing to push:', error);
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
