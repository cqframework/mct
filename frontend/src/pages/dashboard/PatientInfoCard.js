import React from 'react';
import { Typography, Card, Box, CardContent, List, ListItemIcon, ListItem, ListItemText, IconButton, Divider, Badge } from '@mui/material';
import Collapse from '@mui/material/Collapse';
import { LeftOutlined, AuditOutlined } from '@ant-design/icons';
import { styled } from '@mui/material/styles';

import SeverityIcon from 'components/SeverityIcon';
import { gatherPatientDisplayData } from 'utils/patientHelper';

const ExpandMore = styled((props) => {
  const { expand, ...other } = props;
  return <IconButton {...other} />;
})(({ theme, expand }) => ({
  transform: !expand ? 'rotate(90deg)' : 'rotate(270deg)',
  marginLeft: 'auto',
  transition: theme.transitions.create('transform', {
    duration: theme.transitions.duration.shortest
  })
}));

const PatientInfoCard = ({ patient }) => {
  const patientInfo = gatherPatientDisplayData(patient);
  const { name = 'Unknown', birthDate = 'Unknown', gender = 'Unknown', mrn = 'Unknown' } = patientInfo;
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  return (
    <>
      <Typography variant={'h4'}>Patient Information</Typography>
      <Card sx={{ minWidth: 275, mt: 3 }}>
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

          {patient?.contained?.[0]?.issue && (
            <Box sx={{ float: 'right', display: 'flex' }} onClick={handleExpandClick}>
              <Typography sx={{ mr: 1 }}> Issues</Typography>
              <Badge badgeContent={patient?.contained?.[0]?.issue?.length} color="primary">
                <AuditOutlined />
              </Badge>
              <ExpandMore expand={expanded} onClick={handleExpandClick} aria-expanded={expanded} aria-label="show more">
                <LeftOutlined />
              </ExpandMore>
            </Box>
          )}
        </CardContent>
        <Collapse in={expanded} timeout="auto" unmountOnExit>
          <CardContent>
            <List dense={true}>
              {patient?.contained?.[0]?.issue?.map((issue) => (
                <Box key={issue.location.toString() + patient?.id}>
                  <ListItem>
                    <ListItemIcon>
                      <SeverityIcon severity={issue.severity} />
                    </ListItemIcon>
                    <ListItemText primary={issue.diagnostics} secondary={null} />
                  </ListItem>
                  <Divider />
                </Box>
              ))}
            </List>
          </CardContent>
        </Collapse>
      </Card>
    </>
  );
};

export default PatientInfoCard;
