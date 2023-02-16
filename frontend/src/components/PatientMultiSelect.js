import * as React from 'react';
import { useTheme } from '@mui/material/styles';
import Box from '@mui/material/Box';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Chip from '@mui/material/Chip';

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

function getStyles(name, personName, theme) {
  return {
    fontWeight: personName.indexOf(name) === -1 ? theme.typography.fontWeightRegular : theme.typography.fontWeightMedium
  };
}

const PatientMultiSelect = ({ patients }) => {
  const theme = useTheme();
  const [personName, setPersonName] = React.useState([]);

  const handleChange = (event) => {
    const {
      target: { value }
    } = event;
    setPersonName(
      // On autofill we get a stringified value.
      typeof value === 'string' ? value.split(',') : value
    );
  };
  const patientIds = patients?.member?.map(({ entity }) => entity.reference);
  return (
    <div>
      <FormControl sx={{ width: 300 }}>
        <InputLabel id="patient-multiple-chip-label">Patients</InputLabel>
        <Select
          labelId="patient-multiple-chip-label"
          id="patient-multiple-chip"
          multiple
          inputProps={{ autoFocus: true }}
          value={personName}
          onChange={handleChange}
          input={<OutlinedInput id="select-multiple-chip" label="Patients" />}
          renderValue={(selected) => (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {selected.map((value) => (
                <Chip key={value} label={value} />
              ))}
            </Box>
          )}
          MenuProps={MenuProps}
        >
          {patientIds.map((patientId) => (
            <MenuItem key={patientId} value={patientId} style={getStyles(patientId, personName, theme)}>
              {patientId}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
};

export default PatientMultiSelect;
