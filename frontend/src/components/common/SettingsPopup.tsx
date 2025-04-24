import React, { useEffect, useState } from 'react';
import { Dialog, DialogTitle, DialogContent, TextField, IconButton, Button, Box, Avatar } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import CheckIcon from '@mui/icons-material/Check';
import ClearIcon from '@mui/icons-material/Clear';
import { useNavigate } from 'react-router-dom';

import '../../styles/components/common/settingsPopup.css';
import userStore from '../../stores/userStore';
import { User } from '../../types/user';

interface SettingsPopupProps {
  open: boolean;
  onClose: () => void;
}

const SettingsPopup: React.FC<SettingsPopupProps> = ({ open, onClose }) => {
  const navigate = useNavigate();

  const [profile, setProfile] = useState<User>(userStore.getUser());
  console.log('profileStore: ', profile);

  useEffect(() => {
    if (open) {
      setProfile(userStore.getUser());
    }
  }, [open]);
  /*
  const handleProfilePictureChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const fileReader = new FileReader();
      fileReader.onload = (e) => {
        if (e.target && typeof e.target.result === 'string') {
          setProfile((prev) => ({
            ...prev,
            profilePicture: e.target?.result as string,
          }));
          console.log('Profile picture changed');
        }
      };
      fileReader.readAsDataURL(event.target.files[0]);
    }

    <Box className="profile-section">
          <Button component="label" variant="outlined" className="profile-upload-button">
            <Avatar src={profile.profileInfo.profilePicture} className="profile-avatar" style={{ cursor: 'pointer' }} />
            <input type="file" hidden accept="image/png" onChange={handleProfilePictureChange} />
          </Button>
        </Box>

        {editingField === 'username' && tempValue !== 'username' && (
              <Box className="edit-buttons">
                <IconButton onClick={handleCancel} className="cancel-button">
                  <ClearIcon />
                </IconButton>
                <IconButton onClick={handleConfirm} className="confirm-button">
                  <CheckIcon />
                </IconButton>
              </Box>
            )}
  };*/

  const handleLogout = () => {
    userStore.deleteUser();
    navigate('/');
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      className="settings-popup"
      fullWidth
      maxWidth="md"
      slotProps={{
        backdrop: {
          timeout: 600,
          style: {
            backgroundColor: 'rgba(255, 255, 255, 0.5)',
            backdropFilter: 'blur(4px)',
          },
        },
      }}
    >
      <DialogTitle className="settings-header">
        Settings
        <IconButton onClick={onClose} className="close-button">
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent className="settings-content">
        <Box className="settings-field">
          <Box className="input-container">
            <TextField
              label={'First Name'}
              variant="outlined"
              fullWidth
              className="full-input"
              value={profile.firstName}
              //onChange={(e) => setTempValue(e.target.value)}
              //onFocus={() => startEditing('username')}
              disabled
            />
          </Box>
        </Box>
        <Box className="settings-field">
          <Box className="input-container">
            <TextField
              label={'Last Name'}
              variant="outlined"
              fullWidth
              className="full-input"
              value={profile.lastName}
              //onChange={(e) => setTempValue(e.target.value)}
              //onFocus={() => startEditing('username')}
              disabled
            />
          </Box>
        </Box>
        <Box className="settings-field">
          <Box className="input-container">
            <TextField
              label={'Email'}
              variant="outlined"
              fullWidth
              className="full-input"
              value={profile.email}
              //onChange={(e) => setTempValue(e.target.value)}
              //onFocus={() => startEditing('username')}
              disabled
            />
          </Box>
        </Box>
        <Box className="settings-field">
          <Box className="input-container">
            <TextField
              label={'Birthday'}
              variant="outlined"
              fullWidth
              className="full-input"
              value={profile.birthday}
              //onChange={(e) => setTempValue(e.target.value)}
              //onFocus={() => startEditing('username')}
              disabled
            />
          </Box>
        </Box>
        <Box className="settings-field">
          <Box className="input-container">
            <TextField
              label={'Location'}
              variant="outlined"
              fullWidth
              className="full-input"
              value={profile.location}
              //onChange={(e) => setTempValue(e.target.value)}
              //onFocus={() => startEditing('username')}
              disabled
            />
          </Box>
        </Box>
        <Button variant="contained" color="secondary" fullWidth onClick={handleLogout} className="logout-button">
          Logout
        </Button>
      </DialogContent>
    </Dialog>
  );
};

export default SettingsPopup;
