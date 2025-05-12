import { Button, Dialog, DialogActions, DialogTitle } from '@mui/material';
import '../../styles/components/common/deleteProfilePopup.css';

interface DeleteProfilePopupProps {
  open: boolean;
  onClose: () => void;
  onDelete: () => void;
}

const DeleteProfilePopup = ({ open, onClose, onDelete }: DeleteProfilePopupProps) => {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      className="delete-profile-popup"
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
      <DialogTitle className="delete-profile-header">
        You are about to delete your profile. This action is irreversible. Are you sure?
      </DialogTitle>
      <DialogActions className="delete-profile-buttons">
        <Button onClick={onClose} color="error">
          Cancel
        </Button>
        <Button onClick={onDelete} color="error" variant="contained">
          Delete Profile
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteProfilePopup;
