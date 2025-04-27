import React, { useEffect, useState } from 'react';
import { AppBar, Tabs, Tab, IconButton, Toolbar, Box, Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import AddIcon from '@mui/icons-material/Add';
import { useNavigate, useLocation } from 'react-router-dom';
import moment from 'moment';
import RefreshIcon from '@mui/icons-material/Refresh';
import CircularProgress from '@mui/material/CircularProgress';

import SettingsPopup from './SettingsPopup';
import AddPostPopup from './AddPostPopup';
import '../../styles/components/common/header.css';
import { POSTING_TIME } from '../../constants/postTime';
import { getUserPosts, userHasPostedAlready, validPostingTime } from '../../helpers/postHelper';

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [addPostOpen, setAddPostOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [currentTime, setCurrentTime] = useState(moment());
  const [hasPosted, setHasPosted] = useState(false);

  const tabIndex = ['/', '/posts', '/friends'].indexOf(location.pathname);

  useEffect(() => {
    const checkPostingStatus = async () => {
      try {
        const posts = await getUserPosts();
        setHasPosted(userHasPostedAlready(posts));
      } catch (error) {
        console.error('Error checking posting status:', error);
      }
    };

    checkPostingStatus();

    const timer = setInterval(() => {
      setCurrentTime(moment());
      checkPostingStatus();
    }, 60000);

    return () => clearInterval(timer);
  }, []);

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

  const isValidPostingTime = validPostingTime(moment());

  let leftText = '';
  let buttonIcon = null;
  let buttonText = '';
  let onClickHandler = null;

  if (isValidPostingTime && !hasPosted) {
    buttonIcon = <AddIcon />;
    buttonText = 'Add daily Post';
    onClickHandler = () => setAddPostOpen(true);
  } else {
    buttonIcon = isLoading ? <CircularProgress size={24} color="inherit" /> : <RefreshIcon />;

    if (hasPosted && isValidPostingTime) {
      leftText = 'You have already posted today';
    } else {
      const startTime = moment(currentTime).set({
        hour: POSTING_TIME.POST_TIME_START_HOUR,
        minute: POSTING_TIME.POST_TIME_START_MINUTES,
        second: 0,
        millisecond: 0,
      });

      leftText = currentTime.isBefore(startTime)
        ? `Come back at ${startTime.format('HH:mm')} to post`
        : 'Come back tomorrow to post';
    }

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
      <AddPostPopup open={addPostOpen} onClose={() => setAddPostOpen(false)} />
    </>
  );
};

export default Header;
