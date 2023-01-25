import { Box, Container, Typography, Select, FormControl, MenuItem } from '@mui/material';

const OrganizationSelection = ({ organizations, handleChange, currentOrganization }) => (
  <Container
    sx={{
      display: 'flex',
      flexDirection: 'column',
      padding: '10%'
    }}
    maxWidth="sm"
  >
    <Typography variant="h1" sx={{ mb: 10 }}>
      MCT Measure Calculation Tool
    </Typography>
    <FormControl required variant="standard" sx={{ m: 3, mt: 10, minWidth: 180 }}>
      <Typography variant="h4">Select your Organization</Typography>
      <Select
        labelId="organizations-select-label"
        id="organizations-select"
        value={currentOrganization}
        label="organizations"
        onChange={(e) => handleChange(e?.target?.value)}
      >
        <MenuItem disabled value="">
          <em>Select one</em>
        </MenuItem>
        {organizations?.map((organization) => (
          <MenuItem key={organization.id} value={organization.id}>
            {organization.name}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  </Container>
);

export default OrganizationSelection;
