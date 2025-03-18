import React, { useState } from 'react';
import { Box, Typography, Button } from '@mui/material';
import { mockFriendRequests } from '../../mockData/mockFriendRequests';
import '../../styles/components/screens/friendRequests.css';

const FriendsRequests: React.FC = () => {
  // Store requests in local state so we can remove them on accept/decline
  const [requests, setRequests] = useState(mockFriendRequests);

  const handleAccept = (username: string) => {
    alert(`Accepted request from ${username}`);
    // Remove this user from the list
    setRequests((prev) => prev.filter((req) => req.username !== username));
  };

  const handleDecline = (username: string) => {
    alert(`Declined request from ${username}`);
    // Remove this user from the list
    setRequests((prev) => prev.filter((req) => req.username !== username));
  };

  return (
    <Box className="requests-container">
      <Typography variant="h6" className="requests-title">
        Friend Requests
      </Typography>
      <Box className="requests-list">
        {requests.map((request, index) => (
          <Box key={index} className="request-item">
            <Typography variant="subtitle1" className="request-username">
              {request.username}
            </Typography>
            <Button
              variant="contained"
              color="success"
              size="small"
              className="request-button accept"
              onClick={() => handleAccept(request.username)}
            >
              Accept
            </Button>
            <Button
              variant="contained"
              color="error"
              size="small"
              className="request-button decline"
              onClick={() => handleDecline(request.username)}
            >
              Decline
            </Button>
          </Box>
        ))}
      </Box>
    </Box>
  );
};

export default FriendsRequests;
