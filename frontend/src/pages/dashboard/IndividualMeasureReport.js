import { Box, Grid, Typography } from '@mui/material';
import PatientInfoCard from './PatientInfoCard';
import ValidationDataTable from './ValidationDataTable';
import LoadingPage from 'components/LoadingPage';
import { gatherIndividualList } from 'utils/measureReportHelpers';
import { gatherPatientDisplayData } from 'utils/patientHelper';

const IndividualMeasureReport = ({ measureReport, measureName }) => {
  const parsedReport = gatherIndividualList(measureReport);

  if (!measureReport || parsedReport == null) {
    return <LoadingPage message={'Retrieving measure report'} />;
  }

  const { patient, description, resources } = parsedReport;
  const patientInfo = gatherPatientDisplayData(patient);
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
