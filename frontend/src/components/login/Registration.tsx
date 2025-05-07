import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Paper, Typography, Box, Divider } from '@mui/material';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { Dayjs } from 'dayjs';

import { UserJob, UserEducation } from '../../types/user';
import JobsSection from '../common/JobsSection';
import EducationSection from '../common/EducationSection';
import { registerUser } from '../../helpers/loginHelpers';

import '../../styles/components/login/registration.css';

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

  const handleRegister = async () => {
    if (!firstName || !lastName || !email || !location || !birthday) {
      setError('Please fill in all required fields');
      return;
    }

    setLoading(true);
    const formattedBirthday = birthday.format('YYYY-MM-DD');

    const result = await registerUser({
      firstName,
      lastName,
      email,
      location,
      birthday: formattedBirthday,
      jobs,
      education,
    });

    setLoading(false);

    if (result.success) {
      navigate('/feed', { replace: true });
    } else {
      setError(result.error || 'Registration failed. Please try again.');
    }
  };

  return (
    <Box className="registration-container">
      <Paper elevation={6} className="registration-paper">
        <Typography variant="h5" textAlign="center" gutterBottom>
          Complete your Profile
        </Typography>
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
          onChange={(e) => setEmail(e.target.value)}
          className="registration-input"
          margin="normal"
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

        {error && (
          <Typography className="error-text" color="error" textAlign="center">
            Something went wrong during registration. Please try again.
          </Typography>
        )}

        <Box mt={1}>
          <Button
            variant="contained"
            color="primary"
            fullWidth
            disabled={loading || !firstName || !lastName || !email || !location || !birthday}
            onClick={handleRegister}
          >
            {loading ? 'Registering...' : 'Complete Registration'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default Registration;
