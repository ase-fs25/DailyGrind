import React, { useState } from 'react';
import { Box, Typography, Button } from '@mui/material';
import { mockFriendRequests } from '../../mockData/mockFriendRequests';
import '../../styles/components/friends/friendRequests.css';

const FriendsRequests = () => {
  // TODO: Replace 'mockFriendRequests' with an API call to fetch friend requests.
  const [requests, setRequests] = useState(mockFriendRequests);

  const handleAccept = (username: string) => {
    alert(`Accepted request from ${username}`);
    // TODO: Call backend API to accept friend request for this username.
    // After a successful API call, update the UI.
    setRequests((prev) => prev.filter((req) => req.username !== username));
  };

  const handleDecline = (username: string) => {
    alert(`Declined request from ${username}`);
    // TODO: Call backend API to decline friend request for this username.
    // After a successful API call, update the UI.
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

            {/* Button group with spacing */}
            <Box className="request-buttons">
              <Button variant="outlined" color="success" size="small" onClick={() => handleAccept(request.username)}>
                Accept
              </Button>
              <Button variant="outlined" color="error" size="small" onClick={() => handleDecline(request.username)}>
                Decline
              </Button>
            </Box>
          </Box>
        ))}
      </Box>
    </Box>
  );
};

export default FriendsRequests;
