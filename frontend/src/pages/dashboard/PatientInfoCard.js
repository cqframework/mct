import React from 'react';
import { Typography, Card, CardContent } from '@mui/material';

const PatientInfoCard = ({ name, birthDate, gender, mrn }) => (
  <Card sx={{ minWidth: 275 }}>
    <CardContent>
      <Typography sx={{ fontSize: 20 }} color="primary.main" gutterBottom>
        {name}
      </Typography>
      <Typography variant="h5" component="div">
        {birthDate}
      </Typography>
      <Typography sx={{ mb: 1.5 }} color="text.secondary">
        {gender}
      </Typography>
      <Typography variant="body2">MRN: {mrn}</Typography>
    </CardContent>
  </Card>
);

export default PatientInfoCard;
