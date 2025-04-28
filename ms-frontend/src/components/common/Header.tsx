import React, { useState } from 'react';
import { AppBar, Tabs, Tab, IconButton, Toolbar, Box, Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import AddIcon from '@mui/icons-material/Add';
import { useNavigate, useLocation } from 'react-router-dom';

import SettingsPopup from './SettingsPopup';
import AddPostPopup from './AddPostPopup';
import '../../styles/components/common/header.css';

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [addPostOpen, setAddPostOpen] = useState(false);

  const handleAddPostClick = () => {
    setAddPostOpen(true);
  };

  const tabIndex = ['/feed', '/posts', '/friends'].indexOf(location.pathname);

  const handleTabChange = (event: React.SyntheticEvent, newIndex: number) => {
    const paths = ['/feed', '/posts', '/friends'];
    navigate(paths[newIndex]);
  };

  const handleSettingsClick = () => {
    setSettingsOpen(true);
  };

  return (
    <>
      <AppBar position="static" className="header-container">
        <Toolbar className="toolbar">
          <Box className="logo">
            <span>Logo</span>
          </Box>
          <Tabs value={tabIndex >= 0 ? tabIndex : 0} onChange={handleTabChange} className="header-tabs" centered>
            <Tab label="Feed" className="header-tab" />
            <Tab label="My Posts" className="header-tab" />
            <Tab label="Friends" className="header-tab" />
          </Tabs>
          <Box className="button-row">
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleAddPostClick}
              className="add-post-button"
              sx={{
                backgroundColor: '#7b1fa2',
                '&:hover': { backgroundColor: '#9c27b0' },
              }}
            >
              Add daily Post
            </Button>
            <IconButton color="inherit" onClick={handleSettingsClick} className="settings-button">
              <SettingsIcon />
            </IconButton>
          </Box>
        </Toolbar>
      </AppBar>
      <SettingsPopup open={settingsOpen} onClose={() => setSettingsOpen(false)} />
      <AddPostPopup open={addPostOpen} onClose={() => setAddPostOpen(false)} />
    </>
  );
};

export default Header;
