import { Box, Typography, Card } from '@mui/material';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../common/Header';
import userStore from '../../stores/userStore';
import { loginUser } from '../../helpers/loginHelpers';
import { getAuthToken } from '../../helpers/authHelper';
import { requestNotificationPermission, subscribeUserToPush } from '../../helpers/pushNotificationHelpers';

import { mockPosts as initialMockPosts } from '../../mockData/mockPosts';

import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';

type Comment = { text: string };
type LocalPost = (typeof initialMockPosts)[0] & { comments: Comment[] };

const Feed = () => {
  const navigate = useNavigate();
  const initialized = useRef(false);
  const [posts, setPosts] = useState<LocalPost[]>(initialMockPosts.map((p) => ({ ...p, comments: [] })));
  const [newComments, setNewComments] = useState<{ [key: string]: string }>({});

  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;

    // Using mockPosts by default
    // TODO: Replace this with API call to fetch posts from backend
    // fetch('/api/posts')
    //   .then(res => res.json())
    //   .then(data => setPosts(data));

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
          if (permission === 'granted') {
            return subscribeUserToPush();
          }
          return null;
        })
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
            headers: { Authorization: `Bearer ${authToken}` },
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

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${date.getFullYear()}`;
  };

  const handleLike = (postId: string) => {
    setPosts((prev) =>
      prev.map((post) =>
        post.postId === postId
          ? {
              ...post,
              isLiked: !post.isLiked,
              likeCount: post.isLiked ? post.likeCount - 1 : post.likeCount + 1,
            }
          : post,
      ),
    );

    // TODO:  Send to backend
    // fetch(`/api/posts/${postId}/like`, { method: 'POST', headers: { Authorization: `Bearer ${authToken}` } });
  };

  const handleCommentChange = (postId: string, value: string) => {
    setNewComments((prev) => ({ ...prev, [postId]: value }));
  };

  const handleAddComment = (postId: string) => {
    const text = newComments[postId];
    if (!text.trim()) return;

    setPosts((prev) =>
      prev.map((post) =>
        post.postId === postId
          ? {
              ...post,
              commentCount: post.commentCount + 1,
              comments: [...post.comments, { text }],
            }
          : post,
      ),
    );

    setNewComments((prev) => ({ ...prev, [postId]: '' }));

    // TODO: Send comment to backend
    // fetch(`/api/posts/${postId}/comments`, {
    //   method: 'POST',
    //   headers: {
    //     'Content-Type': 'application/json',
    //     Authorization: `Bearer ${authToken}`,
    //   },
    //   body: JSON.stringify({ text }),
    // });
  };

  return (
    <Box className="screen-container">
      <Header />
      <Box className="feed-content">
        <Box className="feed-grid">
          {posts.map((post) => (
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

                <div className="post-actions">
                  <button
                    className={`like-button ${post.isLiked ? 'liked' : ''}`}
                    onClick={() => handleLike(post.postId)}
                  >
                    {post.likeCount}
                  </button>
                  <span className="comment-count">{post.commentCount}</span>
                </div>

                <div className="post-comments">
                  {post.comments.map((comment, idx) => (
                    <Typography key={idx} className="comment-text" variant="body2">
                      {comment.text}
                    </Typography>
                  ))}
                </div>

                <div className="comment-input">
                  <input
                    type="text"
                    placeholder="Add a comment..."
                    value={newComments[post.postId] || ''}
                    onChange={(e) => handleCommentChange(post.postId, e.target.value)}
                  />
                  <button onClick={() => handleAddComment(post.postId)}>Post</button>
                </div>
              </Card>
            </Box>
          ))}
        </Box>
      </Box>
    </Box>
  );
};

export default Feed;
