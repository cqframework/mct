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
import { fetchFacilityPatients } from 'store/reducers/data';

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

const FacilitiesMultiSelect = ({ facilities }) => {
  const dispatch = useDispatch();
  const [didSelectAll, setDidSelectAll] = useState(false); //
  const { selectedFacilities } = useSelector((state) => state.filter);

  const refreshPatientList = () => {
    dispatch(fetchFacilityPatients());
  };

  useEffect(() => {
    if (didSelectAll && facilities.length !== selectedFacilities.length) {
      setDidSelectAll(false);
    }
  }, [selectedFacilities]);

  const handleChange = (event) => {
    const { value = [] } = event.target;
    let valueClone = [...value];
    if (value.includes('select-all')) {
      if (didSelectAll) {
        // handles deselect all
        dispatch(inputSelection({ type: 'selectedFacilities', value: [] }));
      } else {
        // handles select all
        const allFacilities = facilityEntries.map((i) => i.id);
        dispatch(inputSelection({ type: 'selectedFacilities', value: allFacilities }));
        valueClone = allFacilities;
      }
      setDidSelectAll(!didSelectAll);
    } else {
      dispatch(inputSelection({ type: 'selectedFacilities', value: value }));
    }

    // If the facilities are changing numbers then we need to clear the selected patients
    if (selectedFacilities.length !== valueClone.length) {
      dispatch(inputSelection({ type: 'selectedPatients', value: [] }));
    }
  };
  const facilityEntries = facilities?.map((i) => ({ id: i.id, name: i.name }));
  return (
    <div>
      <FormControl required sx={{ width: 300 }}>
        <InputLabel sx={{ p: '1px' }} id="facility-multiple-chip-label">
          Facilities
        </InputLabel>
        <Select
          labelId="facility-multiple-chip-label"
          id="facility-multiple-chip"
          multiple
          value={selectedFacilities}
          onChange={handleChange}
          onClose={refreshPatientList}
          input={<OutlinedInput id="select-multiple-chip" label="Facilities" />}
          renderValue={(selected) =>
            selected.length > 1 ? `${selected.length} Facilities selected` : facilityEntries.find((i) => i.id === selected?.[0])?.name
          }
          MenuProps={MenuProps}
        >
          <MenuItem value={'select-all'}>
            <Checkbox checked={didSelectAll} />
            <ListItemText primary={'Select All'} />
          </MenuItem>
          {facilityEntries?.map(({ id, name }, index) => (
            <MenuItem key={id + '_' + index} value={id}>
              <Checkbox checked={selectedFacilities.indexOf(id) > -1} />
              <ListItemText primary={name} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
};

export default FacilitiesMultiSelect;
