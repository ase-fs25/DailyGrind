import React, {useRef, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Paper, Typography, Box, Divider, Avatar, IconButton } from '@mui/material';
import AddAPhotoIcon from '@mui/icons-material/AddAPhoto';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { Dayjs } from 'dayjs';
import { v4 as uuidv4 } from 'uuid';

import { UserJob, UserEducation } from '../../types/user';
import JobsSection from '../common/JobsSection';
import EducationSection from '../common/EducationSection';
import { registerUser } from '../../helpers/loginHelpers';

import '../../styles/components/login/registration.css';
import { getUserEmail } from '../../helpers/authHelper';

const Registration = () => {
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [birthday, setBirthday] = useState<Dayjs | null>(null);
  const [location, setLocation] = useState('');
  const [jobs, setJobs] = useState<UserJob[]>([]);
  const [education, setEducation] = useState<UserEducation[]>([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const test = async () => {
    const userEmail = await getUserEmail();
    setEmail(userEmail);
  };

  test();

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      setSelectedFile(file);

      const fileUrl = URL.createObjectURL(file);
      setPreviewUrl(fileUrl);
    }
  };

  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  const handleRegister = async () => {
    if (!firstName || !lastName || !email || !location || !birthday) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    const formattedBirthday = birthday.format('YYYY-MM-DD');
    let profilePictureUrl = '';

    try {
      if (selectedFile) {
        const fileUuid = uuidv4();
        const fileName = `users/${fileUuid}/${selectedFile.name}`;
        const bucketUrl = 'http://localhost:4566/dailygrind-profile-pictures';
        profilePictureUrl = `${bucketUrl}/${fileName}`;

        const response = await fetch(profilePictureUrl, {
          method: 'PUT',
          body: selectedFile,
          headers: {
            'Content-Type': selectedFile.type
          }
        });

        if (!response.ok) {
          throw new Error('Failed to upload profile picture');
        }

        if (previewUrl) {
          URL.revokeObjectURL(previewUrl);
        }
      }

      const result = await registerUser({
        firstName,
        lastName,
        profilePictureUrl,
        email,
        location,
        birthday: formattedBirthday,
        jobs,
        education,
      });

      if (result.success) {
        navigate('/feed', { replace: true });
      } else {
        setError(result.error ?? 'Registration failed. Please try again.');
      }
    } catch (error) {
      console.error('Error during registration:', error);
      setError('Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const isFormValid = firstName && lastName && email && location && birthday;

  return (
    <Box className="registration-container">
      <Paper elevation={6} className="registration-paper">
        <div className="registration-content">
          <Typography variant="h5" textAlign="center" gutterBottom>
            Complete your Profile
          </Typography>

          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative', my: 2 }}>
            <Avatar
              src={previewUrl ?? undefined}
              sx={{ width: 120, height: 120, boxShadow: '0 4px 8px rgba(0,0,0,0.1)', mb: 1 }}
            />
            <Box sx={{ position: 'relative' }}>
              <input
                type="file"
                accept="image/*"
                style={{ display: 'none' }}
                ref={fileInputRef}
                onChange={handleFileChange}
              />
              <IconButton
                onClick={handleUploadClick}
                size="small"
                sx={{
                  color: '#7b1fa2',
                  '&:hover': { backgroundColor: '#f3e5f5' }
                }}
              >
                <AddAPhotoIcon fontSize="small" />
              </IconButton>
            </Box>
            <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
              {selectedFile ? selectedFile.name : 'Click the camera icon to add a profile picture'}
            </Typography>
          </Box>

          <TextField
            label="First Name"
            variant="outlined"
            fullWidth
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            className="registration-input"
            margin="normal"
          />
          <TextField
            label="Last Name"
            variant="outlined"
            fullWidth
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            className="registration-input"
            margin="normal"
          />
          <TextField
            label="Email"
            type="email"
            variant="outlined"
            fullWidth
            value={email}
            className="registration-input"
            margin="normal"
            disabled
          />
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DatePicker
              label="Birthday"
              value={birthday}
              onChange={(newValue) => setBirthday(newValue)}
              slotProps={{
                textField: {
                  fullWidth: true,
                  margin: 'normal',
                  className: 'registration-input',
                },
              }}
            />
          </LocalizationProvider>
          <TextField
            label="Location"
            variant="outlined"
            fullWidth
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            className="registration-input"
            margin="normal"
          />

          <Divider sx={{ margin: '8px 0' }} />
          <JobsSection jobs={jobs} onChange={setJobs} registration />

          <Divider sx={{ my: 1 }} />
          <EducationSection education={education} onChange={setEducation} registration />
        </div>

        {error && <div className="error-text">Something went wrong during registration. Please try again.</div>}

        <Box mt={1} className="registration-button">
          <Button
            variant="contained"
            color="primary"
            fullWidth
            disabled={loading || !isFormValid}
            onClick={handleRegister}
            sx={{
              backgroundColor: '#7b1fa2',
              '&:hover': { backgroundColor: '#9c27b0' },
            }}
          >
            {loading ? 'Registering...' : 'Complete Registration'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default Registration;
