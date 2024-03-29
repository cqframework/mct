import React, { useState, useEffect } from 'react';
import {
  Typography,
  Card,
  Input,
  InputLabel,
  InputAdornment,
  FormControl,
  CardContent,
  Box,
  List,
  ListItemButton,
  ListItemText
} from '@mui/material';
import { SearchOutlined } from '@ant-design/icons';

const PatientsList = ({ patients, handlePatientChange, currentSelection }) => {
  const [selectedIndex, setSelectedIndex] = React.useState(patients?.[0]?.id);
  const [originalPatientsList, setOriginalPatientsList] = useState(patients);
  const [filteredPatientsList, setFilteredPatientsList] = useState(patients);
  const [searchPatient, setSearchPatient] = useState('');

  const handleListItemClick = (event, index) => {
    setSelectedIndex(index);
  };

  useEffect(() => {
    setOriginalPatientsList(patients);
    setFilteredPatientsList(patients);
  }, [patients]);

  useEffect(() => {
    setSelectedIndex(currentSelection);
  }, [currentSelection]);

  const textSearchPatientName = (event) => {
    setSearchPatient(event.target.value);
    const targetedPts = originalPatientsList.filter((patient) => {
      return patient?.name.toLowerCase().includes(event.target.value.toLowerCase());
    });
    setFilteredPatientsList(targetedPts);
  };

  return (
    <Box>
      <Typography variant={'h4'} sx={{ mb: 2 }}>
        Patient List ({filteredPatientsList.length})
      </Typography>
      <Card>
        <CardContent>
          <Box sx={{ '& > :not(style)': { m: 1 } }}>
            <FormControl variant="standard">
              <InputLabel sx={{ p: '1px' }} htmlFor="patient-search-list">
                Search
              </InputLabel>
              <Input
                id="patient-search-list"
                onChange={textSearchPatientName}
                startAdornment={
                  <InputAdornment position="start">
                    <SearchOutlined />
                  </InputAdornment>
                }
              />
            </FormControl>
          </Box>
          <Box sx={{ width: '100%', maxWidth: 250, bgcolor: 'background.paper' }}>
            <List component="nav" sx={{ padding: 0, minHeight: 300 }}>
              {filteredPatientsList.map((patient, index) => {
                return (
                  <ListItemButton
                    key={patient?.id + '_' + index}
                    sx={{
                      '&.Mui-selected': {
                        backgroundColor: 'primary.main',
                        color: '#F8F6F0'
                      }
                    }}
                    selected={selectedIndex === patient?.id}
                    onClick={(event) => {
                      handleListItemClick(event, patient?.id);
                      handlePatientChange(patient?.id);
                    }}
                  >
                    <ListItemText primary={patient?.name} />
                  </ListItemButton>
                );
              })}
            </List>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default PatientsList;
