import { useState } from 'react';
import { Grid, Typography, Stack } from '@mui/material';
import PatientInfoCard from './PatientInfoCard';
import ValidationDataTable from './ValidationDataTable';
import { extractDescription } from 'utils/measureReportHelpers';
import PatientsList from './PatientsList';

const IndividualMeasureReport = ({ processedMeasureReport, measureName }) => {
  const { individualLevelData, measureReport } = processedMeasureReport;
  const description = extractDescription(measureReport);
  const [targetedPatient, setTargetedPatient] = useState(individualLevelData?.[0]);

  const handlePatientChange = (patientId) => {
    setTargetedPatient(individualLevelData.find((i, index) => i?.patient?.id === patientId || index === patientId)); //TODO: We should not use index but data is not available
  };

  const patientNameIdArr = individualLevelData.map((i, index) => {
    const given = i?.patient?.name?.[0]?.given?.[0] || 'Unknown';
    const family = i?.patient?.name?.[0]?.family || 'Unknown';
    return {
      id: i?.patient?.id || index, //TODO: We should not use index but data is not available
      name: given + ' ' + family
    };
  });

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
      {individualLevelData?.length > 1 && (
        <Grid item xs={2}>
          <PatientsList handlePatientChange={handlePatientChange} patients={patientNameIdArr} />
        </Grid>
      )}
      <Grid item xs={10}>
        <Stack spacing={2}>
          <PatientInfoCard patient={targetedPatient?.patient} />
          <ValidationDataTable resources={targetedPatient?.resources} />
        </Stack>
      </Grid>
    </>
  );
};

export default IndividualMeasureReport;
