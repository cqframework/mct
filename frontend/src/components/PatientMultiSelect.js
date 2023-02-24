import * as React from 'react';
import { useTheme } from '@mui/material/styles';
import Box from '@mui/material/Box';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Checkbox from '@mui/material/Checkbox';
import ListItemText from '@mui/material/ListItemText';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';

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
  const dispatch = useDispatch();
  const { selectedPatients } = useSelector((state) => state.filter);

  const handleChange = (event) => {
    const {
      target: { value }
    } = event;

    dispatch(inputSelection({ type: 'selectedPatients', value }));
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
          value={selectedPatients}
          onChange={handleChange}
          input={<OutlinedInput id="select-multiple-chip" label="Patients" />}
          renderValue={(selected) => selected.join(', ')}
          MenuProps={MenuProps}
        >
          {patientIds?.map((patientId) => (
            <MenuItem key={patientId} value={patientId}>
              <Checkbox checked={selectedPatients.indexOf(patientId) > -1} />
              <ListItemText primary={patientId} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
};

export default PatientMultiSelect;
