import React, { useState } from 'react';
import { Box, TextField, Button, Typography } from '@mui/material';
import { mockProfiles } from '../../mockData/mockProfiles';
import '../../styles/components/friends/friendsSearch.css';

const FriendsSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');

  // TODO: In the future, replace the local filter logic with an API call to search for users.
  // Filter profiles that match the search term (using local mock data for now)
  const filteredProfiles = mockProfiles.filter((user) =>
    user.firstName.toLowerCase().startsWith(searchTerm.toLowerCase()),
  );

  return (
    <Box className="search-container">
      {/* Search Input */}
      <TextField
        label="Search People"
        variant="outlined"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="search-input"
      />

      {/* Conditionally render search results only when there's input */}
      {searchTerm.trim() !== '' && (
        <Box className="search-results">
          {filteredProfiles.map((user) => (
            <Box key={user.userId} className="search-result-item">
              <Typography variant="subtitle1">{user.firstName + ' ' + user.lastName}</Typography>
              <Button
                variant="outlined"
                color="secondary"
                size="small"
                onClick={() => {
                  // TODO: Replace alert with an API call to send a friend request.
                  alert('Friend request sent!');
                }}
              >
                Add Friend
              </Button>
            </Box>
          ))}
        </Box>
      )}
    </Box>
  );
};

export default FriendsSearch;
