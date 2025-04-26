import React, { useEffect, useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  IconButton,
  Button,
  Box,
  Typography,
  Tabs,
  Tab,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNavigate } from 'react-router-dom';
import { signOut } from 'aws-amplify/auth';
import '../../styles/components/common/settingsPopup.css';
import userStore from '../../stores/userStore';
import { User, UserJob, UserEducation } from '../../types/user';
import JobsSection from './JobsSection';
import EducationSection from './EducationSection';
import { updateUser, deleteUserJob, deleteUserEducation } from '../../helpers/userHelpers';

interface SettingsPopupProps {
  open: boolean;
  onClose: () => void;
}

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`settings-tabpanel-${index}`}
      aria-labelledby={`settings-tab-${index}`}
      {...other}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
}

const SettingsPopup = ({ open, onClose }: SettingsPopupProps) => {
  const navigate = useNavigate();
  const [tabValue, setTabValue] = useState(0);
  const [user, setUser] = useState<User>(userStore.getUser());
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [jobs, setJobs] = useState<UserJob[]>([]);
  const [education, setEducation] = useState<UserEducation[]>([]);

  const handleUserChange = (field: keyof User, value: string) => {
    // TODO Propably not enought as it needs to be set in the backend and the store as well
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
      const user = userStore.getUser();
      setUser(user);
      setJobs(user.jobs || []);
      setEducation(user.education || []);
    }
  }, [open]);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleSaveProfile = async () => {
    setLoading(true);
    setStatusMessage(null);

    try {
      const updatedUser = {
        ...user,
        jobs,
        education,
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
      navigate('/');
    } catch (e) {
      console.error('Error signing out: ', e);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      className="settings-popup"
      fullWidth
      maxWidth="md"
      sx={{ '& .MuiDialog-paper': { height: '80vh', display: 'flex', flexDirection: 'column' } }}
    >
      <DialogTitle className="settings-header">
        Settings
        <IconButton onClick={onClose} className="close-button">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers sx={{ p: 0, flexGrow: 1, overflowY: 'auto' }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={handleTabChange} aria-label="settings tabs">
            <Tab label="Personal Info" id="settings-tab-0" />
            <Tab label="Work Experience" id="settings-tab-1" />
            <Tab label="Education" id="settings-tab-2" />
            <Tab label="Account" id="settings-tab-3" />
          </Tabs>
        </Box>

        {/* Personal Info Tab */}
        <TabPanel value={tabValue} index={0}>
          <Box sx={{ p: 2 }}>
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
            {/*Since email in the userObject is not connected to the email cognito uses we can change it. However do we add some sort of connection between the two?*/}
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
          </Box>
        </TabPanel>

        {/* Work Experience Tab */}
        <TabPanel value={tabValue} index={1}>
          <Box sx={{ p: 2 }}>
            <JobsSection jobs={jobs} onChange={setJobs} onDelete={handleDeleteJob} readOnly={false} />
          </Box>
        </TabPanel>

        {/* Education Tab */}
        <TabPanel value={tabValue} index={2}>
          <Box sx={{ p: 2 }}>
            <EducationSection
              education={education}
              onChange={setEducation}
              onDelete={handleDeleteEducation}
              readOnly={false}
            />
          </Box>
        </TabPanel>

        {/* Account Tab */}
        <TabPanel value={tabValue} index={3}>
          <Box sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Account Settings
            </Typography>
            <Button variant="contained" color="error" onClick={handleLogout} sx={{ mt: 2 }}>
              Log Out
            </Button>
          </Box>
        </TabPanel>

        {statusMessage && (
          <Box sx={{ p: 2, textAlign: 'center' }}>
            <Typography color={statusMessage.includes('success') ? 'success.main' : 'error.main'}>
              {statusMessage}
            </Typography>
          </Box>
        )}
      </DialogContent>

      {tabValue !== 3 && (
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'flex-end' }}>
          <Button onClick={onClose} sx={{ mr: 1 }}>
            Cancel
          </Button>
          <Button variant="contained" onClick={handleSaveProfile} disabled={loading}>
            {loading ? 'Saving...' : 'Save Changes'}
          </Button>
        </Box>
      )}
    </Dialog>
  );
};

export default SettingsPopup;
