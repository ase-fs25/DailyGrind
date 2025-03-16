import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, TextField, IconButton, Button, Box, Avatar } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import CheckIcon from '@mui/icons-material/Check';
import ClearIcon from '@mui/icons-material/Clear';
import { useNavigate } from 'react-router-dom';

import '../../styles/components/common/settingsPopup.css';
import profileStore from '../../stores/profileStore';
import { Profile, ProfileInfo } from '../../types/profile';

interface SettingsPopupProps {
  open: boolean;
  onClose: () => void;
}

const SettingsPopup: React.FC<SettingsPopupProps> = ({ open, onClose }) => {
  const navigate = useNavigate();

  const [profile, setProfile] = useState<Profile>(profileStore.getProfile());

  const [editingField, setEditingField] = useState<keyof Profile | keyof ProfileInfo | null>(null);
  const [tempValue, setTempValue] = useState<string>('');

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
  };

  const startEditing = (field: keyof Profile | keyof ProfileInfo) => {
    setEditingField(field);
    if (field === 'username') {
      setTempValue(profile[field]);
    } else {
      setTempValue(profile.profileInfo[field as keyof ProfileInfo]);
    }
  };

  const handleCancel = () => {
    setEditingField(null);
    setTempValue('');
  };

  const handleConfirm = () => {
    // TODO In this function we would also need to call the API to update the user's profile
    if (!editingField) return;

    setProfile((prev) => {
      if (!prev) return prev;

      return editingField === 'username'
        ? { ...prev, username: tempValue }
        : {
            ...prev,
            profileInfo: {
              ...prev.profileInfo,
              [editingField]: tempValue,
            },
          };
    });

    // TODO Find a better solution for this. Might even change when the API is implemented
    if (editingField === 'username') {
      profileStore.setUsername(tempValue);
    } else if (
      editingField === 'profilePicture' ||
      editingField === 'location' ||
      editingField === 'education' ||
      editingField === 'workExperience'
    ) {
      profileStore.updateProfileInfoField(editingField, tempValue);
    }

    setEditingField(null);
    setTempValue('');
  };

  const handleLogout = () => {
    profileStore.deleteProfile();
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
        <Box className="profile-section">
          <Button component="label" variant="outlined" className="profile-upload-button">
            <Avatar src={profile.profileInfo.profilePicture} className="profile-avatar" style={{ cursor: 'pointer' }} />
            <input type="file" hidden accept="image/png" onChange={handleProfilePictureChange} />
          </Button>
        </Box>
        <Box className="settings-field">
          <Box className="input-container">
            <TextField
              label={'Username'}
              variant="outlined"
              fullWidth
              className={editingField === 'username' ? 'narrow-input' : 'full-input'}
              value={editingField === 'username' ? tempValue : profile.username}
              onChange={(e) => setTempValue(e.target.value)}
              onFocus={() => startEditing('username')}
            />
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
          </Box>
        </Box>
        {Object.entries(profile.profileInfo).map(
          ([key, value]) =>
            key !== 'profilePicture' && (
              <Box key={key} className="settings-field">
                <Box className="input-container">
                  <TextField
                    label={key.charAt(0).toUpperCase() + key.slice(1)}
                    variant="outlined"
                    fullWidth
                    className={editingField === key ? 'narrow-input' : 'full-input'}
                    value={editingField === key ? tempValue : value}
                    onChange={(e) => setTempValue(e.target.value)}
                    onFocus={() => startEditing(key as keyof Profile)}
                  />
                  {editingField === key &&
                    key in profile.profileInfo &&
                    tempValue !== profile.profileInfo[key as keyof ProfileInfo] && (
                      <Box className="edit-buttons">
                        <IconButton onClick={handleCancel} className="cancel-button">
                          <ClearIcon />
                        </IconButton>
                        <IconButton onClick={handleConfirm} className="confirm-button">
                          <CheckIcon />
                        </IconButton>
                      </Box>
                    )}
                </Box>
              </Box>
            ),
        )}
        <Button variant="contained" color="secondary" fullWidth onClick={handleLogout} className="logout-button">
          Logout
        </Button>
      </DialogContent>
    </Dialog>
  );
};

export default SettingsPopup;
