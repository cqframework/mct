import * as React from 'react';
import { useSelector } from 'react-redux';
import { summarizeMeasureReport } from 'utils/measureReportHelpers';
import { Typography, Grid, Stack, Button, Dialog, Box, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import { getFacility, getOrganization } from 'store/reducers/selector';
import SeverityIcon from './SeverityIcon';
import { baseUrl, cqfServerUrl } from 'config';
import LoadingButton from '@mui/lab/LoadingButton';

export default function AlertDialog({ isVisible, setVisibility, setStatusMessage }) {
  const [loading, setLoading] = React.useState(false);
  const { measureReport } = useSelector((state) => state.data);
  const facilities = useSelector((state) => getFacility(state));
  const organization = useSelector((state) => getOrganization(state));
  const summaryStats = summarizeMeasureReport(measureReport);

  const handleClose = ({ isSubmit }) => {
    setVisibility(false);
    if (isSubmit) {
      setStatusMessage(true);
    }
  };
  const handleSubmit = async () => {
    setLoading(true);
    await fetch(`${baseUrl}/mct/$submit?organization=${organization?.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        resourceType: 'Parameters',
        parameter: [
          {
            name: 'receivingSystemUrl',
            valueString: `${cqfServerUrl}/fhir`
          },
          {
            name: 'gatherResult',
            resource: measureReport
          }
        ]
      })
    });
    setLoading(false);
    handleClose({ isSubmit: true });
  };

  const facilityNames = facilities.map((i) => i.name).join(', ');

  return (
    <div>
      <Dialog open={isVisible} onClose={handleClose} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
        <DialogTitle id="alert-dialog-title">
          <Typography variant="h2">Measure Report Submission Summary</Typography>
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ display: 'flex' }} id="alert-dialog-description">
            <Typography variant="subtitle2"> Are you sure you want to submit Measure Report for </Typography>
            <Typography variant="subtitle2" sx={{ ml: 0.25 }} color={'primary.main'}>
              {organization?.name}
            </Typography>
          </DialogContentText>
          <Grid container>
            <Grid sx={{ display: 'flex', justifyContent: 'center' }} item xs={12}>
              <Typography variant="h4">Stats for facility:</Typography>
              <Typography sx={{ ml: 0.25 }} variant="h4" color="primary.main">
                {facilityNames}
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <DisplayBox
                bgColor={'#DCEBF8'}
                sx={{ borderRadius: '20px', border: '1px solid #0462BC', minHeight: '120px'  }}
                primaryColor={'#0462BC'}
                count={summaryStats.patientCount}
                resourceType={'Patients'}
              />
            </Grid>
            {Object.keys(summaryStats?.resources).map((resourceType) => {
              const hasIssues = Object.entries(summaryStats.resources[resourceType]).reduce((acc, [k, v]) => {
                return k !== 'count' ? v + acc : acc
              }, 0);
              return (
              <Grid item xs={6}>
                <DisplayBox
                  bgColor={hasIssues ? '#FEF3DF': '#E9FDF3'}
                  sx={{ borderRadius: '20px', border: `1px solid ${hasIssues ? '#C66A10': '#308367'}`,  maxHeight: '120px' }}
                  primaryColor={hasIssues ? '#C66A10': '#308367'}
                  count={summaryStats.resources[resourceType].count}
                  resourceType={resourceType}
                  severityCountMap={summaryStats.resources[resourceType]}
                />
              </Grid>
            )}
            )}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button
            sx={{
              '&:hover': {
                border: '1px solid #0462BC'
              }
            }}
            onClick={handleClose}
          >
            Cancel
          </Button>
          <LoadingButton sx={{ border: 'none' }} loading={loading} onClick={handleSubmit} variant="outlined">
            Submit
          </LoadingButton>
        </DialogActions>
      </Dialog>
    </div>
  );
}

const DisplayBox = ({ count, resourceType, sx, severityCountMap = null, bgColor, primaryColor }) => {
  return (
    <Box sx={{ maxWidth: 250, minHeight: 100, padding: 1, mt: 2, backgroundColor: bgColor, ...sx }}>
      <Grid container>
        <Grid item xs={3}>
          <Typography variant="h2" color={primaryColor}>
            {count}
          </Typography>
        </Grid>
        <Grid item xs={1}>
          <Grid item xs={12}>
            <Typography variant="h4" sx={{ fontSize: '1vw' }} color="#555759">
              {resourceType}
            </Typography>
          </Grid>
          {severityCountMap != null && (
            <Stack sx={{ width: '100px' }} item xs={12}>
              <Typography variant="h6">Issues</Typography>
              <Typography variant="h6">
                <SeverityIcon severity="information" /> {severityCountMap['information']}
              </Typography>
              <Typography variant="h6">
                <SeverityIcon severity="error" /> {severityCountMap['error']}
              </Typography>
              <Typography variant="h6">
                <SeverityIcon severity="warning" /> {severityCountMap['warning']}
              </Typography>
            </Stack>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};
