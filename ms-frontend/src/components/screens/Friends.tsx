import React, { useState } from 'react';
import { Box, Tabs, Tab } from '@mui/material';
import Header from '../../components/common/Header';
import FriendsList from './FriendsList';
import FriendsSearch from './FriendsSearch';
import FriendsRequests from './FriendsRequests';

import '../../styles/components/screens/friends.css';

const Friends = () => {
  const [activeTab, setActiveTab] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  return (
    <Box className="friends-container">
      <Header />
      <Box className="friends-content">
        {/* Tabs at the top */}
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          textColor="primary"
          indicatorColor="primary"
          centered
          className="friends-tabs"
        >
          <Tab label="Friends" />
          <Tab label="Search" />
          <Tab label="Requests" />
        </Tabs>

        {/* Conditional rendering based on the active tab */}
        {activeTab === 0 && <FriendsList />}
        {activeTab === 1 && <FriendsSearch />}
        {activeTab === 2 && <FriendsRequests />}
      </Box>
    </Box>
  );
};

export default Friends;
