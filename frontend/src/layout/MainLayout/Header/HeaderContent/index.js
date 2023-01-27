import { InputLabel, Select, FormControl, Typography, MenuItem, Box } from '@mui/material';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { inputSelection } from 'store/reducers/filter';
import LoadingButton from '@mui/lab/LoadingButton';
import { fetchMeasures } from 'store/reducers/data'

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
      <MenuItem value={'q1'}>Q1</MenuItem>
      <MenuItem value={'q2'}>Q2</MenuItem>
      <MenuItem value={'q3'}>Q3</MenuItem>
      <MenuItem value={'q4'}>Q4</MenuItem>
    </Select>
  </>
);

const MeasuresSelection = ({ measures = [], measure, isLoading, handleChange }) => {
  if (isLoading) {
    return (
      <LoadingButton 
        loading={true}
        sx={{height: '41.13px', color: '#1890ff !important' }}
        loadingPosition="end"
        variant="contained"
        >
        <Typography variant="span" sx={{ mr: 2 }}>Fetching Measures ...</Typography >
        </LoadingButton>

    )
  }

  return (
    <>
      <InputLabel id="measures-select-label">Measures</InputLabel>
      <Select
        labelId="measures-select-label"
        id="measures-select"
        value={measure}
        onChange={(e) => handleChange(e.target.value)}
      >
        {measures.map(({ id, title }) => (
          <MenuItem key={id} value={id}>
            {title}
          </MenuItem>
        ))}
      </Select>
    </>
  );
};

const HeaderContent = () => {
  const { date, measure, facility } = useSelector((state) => state.filter);
  const { measures, status } = useSelector((state) => state.data);
  const [isLoading, setIsLoading] = useState(measures.length === 0)
  const dispatch = useDispatch();

  useEffect(() => {
    if (measures.length === 0 && status === 'succeeded') {
      dispatch(fetchMeasures(facility))
    } else {
      setIsLoading(false)
    }
  }, [measures, status])

  return (
    <Box sx={{ flexGrow: 1 }}>
      <FormControl
        required
        sx={{
          m: 1,
          minWidth: 200
        }}
      >
        <MeasuresSelection
          measures={measures}
          measure={measure}
          handleChange={(value) => dispatch(inputSelection({ type: 'measure', value }))} 
          isLoading={isLoading}
        />
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
