import { InputLabel, Select, FormControl, Typography, MenuItem, Box } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import LoadingButton from '@mui/lab/LoadingButton';

const DateSelection = ({ date, handleChange }) => (
  <>
    <InputLabel id="date-select-label">Date Range</InputLabel>
    <Select
      labelId="date-select-label"
      id="date-select"
      defaultValue="q1"
      value={date}
      label="Date Range"
      onChange={(e) => handleChange(e.target.value)}
    >
      <MenuItem value={'q1'}>2022 - Q1</MenuItem>
      <MenuItem value={'q2'}>2022 - Q2</MenuItem>
      <MenuItem value={'q3'}>2022 - Q3</MenuItem>
      <MenuItem value={'q4'}>2022 - Q4</MenuItem>
    </Select>
  </>
);

const HeaderContent = () => {
  const { date, } = useSelector((state) => state.filter);
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
        <DateSelection date={date} handleChange={(value) => dispatch(inputSelection({ type: 'date', value }))} />
      </FormControl>
    </Box>
  );
};

export default HeaderContent;
