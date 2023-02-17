import { Grid, Typography, Stack } from '@mui/material';
import { extractDescription, parseStratifier, populationGather, processMeasureReportPayload } from 'utils/measureReportHelpers';
import mrFixture from 'fixtures/MeasureReportDrill.json';
import PopulationStatistics from './PopulationStatistics';
import PatientColumnChart from './PatientColumnChart';
import MainCard from 'components/MainCard';
import PopulationChartRealDataDemo from './PopulationChartRealDataDemo';

const fixtureMeasureReport = mrFixture.parameter[0].resource;

const PopulationMeasureReport = ({ processedMeasureReport }) => {
  const stratifier = parseStratifier(fixtureMeasureReport);
  const description = extractDescription(fixtureMeasureReport);
  const fxiturePopulation = populationGather(fixtureMeasureReport.group[0]);

  const { populationData, measureReport } = processedMeasureReport;
  return (
    <>
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <Typography variant="h1">Diabetes Report Data Demographics</Typography>
        <Typography variant="p" color="textSecondary">
          {description}
        </Typography>
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={3}>
        <PopulationStatistics population={fxiturePopulation} />
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
        <MainCard sx={{ mt: 3 }}>
          <Stack spacing={1.5}>
            <Typography color="secondary">Actual Measure Report Data for</Typography>
            <Typography color="primary">{measureReport.measure}</Typography>
          </Stack>
          <PopulationChartRealDataDemo populationData={populationData} />
        </MainCard>
      </Grid>
    </>
  );
};

export default PopulationMeasureReport;
