import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { TextField, Button, Paper, Typography, Box } from "@mui/material";

import "../../styles/components/login/login.css";

const Login = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = () => {
    // Handle login logic here
    console.log("Logging in with:", username, password);
    navigate("/feed");
  };

  return (
    <Box className="login-container">
      <Paper elevation={6} className="login-paper">
        <Typography variant="h5" textAlign="center">
          Login
        </Typography>
        <TextField
          label="Username"
          variant="outlined"
          fullWidth
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="login-input"
        />
        <TextField
          label="Password"
          type="password"
          variant="outlined"
          fullWidth
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="login-input"
        />
        <Button
          variant="contained"
          color="secondary"
          fullWidth
          disabled={!username || !password}
          onClick={handleLogin}
        >
          Login
        </Button>
        <Button
          variant="text"
          color="inherit"
          fullWidth
          onClick={() => navigate("/registration")}
        >
          No account? Register here
        </Button>
      </Paper>
    </Box>
  );
};

export default Login;
