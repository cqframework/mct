import PropTypes from 'prop-types';

import { useTheme } from '@mui/material/styles';
import { Stack, Typography, Chip } from '@mui/material';

import { useSelector } from 'react-redux';
import DrawerHeaderStyled from './DrawerHeaderStyled';

const DrawerHeader = ({ open }) => {
  const theme = useTheme();
  const { organizations } = useSelector((state) => state.data);
  const { organization: organizationId } = useSelector((state) => state.filter);
  const organization = organizations.find((org) => org.id === organizationId);
  return (
    <DrawerHeaderStyled theme={theme} open={open}>
      <Stack direction="column" spacing={1} alignItems="center">
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
        <Typography sx={{ fontWeight: 'bold', color: 'primary.main' }} variant="span">
          {organization.name}
        </Typography>
      </Stack>
    </DrawerHeaderStyled>
  );
};

DrawerHeader.propTypes = {
  open: PropTypes.bool
};

export default DrawerHeader;
