import React, { useEffect, useState } from 'react';

import { Box, Grid, Tabs, Tab, Typography, Card, CardContent } from '@mui/material';
import { ArrowLeftOutlined, ArrowUpOutlined } from '@ant-design/icons';
import PromptChoiceCard from './PromptChoiceCard';
import { useDispatch, useSelector } from 'react-redux';
import LoadingPage from 'components/LoadingPage';
import IndividualMeasureReport from './IndividualMeasureReport';

const TabPanel = (props) => {
  const { children, value, index, ...other } = props;
  return <>{value === index && children}</>;
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    'aria-controls': `simple-tabpanel-${index}`
  };
}

const DashboardDefault = () => {
  const { measure, selectedPatients } = useSelector((state) => state.filter);
  const { measures, measureReport } = useSelector((state) => state.data);

  const [currentMeasureReport, setCurrentMeasureReport] = useState(measureReport);

  useEffect(() => {
    setCurrentMeasureReport(measureReport);
  }, [measureReport]);

  const [value, setValue] = useState(0);

  const measureResource = measures.find((i) => i.id === measure);

  if (measure.length === 0) {
    return (
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <PromptChoiceCard>
          <Box sx={{ display: 'flex', mt: 10, fontSize: 30 }}>
            <Typography variant="h1" gutterBottom>
              <ArrowLeftOutlined /> Select a
            </Typography>
            <Typography variant="h1" sx={{ ml: 1, mr: 1, color: 'primary.main' }}>
              Measure
            </Typography>
            <Typography variant="h1" gutterBottom>
              to Begin
            </Typography>
          </Box>
        </PromptChoiceCard>
      </Grid>
    );
  } else if (selectedPatients.length >= 0 && measureReport === null) {
    return (
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <PromptChoiceCard>
          <Box sx={{ display: 'flex', mt: 10, fontSize: 30 }}>
            <Typography variant="h1" gutterBottom>
              <ArrowUpOutlined /> Select
            </Typography>
            <Typography variant="h1" sx={{ ml: 1, mr: 1, color: 'primary.main' }}>
              Patient(s)
            </Typography>
            <Typography variant="h1" gutterBottom>
              and submit request for report
            </Typography>
          </Box>
        </PromptChoiceCard>
      </Grid>
    );
  } else if (measureReport === 'pending') {
    return <LoadingPage message={'Retrieving measure report'} />;
  }

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <>
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <Tabs value={value} onChange={(event, newValue) => setValue(newValue)}>
            <Tab label="Patient Data" {...a11yProps(0)} />
            <Tab label="Population Report Data" {...a11yProps(1)} />
          </Tabs>
        </Grid>
        <TabPanel value={value} index={0}>
          <IndividualMeasureReport measureReport={measureReport} measureName={measureResource.title} />
        </TabPanel>
        <TabPanel value={value} index={1}>
          {/* <Grid item xs={12} sx={{ mb: -2.25 }}>
            <Typography variant="h1">Diabetes Report Data Demographics</Typography>
            <Typography variant="p" color="textSecondary">
              {description}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={4} lg={3}>
            <PopulationStatistics population={population} />
          </Grid>
          <Grid item xs={12}>
            <Grid container alignItems="center" justifyContent="space-between">
              <Grid item>
                <Typography variant="h5">Ethnicity</Typography>
              </Grid>
            </Grid>
            <MainCard sx={{ mt: 1.75 }}>
              <Stack spacing={1.5} sx={{ mb: -12 }}>
                <Typography variant="h6" color="secondary">
                  {stratifier['54133-4'].title}
                </Typography>
              </Stack>
              <PatientColumnChart stratifier={stratifier['54133-4']} />
            </MainCard>
          </Grid> */}
        </TabPanel>
      </>
    </Grid>
  );
};

export default DashboardDefault;
