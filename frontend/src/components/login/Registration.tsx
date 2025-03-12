import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { TextField, Button, Paper, Typography, Box } from "@mui/material";

import "../../styles/components/login/registration.css";

const Registration = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");

  const handleRegister = () => {
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    setError("");
    // Handle registration logic here
    console.log("Registering with:", username, password);

    navigate("/feed");
  };

  return (
    <Box className="registration-container">
      <Paper elevation={6} className="registration-paper">
        <Typography variant="h5" textAlign="center">
          Register
        </Typography>
        <TextField
          label="Username"
          variant="outlined"
          fullWidth
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="registration-input"
        />
        <TextField
          label="Password"
          type="password"
          variant="outlined"
          fullWidth
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="registration-input"
        />
        <TextField
          label="Confirm Password"
          type="password"
          variant="outlined"
          fullWidth
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="registration-input"
        />
        {error && (
          <Typography className="error-text" color="error" textAlign="center">
            {error}
          </Typography>
        )}
        <Button
          variant="contained"
          color="secondary"
          fullWidth
          disabled={!username || !password || !confirmPassword}
          onClick={handleRegister}
        >
          Register
        </Button>
        <Button
          variant="text"
          color="inherit"
          fullWidth
          onClick={() => navigate("/")}
        >
          Already have an account? Login here
        </Button>
      </Paper>
    </Box>
  );
};

export default Registration;
