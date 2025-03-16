import { Box, Typography, Card, CardContent, Avatar } from '@mui/material';
import Header from '../common/Header';
import { mockFriends } from '../../mockData/mockFriends';

import '../../styles/components/screens/screen.css';

const Friends = () => {
  return (
    <Box className="screen-container">
      <Header />
      <Box className="screen-content" sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
          Friends
        </Typography>
        {mockFriends.map((friend) => (
          <Card
            key={friend.userId}
            sx={{
              display: 'flex',
              alignItems: 'center',
              width: '100%',
              maxWidth: 600,
              mb: 2,
              p: 1,
              boxShadow: 3,
            }}
          >
            <Avatar src={friend.profilePicture} sx={{ width: 64, height: 64, ml: 2, mr: 2 }} />
            <CardContent>
              <Typography variant="h6">{friend.username}</Typography>
            </CardContent>
          </Card>
        ))}
      </Box>
    </Box>
  );
};

export default Friends;
