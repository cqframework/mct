import { InputLabel, Select, MenuItem } from '@mui/material';

const Selection = ({ currentSelection, handleChange, options, label }) => {
  return (
    <>
      <InputLabel id={`${label}-select-label`}>{label}</InputLabel>
      <Select
        labelId={`${label}-select-label`}
        id={`${label}-select`}
        value={currentSelection}
        label={label}
        onChange={(e) => handleChange(e?.target?.value)}
      >
        {options?.map((facility) => (
          <MenuItem key={facility.id} value={facility.id}>
            {facility.name}
          </MenuItem>
        ))}
      </Select>
    </>
  );
};

export default Selection