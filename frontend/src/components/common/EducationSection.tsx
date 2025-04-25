import React, { useState } from 'react';
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
import { UserEducation } from '../../types/user';

interface EducationSectionProps {
  education: UserEducation[];
  onChange: (education: UserEducation[]) => void;
  onDelete?: (educationId: string) => void;
  readOnly: boolean;
}

const EducationSection: React.FC<EducationSectionProps> = ({ education, onChange, onDelete, readOnly = false }) => {
  const [showEducationDialog, setShowEducationDialog] = useState(false);
  const [currentEducation, setCurrentEducation] = useState<UserEducation>({
    educationId: '',
    degree: '',
    institution: '',
    educationStartDate: '',
    educationEndDate: '',
    fieldOfStudy: '',
    educationLocation: '',
    educationDescription: '',
  });
  const [educationEditMode, setEducationEditMode] = useState(false);

  const openNewEducationDialog = () => {
    setCurrentEducation({
      educationId: Date.now().toString(),
      degree: '',
      institution: '',
      educationStartDate: '',
      educationEndDate: '',
      fieldOfStudy: '',
      educationLocation: '',
      educationDescription: '',
    });
    setEducationEditMode(false);
    setShowEducationDialog(true);
  };

  const openEditEducationDialog = (edu: UserEducation) => {
    setCurrentEducation({ ...edu });
    setEducationEditMode(true);
    setShowEducationDialog(true);
  };

  const handleEducationChange = (field: keyof UserEducation, value: string) => {
    setCurrentEducation((prev) => ({ ...prev, [field]: value }));
  };

  const saveEducation = () => {
    if (educationEditMode) {
      const updatedEducation = education.map((edu) =>
        edu.educationId === currentEducation.educationId ? currentEducation : edu,
      );
      onChange(updatedEducation);
    } else {
      onChange([...education, currentEducation]);
    }
    setShowEducationDialog(false);
  };

  const deleteEducation = (educationId: string) => {
    if (onDelete && educationId) {
      onDelete(educationId);
    } else {
      onChange(education.filter((edu) => edu.educationId !== educationId));
    }
  };

  return (
    <Box sx={{ mb: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">Education</Typography>
        {!readOnly && (
          <Button startIcon={<AddIcon />} variant="outlined" onClick={openNewEducationDialog}>
            Add Education
          </Button>
        )}
      </Box>

      {education.length === 0 ? (
        <Typography variant="body2" color="text.secondary" sx={{ fontStyle: 'italic' }}>
          No education history added yet.
        </Typography>
      ) : (
        education.map((edu) => (
          <Paper key={edu.educationId} elevation={1} sx={{ p: 2, mb: 2, position: 'relative' }}>
            {!readOnly && (
              <Box sx={{ position: 'absolute', top: 5, right: 5 }}>
                <IconButton size="small" onClick={() => openEditEducationDialog(edu)} sx={{ mr: 1 }}>
                  <EditIcon fontSize="small" />
                </IconButton>
                <IconButton size="small" onClick={() => deleteEducation(edu.educationId)}>
                  <DeleteIcon fontSize="small" />
                </IconButton>
              </Box>
            )}
            <Typography variant="subtitle1">
              {edu.degree} in {edu.fieldOfStudy}
            </Typography>
            <Typography variant="body2">
              {edu.institution}, {edu.educationLocation}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {edu.educationStartDate} - {edu.educationEndDate || 'Present'}
            </Typography>
            <Typography variant="body2" sx={{ mt: 1 }}>
              {edu.educationDescription}
            </Typography>
          </Paper>
        ))
      )}

      {/* Education Dialog */}
      <Dialog open={showEducationDialog} onClose={() => setShowEducationDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>{educationEditMode ? 'Edit Education' : 'Add Education'}</DialogTitle>
        <DialogContent>
          <TextField
            label="Degree"
            variant="outlined"
            fullWidth
            value={currentEducation.degree}
            onChange={(e) => handleEducationChange('degree', e.target.value)}
            margin="normal"
          />
          <TextField
            label="Field of Study"
            variant="outlined"
            fullWidth
            value={currentEducation.fieldOfStudy}
            onChange={(e) => handleEducationChange('fieldOfStudy', e.target.value)}
            margin="normal"
          />
          <TextField
            label="Institution"
            variant="outlined"
            fullWidth
            value={currentEducation.institution}
            onChange={(e) => handleEducationChange('institution', e.target.value)}
            margin="normal"
          />
          <TextField
            label="Location"
            variant="outlined"
            fullWidth
            value={currentEducation.educationLocation}
            onChange={(e) => handleEducationChange('educationLocation', e.target.value)}
            margin="normal"
          />
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Start Date (YYYY-MM)"
              variant="outlined"
              fullWidth
              value={currentEducation.educationStartDate}
              onChange={(e) => handleEducationChange('educationStartDate', e.target.value)}
              placeholder="2018-09"
              margin="normal"
            />
            <TextField
              label="End Date (YYYY-MM or leave blank for current)"
              variant="outlined"
              fullWidth
              value={currentEducation.educationEndDate}
              onChange={(e) => handleEducationChange('educationEndDate', e.target.value)}
              placeholder="2022-06"
              margin="normal"
            />
          </Box>
          <TextField
            label="Description"
            variant="outlined"
            fullWidth
            multiline
            rows={4}
            value={currentEducation.educationDescription}
            onChange={(e) => handleEducationChange('educationDescription', e.target.value)}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowEducationDialog(false)}>Cancel</Button>
          <Button
            onClick={saveEducation}
            variant="contained"
            disabled={!currentEducation.degree || !currentEducation.institution || !currentEducation.educationStartDate}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default EducationSection;
