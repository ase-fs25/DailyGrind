import { useState } from "react";
import { Box, Typography, Card, IconButton } from "@mui/material";
import Header from "../common/Header";
import DeleteIcon from "@mui/icons-material/Delete";

import { mockPersonalPosts } from "../../mockData/mockPersonalPosts";
import "../../styles/components/screens/screen.css";
import "../../styles/components/screens/posts.css";

const Posts = () => {
  const [posts, setPosts] = useState(
    mockPersonalPosts.sort((a, b) => {
      return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
    })
  );

  const handleDelete = (postId: string) => {
    // TODO Here we should delete the post with the given postId in the backend
    setPosts(posts.filter((post) => post.post_id !== postId));
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return `${date.getDate().toString().padStart(2, "0")}-${(
      date.getMonth() + 1
    )
      .toString()
      .padStart(2, "0")}-${date.getFullYear()}`;
  };

  return (
    <Box className="screen-container">
      <Header />
      <Box className="post-content">
        <Box className="post-grid">
          {posts.map((post) => (
            <Box key={post.post_id} className="post-item">
              <Card className="personal-post-card">
                <div className="personal-post-header">
                  <Typography variant="h6" className="personal-post-title">
                    {post.title} - {formatDate(post.timestamp)}
                  </Typography>
                  <IconButton
                    edge="end"
                    onClick={() => handleDelete(post.post_id)}
                    aria-label="delete"
                  >
                    <DeleteIcon />
                  </IconButton>
                </div>
                <Typography variant="body1" className="personal-post-content">
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

export default Posts;
