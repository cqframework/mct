import { Grid, Typography } from '@mui/material';
import PatientInfoCard from './PatientInfoCard';
import ValidationDataTable from './ValidationDataTable';
import { extractDescription } from 'utils/measureReportHelpers';

const IndividualMeasureReport = ({ processedMeasureReport, measureName }) => {
  const { patients, measureReport, resources, operationOutcome } = processedMeasureReport;
  const description = extractDescription(measureReport);

  const sortedResources = resources?.sort((a, b) => b?.contained?.length || 0 - a?.contained?.length || 0);
  if (operationOutcome) sortedResources?.push(operationOutcome);

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
        <PatientInfoCard patient={patients?.[0]} />
      </Grid>
      <Grid item xs={8}>
        <ValidationDataTable resources={sortedResources} operationOutcome={operationOutcome} />
      </Grid>
    </>
  );
};

export default IndividualMeasureReport;
