import PropTypes from 'prop-types';
import { IconButton, Tooltip, Chip, Grid, Stack, Typography } from '@mui/material';

import MainCard from 'components/MainCard';
import { TeamOutlined, InfoCircleOutlined } from '@ant-design/icons';

const PopulationStatistics = ({ color, population = {} }) => {
  const { count, description } = population['denominator'];
  const { count: numeratorCount, description: numeratorDescription } = population['numerator'];
  return (
    <MainCard contentSX={{ p: 2.25 }}>
      <Stack spacing={0.5}>
        <Typography variant="h6" color="textPrimary">
          Population Statistics
          <Tooltip title={'Patient count in dataset'}>
            <IconButton>
              <InfoCircleOutlined />
            </IconButton>
          </Tooltip>
        </Typography>
        <Typography variant="caption" color="textSecondary">
          {description}
        </Typography>

        <Grid container alignItems="center">
          <Grid item>
            <Typography variant="h4" color="inherit">
              {count} Patients
            </Typography>
          </Grid>
          {count && (
            <Grid item>
              <Tooltip title={numeratorDescription}>
                <Chip
                  color={'error'}
                  icon={
                    <>
                      <TeamOutlined style={{ fontSize: '0.75rem', color: 'inherit' }} />
                    </>
                  }
                  label={`${numeratorCount}`}
                  sx={{ ml: 1.25, pl: 1 }}
                  size="small"
                />
              </Tooltip>
            </Grid>
          )}
        </Grid>
      </Stack>
    </MainCard>
  );
};

PopulationStatistics.defaultProps = {
  color: 'primary'
};

export default PopulationStatistics;
