import { useState } from 'react';
import { Grid, Typography, Box, Chip, InputLabel, Stack, FormControl, OutlinedInput, Select, MenuItem } from '@mui/material';
import PatientInfoCard from './PatientInfoCard';
import ValidationDataTable from './ValidationDataTable';
import { extractDescription } from 'utils/measureReportHelpers';
import PatientsList from './PatientsList';

const FILTER_OPTIONS = ['Initial Population', 'Denominator', 'Numerator', 'Denominator Exclusion', 'Denominator Exception'];

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250
    }
  }
};

const IndividualMeasureReport = ({ processedMeasureReport, measureName }) => {
  const { individualLevelData, measureReport } = processedMeasureReport;
  const description = extractDescription(measureReport);
  const [currentFilter, setCurrentFilter] = useState([]);
  const [targetedPatient, setTargetedPatient] = useState(individualLevelData?.[0]);
  const handlePatientChange = (patientId) => {
    setTargetedPatient(individualLevelData.find((i, index) => i?.patient?.id === patientId));
  };

  const patientNameIdArr = individualLevelData
    .map(({ patient, groups }) => {
      const isIncludedWithFilter = groups.filter((i) => currentFilter.includes(i));
      if (currentFilter.length === 0 || isIncludedWithFilter.length > 0) {
        const given = patient?.name?.[0]?.given?.[0] || 'Unknown';
        const family = patient?.name?.[0]?.family || 'Unknown';
        return {
          id: patient?.id,
          name: given + ' ' + family
        };
      }
    })
    .filter((i) => i);

  const handleFilterChange = (evt) => {
    setCurrentFilter(evt.target.value);
  };

  const handleDelete = (value) => (evt) => {
    const filterAfterDeletion = currentFilter.filter((i) => i !== value);
    setCurrentFilter(filterAfterDeletion);
    updateCurrentPatientSelection(filterAfterDeletion);
  };

  const handleOnClose = (_evt) => {
    updateCurrentPatientSelection(currentFilter);
  };

  const updateCurrentPatientSelection = (filter) => {
    if (filter.length === 0) {
      setTargetedPatient(individualLevelData?.[0]);
    } else {
      const filteredPatients = individualLevelData.filter((i) => {
        const isIncludedWithFilter = i.groups.filter((j) => filter.includes(j));
        return isIncludedWithFilter.length > 0;
      });
      setTargetedPatient(filteredPatients[0]);
    }
  };

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
      <Grid item xs={12}>
        <FormControl sx={{ m: 1, width: 300 }}>
          <InputLabel id="group-filter-label">Filter By Group</InputLabel>
          <Select
            labelId="group-filter-label"
            id="group-filter-label-name"
            multiple
            placeholder="Filter by Group"
            value={currentFilter}
            onClose={handleOnClose}
            onChange={handleFilterChange}
            input={<OutlinedInput label="Name" />}
            MenuProps={MenuProps}
            renderValue={(selected) => (
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                {selected.map((value) => (
                  <Chip
                    sx={{ borderRadius: '30px' }}
                    onMouseDown={(event) => {
                      event.stopPropagation();
                    }}
                    onDelete={handleDelete(value)}
                    color="warning"
                    key={value}
                    label={value}
                  />
                ))}
              </Box>
            )}
          >
            {FILTER_OPTIONS.map((name) => (
              <MenuItem key={name} value={name}>
                {name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Grid>
      {individualLevelData?.length > 1 && (
        <Grid item xs={2}>
          <PatientsList
            currentSelection={targetedPatient?.patient?.id}
            handlePatientChange={handlePatientChange}
            patients={patientNameIdArr}
          />
        </Grid>
      )}
      <Grid item xs={10}>
        <Stack spacing={2}>
          <PatientInfoCard patient={targetedPatient?.patient} groups={targetedPatient?.groups} ethnicity={targetedPatient?.ethnicity} />
          <ValidationDataTable resources={targetedPatient?.resources} />
        </Stack>
      </Grid>
    </>
  );
};

export default IndividualMeasureReport;
