import { Box, Grid, Typography } from '@mui/material';
import PatientInfoCard from './PatientInfoCard';
import ValidationDataTable from './ValidationDataTable';
import LoadingPage from 'components/LoadingPage';
import { gatherIndividualList, extractDescription } from 'utils/measureReportHelpers';
import { gatherPatientDisplayData } from 'utils/patientHelper';

const IndividualMeasureReport = ({ measureReportPayload, measureName }) => {
  const parsedReport = gatherIndividualList(measureReportPayload);

  const { patients, measureReport, resources } = parsedReport;
  const description = extractDescription(measureReport);
  const patientInfo = gatherPatientDisplayData(patients?.[0]);
  debugger;
  return (
    <>
      <Grid item xs={12}>
        <Typography variant={'h4'} color="textPrimary">
          {measureName}
        </Typography>
        <Typography variant="caption" color="textSecondary">
          {description}
        </Typography>
      </Grid>
      <Grid item xs={4}>
        <PatientInfoCard {...patientInfo} />
      </Grid>
      <Grid item xs={8}>
        <ValidationDataTable resources={resources} />
      </Grid>
    </>
  );
};

export default IndividualMeasureReport;
