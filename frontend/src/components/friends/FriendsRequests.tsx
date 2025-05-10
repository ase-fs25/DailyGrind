import React, { useEffect, useState } from 'react';
import { Box, Typography, Button } from '@mui/material';
import { fetchIncomingRequests, acceptFriendRequest, declineFriendRequest } from '../../helpers/friendsHelper';
import '../../styles/components/friends/friendRequests.css';

interface FriendRequest {
  requestId: string; // SK
  senderId: string;
  firstName: string;
  lastName: string;
}

const FriendsRequests = () => {
  const [requests, setRequests] = useState<FriendRequest[]>([]);

  useEffect(() => {
    const loadRequests = async () => {
      try {
        const data = await fetchIncomingRequests();
        setRequests(data);
      } catch (error) {
        console.error('Failed to load friend requests:', error);
      }
    };

    loadRequests();
  }, []);

  const handleAccept = async (requestId: string) => {
    try {
      await acceptFriendRequest(requestId);
      setRequests((prev) => prev.filter((req) => req.requestId !== requestId));
    } catch (error) {
      console.error('Failed to accept friend request:', error);
    }
  };

  const handleDecline = async (requestId: string) => {
    try {
      await declineFriendRequest(requestId);
      setRequests((prev) => prev.filter((req) => req.requestId !== requestId));
    } catch (error) {
      console.error('Failed to decline friend request:', error);
    }
  };

  return (
    <Box className="requests-container">
      <Box className="requests-list">
        {requests.map((request) => (
          <Box key={request.requestId} className="request-item">
            <Typography variant="subtitle1" className="request-username">
              {request.firstName} {request.lastName}
            </Typography>
            <Box className="request-buttons">
              <Button variant="outlined" color="success" size="small" onClick={() => handleAccept(request.requestId)}>
                Accept
              </Button>
              <Button variant="outlined" color="error" size="small" onClick={() => handleDecline(request.requestId)}>
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
