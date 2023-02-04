import { InputLabel, Select, MenuItem } from '@mui/material';

const Selection = ({ currentSelection, targetDisplayField = 'name', handleChange, options, label }) => {
  return (
    <>
      <InputLabel id={`${label}-select-label`}>{label}</InputLabel>
      <Select
        labelId={`${label}-select-label`}
        sx={{ minWidth: 200 }}
        id={`${label}-select`}
        value={currentSelection}
        label={label}
        onChange={(e) => handleChange(e?.target?.value)}
      >
        {options?.map((opts) => (
          <MenuItem key={opts.id} value={opts.id}>
            {opts[targetDisplayField]}
          </MenuItem>
        ))}
      </Select>
    </>
  );
};

export default Selection;
