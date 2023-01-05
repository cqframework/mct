import PropTypes from 'prop-types';

// material-ui
import { useTheme } from '@mui/material/styles';
import { Stack, Typography, Chip } from '@mui/material';

// project import
import DrawerHeaderStyled from './DrawerHeaderStyled';

const DrawerHeader = ({ open }) => {
  const theme = useTheme();

  return (
    <DrawerHeaderStyled theme={theme} open={open}>
      <Stack direction="row" spacing={1} alignItems="center">
        <Typography sx={{ mt: 3, ml: 3 }} variant="h2" align="center" gutterBottom>
          CQF MCT
        </Typography>
        <Chip
          label={'1.0.0'}
          size="small"
          sx={{ height: 16, '& .MuiChip-label': { fontSize: '0.625rem', py: 0.25 } }}
          component="a"
          target="_blank"
        />
      </Stack>
    </DrawerHeaderStyled>
  );
};

DrawerHeader.propTypes = {
  open: PropTypes.bool
};

export default DrawerHeader;
