import { Box, Typography, Card } from '@mui/material';
import { useEffect } from 'react';

import Header from '../common/Header';
import { mockPosts } from '../../mockData/mockPosts';
//import {mockVapidKeys} from "../../mockData/mockVapidKeys";
import { requestNotificationPermission, subscribeUserToPush} from "../../helpers/pushNotificationHelpers";
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';

const Feed = () => {
    console.log('Feed component rendering');
    alert("test");
    useEffect(() => {
        console.log('Service Worker Registration');
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/service-worker.js')
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
                }).then(subscription => {
                    console.log('Subscription Object: ', subscription);
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
