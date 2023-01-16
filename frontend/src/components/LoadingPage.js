import { Box, CircularProgress, Typography } from '@mui/material';

const LoadingPage = ({ message }) => {
  return (
    <Box
      sx={{
        width: '100%',
        textAlign: 'center',
        margin: '0',
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)'
      }}
    >
      <CircularProgress />
      <Typography variant="h1">{message}</Typography>
    </Box>
  );
};

export default LoadingPage;
