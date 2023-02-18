import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Box, Grid, Tabs, Tab, Typography } from '@mui/material';
import { ArrowLeftOutlined, ArrowUpOutlined } from '@ant-design/icons';

import PromptChoiceCard from './PromptChoiceCard';
import LoadingPage from 'components/LoadingPage';
import { processMeasureReportPayload } from 'utils/measureReportHelpers';

import IndividualMeasureReport from './IndividualMeasureReport';
import PopulationMeasureReport from './PopulationMeasureReport';

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

  const [value, setValue] = useState(0);

  const measureResource = measures.find((i) => i.id === measure);

  useEffect(() => {
    console.log('Resetting Tabs back to first tab');
    setValue(0);
  }, [measureReport]);

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
    return <LoadingPage message={'Retrieving Measure Report'} />;
  }
  const isPopulationMeasureReport = measureReport?.parameter?.length > 1;
  const processedMeasureReport = processMeasureReportPayload(measureReport);

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <>
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <Tabs value={value} fixed onChange={(event, newValue) => setValue(newValue)}>
            <Tab label={isPopulationMeasureReport ? 'Population Report Data' : processedMeasureReport?.name} {...a11yProps(0)} />
            {isPopulationMeasureReport &&
              processedMeasureReport?.individualLevelData?.map((i, index) => <Tab label={`${i.name}`} {...a11yProps(index + 1)} />)}
          </Tabs>
        </Grid>
        <TabPanel value={value} index={0}>
          {isPopulationMeasureReport ? (
            <PopulationMeasureReport processedMeasureReport={processedMeasureReport} />
          ) : (
            <IndividualMeasureReport processedMeasureReport={processedMeasureReport} measureName={measureResource.title} />
          )}
        </TabPanel>
        {processedMeasureReport?.individualLevelData?.map((i, index) => (
          <TabPanel value={value} index={index + 1}>
            <IndividualMeasureReport processedMeasureReport={i} measureName={measureResource.title} />
          </TabPanel>
        ))}
      </>
    </Grid>
  );
};

export default DashboardDefault;
