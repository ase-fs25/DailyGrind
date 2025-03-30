import { Box, Card, CardContent, Avatar, Typography } from '@mui/material';
import { mockFriends } from '../../mockData/mockFriends';
import '../../styles/components/friends/friendList.css';

const FriendsList = () => {
  // TODO: Replace 'mockFriends' with a backend API call to fetch the user's friends list.
  return (
    <Box className="friends-list-container">
      {mockFriends.map((friend) => (
        <Card key={friend.userId} className="friend-card">
          <Avatar src={friend.profilePicture} className="friend-avatar" />
          <CardContent className="friend-card-content">
            <Typography variant="h6" className="friend-username">
              {friend.username}
            </Typography>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

export default FriendsList;
