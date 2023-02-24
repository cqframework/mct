import { Grid, Typography, Stack } from '@mui/material';
import { parseStratifier, populationGather } from 'utils/measureReportHelpers';
import { useSelector } from 'react-redux';
import { getMeasure } from 'store/reducers/selector';
import PopulationStatistics from './PopulationStatistics';
import PatientColumnChart from './PatientColumnChart';
import MainCard from 'components/MainCard';

const PopulationMeasureReport = ({ processedMeasureReport }) => {
  const measureResource = useSelector((state) => getMeasure(state));
  const { measureReport } = processedMeasureReport;
  const population = populationGather(measureReport);
  return (
    <>
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <Typography variant="h1">{measureResource.title}</Typography>
        <Typography variant="p" color="textSecondary">
          {measureResource.purpose}
        </Typography>
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={3}>
        <PopulationStatistics population={population} />
      </Grid>
      <Grid item xs={12}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Ethnicity</Typography>
          </Grid>
        </Grid>
        <MainCard>
          <Stack spacing={1.5}>
            <Typography variant="h6" color="secondary">
              {'Ethnicity (CDC Value Set)'}
            </Typography>
          </Stack>
          <PatientColumnChart
            // stratifier={stratifier['54133-4']}
            measureReport={measureReport}
            numeratorDescription={population['numerator'].description}
            denominatorDescription={population['denominator'].description}
          />
        </MainCard>
      </Grid>
    </>
  );
};

export default PopulationMeasureReport;
