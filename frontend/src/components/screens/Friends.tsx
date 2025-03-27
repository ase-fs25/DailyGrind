import React, { useState } from 'react';
import { Box, Tabs, Tab } from '@mui/material';
import Header from '../../components/common/Header';
import FriendsList from '../friends/FriendsList';
import FriendsSearch from '../friends/FriendsSearch';
import FriendsRequests from '../friends/FriendsRequests';

import '../../styles/components/screens/friends.css';
import '../../styles/components/screens/screen.css';

const Friends = () => {
  const [activeFriendsTab, setActiveFriendsTab] = useState(0);

  const handleFriendsTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveFriendsTab(newValue);
  };

  return (
    <Box className="screen-container">
      <Header />
      <Box className="friends-content">
        <Tabs
          value={activeFriendsTab}
          onChange={handleFriendsTabChange}
          textColor="primary"
          indicatorColor="primary"
          centered
          className="friends-tabs"
        >
          <Tab label="Friends" />
          <Tab label="Search" />
          <Tab label="Requests" />
        </Tabs>

        {activeFriendsTab === 0 && <FriendsList />}
        {activeFriendsTab === 1 && <FriendsSearch />}
        {activeFriendsTab === 2 && <FriendsRequests />}
      </Box>
    </Box>
  );
};

export default Friends;
