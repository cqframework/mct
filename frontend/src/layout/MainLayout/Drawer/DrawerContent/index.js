// project import
import Navigation from './Navigation';
import SimpleBar from 'components/third-party/SimpleBar';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import Selection from 'components/Selection';
import { FormControl } from '@mui/material';

const DrawerContent = () => {
  const { facility } = useSelector((state) => state.filter);
  const { facilities } = useSelector((state) => state.data);
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
        <Selection
          facilities={facilities}
          label="Measures"
          currentFacility={facility}
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
