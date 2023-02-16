import { Grid, Typography } from '@mui/material';
import { extractDescription, parseStratifier, populationGather } from 'utils/measureReportHelpers';

const PopulationMeasureReport = ({ measureReport }) => {
  const stratifier = parseStratifier(measureReport);
  const description = extractDescription(measureReport);
  const population = populationGather(measureReport.group[0]);

  return (
    <>
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <Typography variant="h1">Diabetes Report Data Demographics</Typography>
        <Typography variant="p" color="textSecondary">
          {description}
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
        <MainCard sx={{ mt: 1.75 }}>
          <Stack spacing={1.5} sx={{ mb: -12 }}>
            <Typography variant="h6" color="secondary">
              {stratifier['54133-4'].title}
            </Typography>
          </Stack>
          <PatientColumnChart stratifier={stratifier['54133-4']} />
        </MainCard>
      </Grid>
    </>
  );
};

export default PopulationMeasureReport;
