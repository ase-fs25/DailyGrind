import { useEffect, useState } from 'react';
import { Box, Typography, Button } from '@mui/material';
import { fetchIncomingRequests, acceptFriendRequest, declineFriendRequest } from '../../helpers/friendsHelper';
import '../../styles/components/friends/friendRequests.css';
import { User } from '../../types/user';

const FriendsRequests = () => {
  const [requestsBy, setRequestsBy] = useState<User[]>([]);

  useEffect(() => {
    const loadRequests = async () => {
      try {
        const data = await fetchIncomingRequests();
        setRequestsBy(data);
      } catch (error) {
        console.error('Failed to load friend requests:', error);
      }
    };

    loadRequests();
  }, []);

  const handleAccept = async (requestedById: string) => {
    try {
      await acceptFriendRequest(requestedById);
      setRequestsBy((prev) => prev.filter((req) => req.userId !== requestedById));
    } catch (error) {
      console.error('Failed to accept friend request:', error);
    }
  };

  const handleDecline = async (requestedById: string) => {
    try {
      await declineFriendRequest(requestedById);
      setRequestsBy((prev) => prev.filter((req) => req.userId !== requestedById));
    } catch (error) {
      console.error('Failed to decline friend request:', error);
    }
  };

  return (
    <Box className="requests-container">
      <Box className="requests-list">
        {requestsBy.map((requestBy) => (
          <Box key={requestBy.userId} className="request-item">
            <Typography variant="subtitle1" className="request-username">
              {requestBy.firstName} {requestBy.lastName}
            </Typography>
            <Box className="request-buttons">
              <Button variant="outlined" color="success" size="small" onClick={() => handleAccept(requestBy.userId)}>
                Accept
              </Button>
              <Button variant="outlined" color="error" size="small" onClick={() => handleDecline(requestBy.userId)}>
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
