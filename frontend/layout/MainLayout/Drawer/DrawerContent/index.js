// project import
import Navigation from './Navigation';
import SimpleBar from 'components/third-party/SimpleBar';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';

import { InputLabel, Select, FormControl, MenuItem } from '@mui/material';

const FacilitiesSelection = ({ facility, facilityList, handleChange }) => {
  return (
    <>
      <InputLabel id="facilities-select-label">Facilities</InputLabel>
      <Select
        labelId="facilities-select-label"
        id="facilities-select"
        value={facility}
        label="Facilities"
        onChange={(e) => handleChange(e.target.value)}
      >
        <MenuItem value={'1234'}>Yale New Haven Hospital</MenuItem>
      </Select>
    </>
  );
};

const DrawerContent = () => {
  const facility = useSelector((state) => state.filter.facility);
  const dispatch = useDispatch();

  return (
    <SimpleBar
      sx={{
        '& .simplebar-content': {
          display: 'flex',
          flexDirection: 'column'
        }
      }}
    >
      <FormControl required variant="standard" sx={{ m: 3, mt: 10, minWidth: 180 }}>
        <FacilitiesSelection
          facility={facility}
          handleChange={(newFacility) => {
            dispatch(inputSelection({ type: 'facility', value: newFacility }));
          }}
        />
      </FormControl>
      <Navigation />
    </SimpleBar>
  );
};

export default DrawerContent;
