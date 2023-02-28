import { Grid, Typography, Stack, Tooltip, IconButton } from '@mui/material';
import { populationGather } from 'utils/measureReportHelpers';
import { InfoCircleOutlined } from '@ant-design/icons';

import { useSelector } from 'react-redux';
import { getMeasure } from 'store/reducers/selector';
import PopulationStatistics from './PopulationStatistics';
import PatientColumnChart from './PatientColumnChart';
import MainCard from 'components/MainCard';
import MeasureReportPopulationData from './MeasureReportPopulationData';
const PopulationMeasureReport = ({ processedMeasureReport }) => {
  const measureResource = useSelector((state) => getMeasure(state));
  const { measureReport, populationData } = processedMeasureReport;
  const population = populationGather(measureReport);

  return (
    <>
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <Typography variant="h1">{measureResource.title}</Typography>
        <Typography variant="p" color="textSecondary">
          {measureResource.purpose}
        </Typography>
      </Grid>
      <Grid item xs={4}>
        <PopulationStatistics population={population} />
      </Grid>
      <Grid item xs={4}>
        <MainCard sx={{ maxWidth: 300 }} contentSX={{ p: 2.25 }}>
          <Stack spacing={0.5}>
            <Typography variant="h6" color="textPrimary">
              Measure Score
              <Tooltip title={'Score this group achieved'}>
                <IconButton>
                  <InfoCircleOutlined />
                </IconButton>
              </Tooltip>
            </Typography>
            <Typography variant="h5">{populationData.measureScore}</Typography>
          </Stack>
        </MainCard>
      </Grid>
      <Grid item xs={12}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Ethnicity and Race</Typography>
          </Grid>
        </Grid>
        <MainCard>
          <PatientColumnChart
            measureReport={measureReport}
            numeratorDescription={population['numerator'].description}
            denominatorDescription={population['denominator'].description}
          />
        </MainCard>
        <Grid sx={{ mt: 5 }} item>
          <Typography variant="h5">Measure Report Data</Typography>
        </Grid>
        <MainCard>
          <MeasureReportPopulationData populationData={populationData} />
        </MainCard>
      </Grid>
    </>
  );
};

export default PopulationMeasureReport;
