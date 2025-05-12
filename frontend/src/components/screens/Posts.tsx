import { useState, useEffect } from 'react';
import { Box, Typography, Card, IconButton, CircularProgress, Divider } from '@mui/material';
import Header from '../common/Header';
import DeleteIcon from '@mui/icons-material/Delete';
import TurnedInIcon from '@mui/icons-material/TurnedIn';
import TurnedInNotIcon from '@mui/icons-material/TurnedInNot';
import {
  getUserPosts,
  deletePost,
  getUserPinnedPosts,
  unpinPost,
  pinPost,
  getCommentsForPost,
} from '../../helpers/postHelper';
import postsStore from '../../stores/postsStore';
import '../../styles/components/screens/screen.css';
import '../../styles/components/screens/posts.css';
import { FeedPost, Post, PostComments } from '../../types/post';
import CommentIcon from '@mui/icons-material/Comment';
import CommentsPopup from '../common/CommentsPopup';
import userStore from '../../stores/userStore';

const Posts = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [personalPosts, setPersonalPosts] = useState<Post[]>(postsStore.getPosts());
  const [pinnedPosts, setPinnedPosts] = useState<Post[]>([]);
  const [openComments, setOpenComments] = useState(false);
  const [currentComments, setCurrentComments] = useState<PostComments[]>([]);
  const user = userStore.getUser();
  const [tempFeedPost, setTempFeedPost] = useState<FeedPost>({
    post: {
      postId: '',
      title: '',
      content: '',
      timestamp: '',
      commentCount: 0,
      isLiked: false,
      isPinned: false,
      likeCount: 0,
    },
    user: user,
  });

  useEffect(() => {
    if (postsStore.getPosts().length > 0) {
      setLoading(false);
    } else {
      fetchPosts();
      fetchPinnedPosts();
    }
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const fetchedPosts = await getUserPosts();

      const sortedPosts = [...fetchedPosts].sort((a, b) => {
        return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
      });

      postsStore.setPosts(sortedPosts);
      setPersonalPosts(sortedPosts);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch posts:', err);
      setError('Failed to load posts. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (postId: string) => {
    try {
      await deletePost(postId);

      const isPersonal = personalPosts.some((pinnedPost) => pinnedPost.postId === postId);
      if (isPersonal) {
        postsStore.removePost(postId);
        setPersonalPosts((posts) => posts.filter((p) => p.postId !== postId));
      }
      const isPinned = pinnedPosts.some((pinnedPost) => pinnedPost.postId === postId);
      if (isPinned) {
        postsStore.removePinnedPost(postId);
        setPinnedPosts((posts) => posts.filter((p) => p.postId !== postId));
      }
    } catch (err) {
      console.error('Error deleting post:', err);
      setError('Failed to delete post. Please try again later.');
    }
  };

  const fetchPinnedPosts = async () => {
    try {
      setLoading(true);
      const fetchedPinnedPosts = await getUserPinnedPosts();

      const sortedPinnedPosts = [...fetchedPinnedPosts].sort((a, b) => {
        return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
      });

      postsStore.setPinnedPosts(sortedPinnedPosts);
      setPinnedPosts(sortedPinnedPosts);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch pinned posts:', err);
      setError('Failed to fetch pinned posts. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handlePinPost = async (post: Post) => {
    const updatedPost = { ...post, isPinned: true };
    try {
      await pinPost(post.postId);
      postsStore.addPinnedPost(updatedPost);
      postsStore.updatePost(post.postId, updatedPost);
      setPinnedPosts((pinned) => [...pinned, updatedPost]);
    } catch (err) {
      console.error('Error unpinning post:', err);
      setError('Failed to unpin post. Please try again later.');
    }
  };

  const handleUnpinPost = async (pinnedPost: Post) => {
    const updatedPost = { ...pinnedPost, isPinned: false };
    try {
      await unpinPost(pinnedPost.postId);
      postsStore.removePinnedPost(pinnedPost.postId);
      postsStore.updatePost(pinnedPost.postId, updatedPost);
      setPinnedPosts((posts) => posts.filter((p) => p.postId !== pinnedPost.postId));
    } catch (err) {
      console.error('Error pinning post:', err);
      setError('Failed to pin post. Please try again later.');
    }
  };

  const handleCommentsClick = async (post: Post) => {
    const fetchedComments = await getCommentsForPost(post.postId);

    if (fetchedComments) {
      setCurrentComments(fetchedComments);
    }

    const postObject: FeedPost = {
      post: post,
      user: user,
    };
    setTempFeedPost(postObject);
    setOpenComments(true);
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(Number(timestamp));
    return `${date.getDate().toString().padStart(2, '0')}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-${date.getFullYear()}`;
  };

  return (
    <Box className="screen-container">
      <Header />
      <Box className="post-content">
        {error && (
          <Box mb={3}>
            <Typography color="error">{error}</Typography>
          </Box>
        )}
        {loading ? (
          <Box display="flex" justifyContent="center" alignItems="center" height="60vh">
            <CircularProgress />
          </Box>
        ) : postsStore.getPosts().length === 0 ? (
          <Box textAlign="center" my={4}>
            <Typography>No posts found. Create your first post!</Typography>
          </Box>
        ) : (
          <>
            <Box className="post-grid">
              {postsStore.getPinnedPosts().length > 0 && <div>Pinned posts:</div>}
              {postsStore.getPinnedPosts().map((pinnedPost) => (
                <Box key={pinnedPost.postId} className="post-item">
                  <Card className="personal-post-card">
                    <div className="personal-post-card-header">
                      <Typography variant="h6" className="personal-post-title">
                        {pinnedPost.title}
                      </Typography>
                      <Typography variant="subtitle2" className="personal-post-timestamp">
                        {formatDate(pinnedPost.timestamp)}
                      </Typography>
                    </div>
                    <Typography variant="body1" className="post-content">
                      {pinnedPost.content}
                    </Typography>
                    <div className="post-like-wrapper">
                      <Typography variant="body1" className="post-like-count">
                        {pinnedPost.likeCount} Likes
                      </Typography>
                      <IconButton
                        edge="end"
                        onClick={() => handleCommentsClick(pinnedPost)}
                        aria-label="comment"
                        sx={{ width: '36px', height: '36px', marginLeft: '12px;', marginTop: '2px' }}
                        color="secondary"
                      >
                        <CommentIcon />
                      </IconButton>
                    </div>
                  </Card>
                  <div className="pin-icon-wrapper">
                    <IconButton
                      edge="end"
                      onClick={() => handleUnpinPost(pinnedPost)}
                      aria-label="delete"
                      color="secondary"
                    >
                      <TurnedInIcon />
                    </IconButton>
                  </div>
                </Box>
              ))}
            </Box>
            {postsStore.getPinnedPosts().length > 0 && (
              <Divider sx={{ marginTop: '16px', marginBottom: '10px', bgcolor: 'secondary.light' }} />
            )}
            <Box className="post-grid">
              <div>All posts:</div>
              {postsStore.getPosts().map((post) => (
                <Box key={post.postId} className="post-item">
                  <Card className="personal-post-card">
                    <div className="personal-post-card-header">
                      <Typography variant="h6" className="personal-post-title">
                        {post.title}
                      </Typography>
                      <Typography variant="subtitle2" className="personal-post-timestamp">
                        {formatDate(post.timestamp)}
                      </Typography>
                    </div>
                    <Typography variant="body1" className="post-content">
                      {post.content}
                    </Typography>
                    <div className="post-like-wrapper">
                      <Typography variant="body1" className="post-like-count">
                        {post.likeCount} Likes
                      </Typography>
                      <IconButton
                        edge="end"
                        onClick={() => handleCommentsClick(post)}
                        aria-label="comment"
                        sx={{ width: '36px', height: '36px', marginLeft: '12px;', marginTop: '2px' }}
                        color="secondary"
                      >
                        <CommentIcon />
                      </IconButton>
                    </div>
                  </Card>
                  <div className="delete-icon-wrapper">
                    <IconButton
                      edge="end"
                      onClick={() => handleDelete(post.postId)}
                      aria-label="delete"
                      sx={{ width: '36px', height: '36px' }}
                      color="secondary"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </div>
                  <div className="pin-icon-wrapper">
                    {post.isPinned ? (
                      <IconButton
                        edge="end"
                        onClick={() => handleUnpinPost(post)}
                        aria-label="delete"
                        color="secondary"
                      >
                        <TurnedInIcon />
                      </IconButton>
                    ) : (
                      <IconButton
                        edge="end"
                        onClick={() => handlePinPost(post)}
                        aria-label="delete"
                        sx={{ width: '36px', height: '36px' }}
                        color="secondary"
                      >
                        <TurnedInNotIcon />
                      </IconButton>
                    )}
                  </div>
                  <CommentsPopup
                    open={openComments}
                    onClose={() => setOpenComments(false)}
                    post={tempFeedPost}
                    comments={currentComments}
                  />
                </Box>
              ))}
            </Box>
          </>
        )}
      </Box>
    </Box>
  );
};

export default Posts;
