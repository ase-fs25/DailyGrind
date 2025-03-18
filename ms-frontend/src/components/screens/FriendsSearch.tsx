import React, { useState } from 'react';
import { Box, TextField, Button, Typography } from '@mui/material';
import { mockProfiles } from '../../mockData/mockProfiles';

import '../../styles/components/screens/friendsSearch.css';

const FriendsSearch: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');

  // Filter profiles that match the search term
  const filteredProfiles = mockProfiles.filter((profile) =>
    profile.username.toLowerCase().startsWith(searchTerm.toLowerCase()),
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
          {filteredProfiles.map((profile) => (
            <Box key={profile.userId} className="search-result-item">
              <Typography variant="subtitle1">{profile.username}</Typography>
              <Button
                variant="contained"
                color="primary"
                onClick={() => {
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
