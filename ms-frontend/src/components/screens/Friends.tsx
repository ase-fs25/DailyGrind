import { Box, Typography } from "@mui/material";
import Header from "../common/Header";

import "../../styles/components/screens/screen.css";

const Friends = () => {
  return (
    <Box className="screen-container">
      <Header />
      <Box className="screen-content">
        <Typography variant="h4">This is the Friends page</Typography>
      </Box>
    </Box>
  );
};

export default Friends;
