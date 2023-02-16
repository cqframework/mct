import { FormControl, Box } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import Selection from 'components/Selection';
import PatientMultiSelect from 'components/PatientMultiSelect';

const dateOptions = [
  {
    id: 'q1',
    name: '2022 - Q1'
  },
  {
    id: 'q2',
    name: '2022 - Q2'
  },
  {
    id: 'q3',
    name: '2022 - Q3'
  },
  {
    id: 'q4',
    name: '2022 - Q4'
  }
];

const HeaderContent = () => {
  const { date, facility, selectedPatients } = useSelector((state) => state.filter);
  const { facilities, patients } = useSelector((state) => state.data);

  const dispatch = useDispatch();

  return (
    <Box sx={{ flexGrow: 1 }}>
      <FormControl
        required
        sx={{
          m: 1,
          minWidth: 200
        }}
      >
        <PatientMultiSelect
          patients={patients}
          selectedPatients={selectedPatients}
          handleChange={(value) => dispatch(inputSelection({ type: 'patient', value }))}
        />
      </FormControl>
      <FormControl
        required
        sx={{
          m: 1,
          minWidth: 200
        }}
      >
        <Selection
          options={facilities}
          label="Facilities"
          currentSelection={facility}
          handleChange={(value) => dispatch(inputSelection({ type: 'facility', value }))}
        />
      </FormControl>
      <FormControl
        required
        sx={{
          m: 1,
          minWidth: 200
        }}
      >
        <Selection
          options={dateOptions}
          label="Date Range"
          currentSelection={date}
          handleChange={(value) => dispatch(inputSelection({ type: 'date', value }))}
        />
      </FormControl>
    </Box>
  );
};

export default HeaderContent;
