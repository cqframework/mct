import React from 'react';

import { Typography, Card, Box, Chip, CardContent, IconButton } from '@mui/material';
import { styled } from '@mui/material/styles';

import { gatherPatientDisplayData } from 'utils/patientHelper';
import { useSelector } from 'react-redux';
import { HomeOutlined } from '@ant-design/icons';
const PatientInfoCard = ({ patient, groups, ethnicity }) => {
  const patientInfo = gatherPatientDisplayData(patient);
  const { name = 'Unknown', birthDate = 'Unknown', gender = 'Unknown', mrn = 'Unknown' } = patientInfo;

  const locationId = patient?.meta?.tag?.find((i) => i.system === 'http://cms.gov/fhir/mct/tags/Location')?.display;
  const facilityName = useSelector((state) => state.data.facilities.find((i) => `Location/${i.id}` === locationId)?.name);

  return (
    <>
      <Typography variant={'h4'}>Patient Information</Typography>
      <Card sx={{ minWidth: 275, mt: 3 }}>
        <CardContent>
          <Typography sx={{ fontSize: 20, display: 'flex', justifyContent: 'space-between' }} color="primary.main" gutterBottom>
            {name}
            <Box sx={{ display: 'flex', gap: '10px' }}>
              {groups.map((i) => (
                <Chip key={i} sx={{ borderRadius: '20px' }} label={i} color="warning" />
              ))}
            </Box>
          </Typography>
          <Typography variant="h5" component="div">
            {birthDate}
          </Typography>
          <Typography sx={{ mb: 1.5 }} color="text.secondary">
            {gender}
          </Typography>
          <Typography variant="span">MRN: {mrn}</Typography>
          <Box sx={{ display: 'flex', gap: '10px' }}>
            {ethnicity.map((i) => (
              <Chip key={i} sx={{ borderRadius: '20px' }} label={i} color="primary" />
            ))}
          </Box>
          <Chip sx={{ borderRadius: '20px', mt: 1 }} label={facilityName} color="warning" icon={<HomeOutlined />} />
        </CardContent>
      </Card>
    </>
  );
};

export default PatientInfoCard;
