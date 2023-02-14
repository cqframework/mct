// project import
import Navigation from './Navigation';
import SimpleBar from 'components/third-party/SimpleBar';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import Selection from 'components/Selection';
import { FormControl } from '@mui/material';

const DrawerContent = () => {
  const { measure } = useSelector((state) => state.filter);
  const { measures } = useSelector((state) => state.data);

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
          options={measures}
          label="Measures"
          targetDisplayField="title"
          currentSelection={measure}
          handleChange={(newMeasure) => {
            dispatch(inputSelection({ type: 'measure', value: newMeasure }));
          }}
        />
      </FormControl>
      <Navigation />
    </SimpleBar>
  );
};

export default DrawerContent;
