import { FormControl, Box } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import Selection from 'components/Selection';
import { Button } from '@mui/material';
import PatientMultiSelect from 'components/PatientMultiSelect';
import { executeGatherOperation } from 'store/reducers/data';
import { SendOutlined } from '@ant-design/icons';
import FacilitiesMultiSelect from 'components/FacilitiesMultiSelect';
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
  const { date, facility } = useSelector((state) => state.filter);
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
        <PatientMultiSelect patients={patients} />
      </FormControl>
      <FormControl
        required
        sx={{
          m: 1,
          minWidth: 200
        }}
      >
        <FacilitiesMultiSelect facilities={facilities} />
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
      <Button
        onClick={() => dispatch(executeGatherOperation())}
        sx={{ lineHeight: '1.85rem', mt: 1 }}
        variant="contained"
        endIcon={<SendOutlined />}
      >
        Get Report
      </Button>
    </Box>
  );
};

export default HeaderContent;
