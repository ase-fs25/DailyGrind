import { Box, Typography, Card, CircularProgress, IconButton } from '@mui/material';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import CommentIcon from '@mui/icons-material/Comment';

import Header from '../common/Header';

import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/feed.css';
import postsStore from '../../stores/postsStore';
import { useEffect, useState } from 'react';
import { FeedPost, Post, PostComments } from '../../types/post';
import userStore from '../../stores/userStore';
import { formatDate, getCommentsForPost, likePost, unlikePost } from '../../helpers/postHelper';
import CommentsPopup from '../common/CommentsPopup';

const Feed = () => {
  const [feedPosts, setFeedPosts] = useState<FeedPost[]>(postsStore.getFeedPosts());
  const [loading, setLoading] = useState(false);
  const [openComments, setOpenComments] = useState(false);
  const [currentComments, setCurrentComments] = useState<PostComments[]>([]);

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

  const handleLike = async (post: FeedPost) => {
    try {
      if (post.post.isLiked) {
        await unlikePost(post.post.postId);
      } else {
        await likePost(post.post.postId);
      }

      const existing = feedPosts.find((fp) => fp.post.postId === post.post.postId);
      if (!existing) return;

      const updatedPost: Post = {
        ...existing.post,
        isLiked: !existing.post.isLiked,
        likeCount: existing.post.likeCount + (existing.post.isLiked ? -1 : 1),
      };

      postsStore.updateFeedPost(post.post.postId, { ...post, post: updatedPost });

      setFeedPosts((fps) => fps.map((fp) => (fp.post.postId === post.post.postId ? { ...fp, post: updatedPost } : fp)));
    } catch (err) {
      console.error('Error toggling like:', err);
    }
  };

  const handleCommentsClick = async (postId: string) => {
    const fetchedComments = await getCommentsForPost(postId);

    if (fetchedComments) {
      setCurrentComments(fetchedComments);
    }

    setOpenComments(true);
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
                <div className="like-wrapper">
                  <Typography variant="body1" className="like-count">
                    {post.post.likeCount}
                  </Typography>
                  <IconButton
                    edge="end"
                    onClick={() => handleLike(post)}
                    aria-label="like"
                    sx={{ width: '36px', height: '36px' }}
                    color="secondary"
                  >
                    {post.post.isLiked ? <FavoriteIcon /> : <FavoriteBorderIcon />}
                  </IconButton>
                  <IconButton
                    edge="end"
                    onClick={() => handleCommentsClick(post.post.postId)}
                    aria-label="comment"
                    sx={{ width: '36px', height: '36px', marginLeft: '12px;', marginTop: '2px' }}
                    color="secondary"
                  >
                    <CommentIcon />
                  </IconButton>
                </div>
              </Card>
              <CommentsPopup
                open={openComments}
                onClose={() => setOpenComments(false)}
                post={post}
                comments={currentComments}
              />
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
