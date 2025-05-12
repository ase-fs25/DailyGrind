import { styled } from '@mui/material/styles';
import { IconButton } from '@mui/material';

const UploadPictureButton = styled(IconButton)(() => ({
  position: 'absolute',
  bottom: 0,
  right: 0,
  backgroundColor: '#7b1fa2', // Match your purple theme
  color: 'white',
  '&:hover': {
    backgroundColor: '#9c27b0', // Match your hover purple theme
  },
  padding: '8px',
}));

export default UploadPictureButton;
