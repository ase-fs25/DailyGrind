import React, { useEffect, useState } from 'react';
import { Dialog, DialogTitle, TextField, IconButton, Button, Box, Typography } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNavigate } from 'react-router-dom';
import { deleteUser, signOut } from 'aws-amplify/auth';
import '../../styles/components/common/settingsPopup.css';
import userStore from '../../stores/userStore';
import { User, UserJob, UserEducation } from '../../types/user';
import JobsSection from './JobsSection';
import EducationSection from './EducationSection';
import { updateUser, deleteUserJob, deleteUserEducation, deleteUserProfile } from '../../helpers/userHelpers';
import postsStore from '../../stores/postsStore';
import DeleteProfilePopup from './DeleteProfilePopup';

interface SettingsPopupProps {
  open: boolean;
  onClose: () => void;
}

const SettingsPopup = ({ open, onClose }: SettingsPopupProps) => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User>(userStore.getUser());
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [jobs, setJobs] = useState<UserJob[]>(userStore.getJobs());
  const [education, setEducation] = useState<UserEducation[]>(userStore.getEducation());

  const [deleteProfilePopup, setDeleteProfilePopup] = useState<boolean>(false);

  const handleUserChange = (field: keyof User, value: string) => {
    setUser((prev) => ({ ...prev, [field]: value }));
  };

  const handleDeleteJob = async (jobId: string) => {
    try {
      const result = await deleteUserJob(jobId);
      if (result.success) {
        setJobs(jobs.filter((job) => job.jobId !== jobId));
        setStatusMessage('Job deleted successfully');
      } else {
        setStatusMessage('Failed to delete job');
      }

      setTimeout(() => {
        setStatusMessage(null);
      }, 2000);
    } catch (error) {
      console.error('Error deleting job:', error);
      setStatusMessage('Error deleting job');
    }
  };

  const handleDeleteEducation = async (educationId: string) => {
    try {
      const result = await deleteUserEducation(educationId);

      if (result.success) {
        setEducation(education.filter((edu) => edu.educationId !== educationId));
        setStatusMessage('Education entry deleted successfully');
      } else {
        setStatusMessage('Failed to delete education entry');
      }

      setTimeout(() => {
        setStatusMessage(null);
      }, 2000);
    } catch (error) {
      console.error('Error deleting education:', error);
      setStatusMessage('Error deleting education entry');
    }
  };

  useEffect(() => {
    if (open) {
      setUser(userStore.getUser());
      setJobs(userStore.getJobs());
      setEducation(userStore.getEducation());
    }
  }, [open]);

  const handleSaveProfile = async () => {
    setLoading(true);
    setStatusMessage(null);

    try {
      const updatedUser = {
        ...user,
      };

      userStore.setUser(updatedUser);
      await updateUser(updatedUser);

      setStatusMessage('Profile updated successfully');
      setTimeout(() => {
        setStatusMessage(null);
      }, 2000);
    } catch (error) {
      console.error('Error updating profile:', error);
      setStatusMessage('Failed to update profile. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await signOut();
      userStore.deleteUser();
      userStore.setFeedHasLoaded(false);
      postsStore.clearPosts();
      postsStore.clearPinnedPosts();
      postsStore.clearFeedPosts();
      window.localStorage.clear();
      navigate('/');
    } catch (e) {
      console.error('Error signing out: ', e);
    }
  };

  const handleDeleteClick = async () => {
    setDeleteProfilePopup(true);
  };

  const handleDefiniteDelete = async () => {
    await deleteUserProfile();
    await deleteUser();
    userStore.deleteUser();
    userStore.setFeedHasLoaded(false);
    postsStore.clearPosts();
    postsStore.clearPinnedPosts();
    postsStore.clearFeedPosts();
    window.localStorage.clear();
    setDeleteProfilePopup(false);
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
        <div>Settings</div>
        <IconButton onClick={onClose} className="settings-close-button">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <div className="settings-content">
        <Box className="settings-field">
          <TextField
            label="First Name"
            variant="outlined"
            fullWidth
            className="full-input"
            value={user.firstName}
            onChange={(e) => handleUserChange('firstName', e.target.value)}
            margin="normal"
          />
        </Box>
        <Box className="settings-field">
          <TextField
            label="Last Name"
            variant="outlined"
            fullWidth
            className="full-input"
            value={user.lastName}
            onChange={(e) => handleUserChange('lastName', e.target.value)}
            margin="normal"
          />
        </Box>
        <Box className="settings-field">
          <TextField
            label="Email"
            variant="outlined"
            fullWidth
            className="full-input"
            value={user.email}
            onChange={(e) => handleUserChange('email', e.target.value)}
            margin="normal"
          />
        </Box>
        <Box className="settings-field">
          <TextField
            label="Birthday"
            variant="outlined"
            fullWidth
            className="full-input"
            value={user.birthday}
            onChange={(e) => handleUserChange('birthday', e.target.value)}
            margin="normal"
          />
        </Box>
        <Box className="settings-field">
          <TextField
            label="Location"
            variant="outlined"
            fullWidth
            className="full-input"
            value={user.location}
            onChange={(e) => handleUserChange('location', e.target.value)}
            margin="normal"
          />
        </Box>
        <Box sx={{ p: 2 }}>
          <JobsSection jobs={jobs} onChange={setJobs} onDelete={handleDeleteJob} readOnly={false} />
        </Box>
        <Box sx={{ p: 2 }}>
          <EducationSection
            education={education}
            onChange={setEducation}
            onDelete={handleDeleteEducation}
            readOnly={false}
          />
        </Box>
      </div>

      {statusMessage && (
        <Box sx={{ p: 2, textAlign: 'center' }}>
          <Typography color={statusMessage.includes('success') ? 'success.main' : 'error.main'}>
            {statusMessage}
          </Typography>
        </Box>
      )}

      <Box sx={{ display: 'flex', justifyContent: 'space-between' }} className="footer-container">
        <div>
          <Button variant="contained" color="error" onClick={handleLogout} sx={{ mr: 1 }}>
            Log Out
          </Button>
          <Button variant="outlined" color="error" onClick={handleDeleteClick} sx={{ mr: 1 }}>
            Delete Profile?
          </Button>
        </div>
        <div className="footer-buttons-right">
          <Button onClick={onClose} sx={{ mr: 1, color: '#7b1fa2', '&:hover': { backgroundColor: '#f3e5f5' } }}>
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={handleSaveProfile}
            disabled={loading}
            sx={{
              backgroundColor: '#7b1fa2',
              '&:hover': { backgroundColor: '#9c27b0' },
            }}
          >
            {loading ? 'Saving...' : 'Save Changes'}
          </Button>
        </div>
      </Box>
      <DeleteProfilePopup
        open={deleteProfilePopup}
        onClose={() => setDeleteProfilePopup(false)}
        onDelete={handleDefiniteDelete}
      />
    </Dialog>
  );
};

export default SettingsPopup;
