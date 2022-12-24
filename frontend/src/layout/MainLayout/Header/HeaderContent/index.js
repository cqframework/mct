import { InputLabel, Select, FormControl, MenuItem, Box } from '@mui/material';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';

// Fixture
import measureBundle from 'fixtures/Measure.json';

const DateSelection = ({ date, handleChange }) => {
  return (
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
        <MenuItem value={'q1'}>Q1</MenuItem>
        <MenuItem value={'q2'}>Q2</MenuItem>
        <MenuItem value={'q3'}>Q3</MenuItem>
        <MenuItem value={'q4'}>Q4</MenuItem>
      </Select>
    </>
  );
};

const MeasuresSelection = ({ measure, handleChange }) => {
  const items = measureBundle?.entry?.map(({ resource }) => {
    const { id, title, url } = resource;
    return {
      id,
      url,
      title
    };
  });

  return (
    <>
      <InputLabel id="measures-select-label">Measures</InputLabel>
      <Select
        labelId="measures-select-label"
        id="measures-select"
        value={measure}
        label="Measures"
        onChange={(e) => handleChange(e.target.value)}
      >
        {items.map(({ id, title }) => (
          <MenuItem key={id} value={id}>
            {title}
          </MenuItem>
        ))}
      </Select>
    </>
  );
};

const HeaderContent = () => {
  const { facility, date, measure } = useSelector((state) => state.filter);
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
        <MeasuresSelection measure={measure} handleChange={(value) => dispatch(inputSelection({ type: 'measure', value }))} />
      </FormControl>
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
