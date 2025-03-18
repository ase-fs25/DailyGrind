import React from "react";
import { Box, Card, CardContent, Avatar, Typography } from "@mui/material";
import { mockFriends } from "../../mockData/mockFriends";

const FriendsList = () => {
  return (
    <Box sx={{ mt: 2 }}>
      {mockFriends.map((friend) => (
        <Card
          key={friend.userId}
          sx={{
            display: "flex",
            alignItems: "center",
            width: "100%",
            maxWidth: 600,
            mb: 2,
            p: 1,
            boxShadow: 3,
          }}
        >
          <Avatar
            src={friend.profilePicture}
            sx={{ width: 64, height: 64, ml: 2, mr: 2 }}
          />
          <CardContent>
            <Typography variant="h6">{friend.username}</Typography>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

export default FriendsList;
