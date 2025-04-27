// TODO This should be avoided
/* eslint-disable no-unused-vars */
import { useState } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { UserJob } from '../../types/user';
import { addUserJob } from '../../helpers/userHelpers';

interface JobsSectionProps {
  jobs: UserJob[];
  onChange: (jobsToChange: UserJob[]) => void;
  onDelete?: (jobId: string) => void;
  readOnly?: boolean;
}

const JobsSection = ({ jobs, onChange, onDelete, readOnly = false }: JobsSectionProps) => {
  const [showJobDialog, setShowJobDialog] = useState(false);
  const [currentJob, setCurrentJob] = useState<UserJob>({
    jobId: '',
    startDate: '',
    endDate: '',
    jobTitle: '',
    companyName: '',
    location: '',
    description: '',
  });
  const [jobEditMode, setJobEditMode] = useState(false);

  const openNewJobDialog = () => {
    setCurrentJob({
      jobId: Date.now().toString(),
      startDate: '',
      endDate: '',
      jobTitle: '',
      companyName: '',
      location: '',
      description: '',
    });
    setJobEditMode(false);
    setShowJobDialog(true);
  };

  const openEditJobDialog = (job: UserJob) => {
    setCurrentJob({ ...job });
    setJobEditMode(true);
    setShowJobDialog(true);
  };

  const handleJobChange = (field: keyof UserJob, value: string) => {
    // TODO Implement backend call here
    setCurrentJob((prev) => ({ ...prev, [field]: value }));
  };

  const saveJob = () => {
    if (jobEditMode) {
      const updatedJobs = jobs.map((job) => (job.jobId === currentJob.jobId ? currentJob : job));
      onChange(updatedJobs);
    } else {
      addUserJob(currentJob);
      onChange([...jobs, currentJob]);
    }
    setShowJobDialog(false);
  };

  const deleteJob = (jobId: string) => {
    if (onDelete && jobId) {
      onDelete(jobId);
    } else {
      onChange(jobs.filter((job) => job.jobId !== jobId));
    }
  };

  return (
    <Box sx={{ mb: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">Work Experience</Typography>
        {!readOnly && (
          <Button startIcon={<AddIcon />} variant="outlined" onClick={openNewJobDialog}>
            Add Job
          </Button>
        )}
      </Box>

      {jobs.length === 0 ? (
        <Typography variant="body2" color="text.secondary" sx={{ fontStyle: 'italic' }}>
          No work experience added yet.
        </Typography>
      ) : (
        jobs.map((job) => (
          <Paper key={job.jobId} elevation={1} sx={{ p: 2, mb: 2, position: 'relative' }}>
            {!readOnly && (
              <Box sx={{ position: 'absolute', top: 5, right: 5 }}>
                <IconButton size="small" onClick={() => openEditJobDialog(job)} sx={{ mr: 1 }}>
                  <EditIcon fontSize="small" />
                </IconButton>
                <IconButton size="small" onClick={() => deleteJob(job.jobId)}>
                  <DeleteIcon fontSize="small" />
                </IconButton>
              </Box>
            )}
            <Typography variant="subtitle1">{job.jobTitle}</Typography>
            <Typography variant="body2">
              {job.companyName}, {job.location}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {job.startDate} - {job.endDate || 'Present'}
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              {job.description}
            </Typography>
          </Paper>
        ))
      )}

      {/* Job Dialog */}
      <Dialog
        open={showJobDialog}
        onClose={() => setShowJobDialog(false)}
        maxWidth="md"
        fullWidth
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
        <DialogTitle>{jobEditMode ? 'Edit Work Experience' : 'Add Work Experience'}</DialogTitle>
        <DialogContent>
          <TextField
            label="Job Title"
            variant="outlined"
            fullWidth
            value={currentJob.jobTitle}
            onChange={(e) => handleJobChange('jobTitle', e.target.value)}
            margin="normal"
          />
          <TextField
            label="Company Name"
            variant="outlined"
            fullWidth
            value={currentJob.companyName}
            onChange={(e) => handleJobChange('companyName', e.target.value)}
            margin="normal"
          />
          <TextField
            label="Location"
            variant="outlined"
            fullWidth
            value={currentJob.location}
            onChange={(e) => handleJobChange('location', e.target.value)}
            margin="normal"
          />
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Start Date (YYYY-MM)"
              variant="outlined"
              fullWidth
              value={currentJob.startDate}
              onChange={(e) => handleJobChange('startDate', e.target.value)}
              placeholder="2020-01"
              margin="normal"
            />
            <TextField
              label="End Date (YYYY-MM or leave blank for current)"
              variant="outlined"
              fullWidth
              value={currentJob.endDate}
              onChange={(e) => handleJobChange('endDate', e.target.value)}
              placeholder="2022-12"
              margin="normal"
            />
          </Box>
          <TextField
            label="Description"
            variant="outlined"
            fullWidth
            multiline
            rows={4}
            value={currentJob.description}
            onChange={(e) => handleJobChange('description', e.target.value)}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowJobDialog(false)}>Cancel</Button>
          <Button
            onClick={saveJob}
            variant="contained"
            disabled={!currentJob.jobTitle || !currentJob.companyName || !currentJob.startDate}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default JobsSection;
