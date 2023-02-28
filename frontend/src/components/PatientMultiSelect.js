import { useEffect, useState } from 'react';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Checkbox from '@mui/material/Checkbox';
import ListItemText from '@mui/material/ListItemText';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import LoadingButton from '@mui/lab/LoadingButton';

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

const PatientMultiSelect = ({ patients = [] }) => {
  const dispatch = useDispatch();
  const [didSelectAll, setDidSelectAll] = useState(false);
  const { selectedPatients } = useSelector((state) => state.filter);

  useEffect(() => {
    if (didSelectAll && patients?.member?.length !== selectedPatients.length) {
      setDidSelectAll(false);
    }
  }, [selectedPatients]);

  const handleChange = (event) => {
    const { value } = event.target;
    if (value.includes('select-all')) {
      if (didSelectAll) {
        // handles deselect all
        dispatch(inputSelection({ type: 'selectedPatients', value: [] }));
      } else {
        // handles select all
        dispatch(inputSelection({ type: 'selectedPatients', value: patientIds }));
      }

      setDidSelectAll(!didSelectAll);
    } else {
      dispatch(inputSelection({ type: 'selectedPatients', value }));
    }
  };
  const patientIds = patients?.member?.map(({ entity }) => entity.reference) || [];

  // Cut to first patient after filtering
  // Deselect all - done
  // Use issue type for user friendly messages
  // tag in location data
  // Measure Report Data for chart
  // use meta tag to get report
  // Push pt operation outcome to validation table

  return (
    <div>
      <FormControl sx={{ width: 300 }}>
        {patients.length === 0 ? (
          <LoadingButton loading sx={{ height: '41.13px' }} variant="outlined">
            Fetching Patients...
          </LoadingButton>
        ) : (
          <>
            <InputLabel id="patient-multiple-chip-label">Patients</InputLabel>
            <Select
              labelId="patient-multiple-chip-label"
              id="patient-multiple-chip"
              multiple
              inputProps={{ autoFocus: true }}
              disabled={patientIds?.length === 0}
              value={selectedPatients}
              onChange={handleChange}
              input={<OutlinedInput id="select-multiple-chip" label="Patients" />}
              renderValue={(selected) => `${selected.length} patients selected`}
              MenuProps={MenuProps}
            >
              <MenuItem value={'select-all'}>
                <Checkbox checked={didSelectAll} />
                <ListItemText primary={'Select All'} />
              </MenuItem>
              {patientIds?.map((patientId, index) => (
                <MenuItem key={patientId + '_' + index} value={patientId}>
                  <Checkbox checked={selectedPatients.indexOf(patientId) > -1} />
                  <ListItemText primary={patientId} />
                </MenuItem>
              ))}
            </Select>
          </>
        )}
      </FormControl>
    </div>
  );
};

export default PatientMultiSelect;
