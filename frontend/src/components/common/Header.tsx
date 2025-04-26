import React, { useState } from 'react';
import { AppBar, Tabs, Tab, IconButton, Toolbar, Box, Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import AddIcon from '@mui/icons-material/Add';
import { useNavigate, useLocation } from 'react-router-dom';
import moment from 'moment';
import RefreshIcon from '@mui/icons-material/Refresh';
import CircularProgress from '@mui/material/CircularProgress';

import SettingsPopup from './SettingsPopup';
// import AddPostPopup from './AddPostPopup';
import '../../styles/components/common/header.css';
import { POSTING_TIME } from '../../constants/postTime';

// TODO add logic for only being able to post once a day

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [settingsOpen, setSettingsOpen] = useState(false);
  // const [addPostOpen, setAddPostOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [currentTime, setCurrentTime] = useState(moment());

  const tabIndex = ['/', '/posts', '/friends'].indexOf(location.pathname);

  const handleTabChange = (event: React.SyntheticEvent, newIndex: number) => {
    const paths = ['/', '/posts', '/friends'];
    navigate(paths[newIndex]);
  };

  const handleSettingsClick = () => {
    setSettingsOpen(true);
  };

  const checkTime = () => {
    setCurrentTime(moment());
  };

  const startTime = moment(currentTime).set({
    hour: POSTING_TIME.POST_TIME_START_HOUR,
    minute: POSTING_TIME.POST_TIME_START_MINUTES,
    second: 0,
    millisecond: 0,
  });

  const endTime = moment(currentTime).set({
    hour: POSTING_TIME.POST_TIME_END_HOUR,
    minute: POSTING_TIME.POST_TIME_END_MINUTES,
    second: 0,
    millisecond: 0,
  });

  const isValidPostingTime = currentTime.isBetween(startTime, endTime, null, '[]');

  let leftText = '';
  let buttonIcon = null;
  let buttonText = '';
  let onClickHandler = null;

  if (isValidPostingTime) {
    buttonIcon = <AddIcon />;
    buttonText = 'Add daily Post';
    onClickHandler = () => {
      const now = moment();
      const endTimeNow = moment(now).set({
        hour: POSTING_TIME.POST_TIME_END_HOUR,
        minute: POSTING_TIME.POST_TIME_END_MINUTES,
        second: 0,
        millisecond: 0,
      });
      if (now <= endTimeNow) {
        // setAddPostOpen(true);
      } else {
        checkTime();
      }
    };
  } else {
    buttonIcon = isLoading ? <CircularProgress size={24} color="inherit" /> : <RefreshIcon />;

    // If currentTime is before the posting window, suggest the start time; otherwise, ask to come back tomorrow.
    leftText = currentTime.isBefore(startTime)
      ? `Come back at ${startTime.format('HH:mm')} to post`
      : 'Come back tomorrow to post';

    onClickHandler = () => {
      setIsLoading(true);
      setTimeout(() => {
        setIsLoading(false);
        checkTime();
      }, 2000);
    };
  }

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
            <div style={{ display: 'flex', alignItems: 'center' }}>
              {leftText && <span style={{ marginRight: 8 }}>{leftText}</span>}
              <Button
                variant="contained"
                startIcon={buttonIcon}
                onClick={onClickHandler}
                className="add-post-button"
                sx={{
                  backgroundColor: '#7b1fa2',
                  '&:hover': { backgroundColor: '#9c27b0' },
                  minWidth: 'auto',
                  px: 2,
                }}
              >
                {buttonText}
              </Button>
            </div>
            <IconButton color="inherit" onClick={handleSettingsClick} className="settings-button">
              <SettingsIcon />
            </IconButton>
          </Box>
        </Toolbar>
      </AppBar>
      <SettingsPopup open={settingsOpen} onClose={() => setSettingsOpen(false)} />
      {/* <AddPostPopup open={addPostOpen} onClose={() => setAddPostOpen(false)} /> */}
    </>
  );
};

export default Header;
