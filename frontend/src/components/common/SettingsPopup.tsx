import React, { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  IconButton,
  Button,
  Box,
  Avatar,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import CheckIcon from "@mui/icons-material/Check";
import ClearIcon from "@mui/icons-material/Clear";
import { useNavigate } from "react-router-dom";

import "../../styles/components/common/settingsPopup.css";

interface SettingsPopupProps {
  open: boolean;
  onClose: () => void;
}

interface Profile {
  profilePicture: string;
  username: string;
  location: string;
  education: string;
  workExperience: string;
}

const SettingsPopup: React.FC<SettingsPopupProps> = ({ open, onClose }) => {
  const navigate = useNavigate();

  const [profile, setProfile] = useState<Profile>({
    profilePicture: "https://via.placeholder.com/100",
    username: "JohnDoe",
    location: "New York, USA",
    education: "Bachelor's in Computer Science",
    workExperience: "Software Developer at TechCorp",
  });

  const [editingField, setEditingField] = useState<keyof Profile | null>(null);
  const [tempValue, setTempValue] = useState<string>("");

  const handleProfilePictureChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (event.target.files && event.target.files[0]) {
      const fileReader = new FileReader();
      fileReader.onload = (e) => {
        if (e.target && typeof e.target.result === "string") {
          setProfile((prev) => ({
            ...prev,
            profilePicture: e.target?.result as string,
          }));
          console.log("Profile picture changed");
        }
      };
      fileReader.readAsDataURL(event.target.files[0]);
    }
  };

  const startEditing = (field: keyof Profile) => {
    setEditingField(field);
    setTempValue(profile[field]);
  };

  const handleCancel = () => {
    setEditingField(null);
    setTempValue("");
  };

  const handleConfirm = () => {
    if (editingField) {
      setProfile((prev) => ({ ...prev, [editingField]: tempValue }));
      console.log("Changed", editingField, tempValue);
    }
    setEditingField(null);
    setTempValue("");
  };

  const handleLogout = () => {
    navigate("/login");
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
            backgroundColor: "rgba(255, 255, 255, 0.5)",
            backdropFilter: "blur(4px)",
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
          <Button
            component="label"
            variant="outlined"
            className="profile-upload-button"
          >
            <Avatar
              src={profile.profilePicture}
              className="profile-avatar"
              style={{ cursor: "pointer" }}
            />
            <input
              type="file"
              hidden
              accept="image/png"
              onChange={handleProfilePictureChange}
            />
          </Button>
        </Box>
        {Object.entries(profile).map(
          ([key, value]) =>
            key !== "profilePicture" && (
              <Box key={key} className="settings-field">
                <Box className="input-container">
                  <TextField
                    label={key.charAt(0).toUpperCase() + key.slice(1)}
                    variant="outlined"
                    fullWidth
                    className={
                      editingField === key ? "narrow-input" : "full-input"
                    }
                    value={editingField === key ? tempValue : value}
                    onChange={(e) => setTempValue(e.target.value)}
                    onFocus={() => startEditing(key as keyof Profile)}
                  />
                  {editingField === key && tempValue !== profile[key] && (
                    <Box className="edit-buttons">
                      <IconButton
                        onClick={handleCancel}
                        className="cancel-button"
                      >
                        <ClearIcon />
                      </IconButton>
                      <IconButton
                        onClick={handleConfirm}
                        className="confirm-button"
                      >
                        <CheckIcon />
                      </IconButton>
                    </Box>
                  )}
                </Box>
              </Box>
            )
        )}
        <Button
          variant="contained"
          color="secondary"
          fullWidth
          onClick={handleLogout}
          className="logout-button"
        >
          Logout
        </Button>
      </DialogContent>
    </Dialog>
  );
};

export default SettingsPopup;
