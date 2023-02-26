import PropTypes from 'prop-types';
import { IconButton, Tooltip, Chip, Grid, Stack, Typography } from '@mui/material';

import MainCard from 'components/MainCard';
import { TeamOutlined, InfoCircleOutlined } from '@ant-design/icons';

const PopulationStatistics = ({ color, population = {} }) => {
  const { count, description } = population['denominator'];
  const { count: numeratorCount, description: numeratorDescription } = population['numerator'];
  const totalPopCount = Object.values(population.gender).reduce((acc, cur) => acc + cur, 0);
  return (
    <MainCard sx={{ minWidth: 400 }} contentSX={{ p: 2.25 }}>
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
          <Grid item xs={4}>
            <Typography variant="h4" color="inherit">
              {totalPopCount} Patients
            </Typography>
          </Grid>
          {totalPopCount && (
            <Grid item xs={8}>
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

          <Grid item xs={12}>
            <Typography variant="h6" color="inherit">
              Male: {population.gender['M']}
            </Typography>
          </Grid>
          <Grid item xs={12}>
            Female: {population.gender['F']}
          </Grid>
        </Grid>
      </Stack>
    </MainCard>
  );
};

PopulationStatistics.defaultProps = {
  color: 'primary'
};

export default PopulationStatistics;
