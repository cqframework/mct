import React, { useEffect, useState } from 'react';

import { Box, Grid, Tabs, Tab, Typography } from '@mui/material';
import { ArrowLeftOutlined, ArrowUpOutlined } from '@ant-design/icons';
import PromptChoiceCard from './PromptChoiceCard';
import { useSelector } from 'react-redux';
import LoadingPage from 'components/LoadingPage';
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
    return <LoadingPage message={'Retrieving Measure Report'} />;
  }
  const isInvididualView = measureReport?.parameter?.length === 1;
  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <>
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <Tabs value={value} fixed onChange={(event, newValue) => setValue(newValue)}>
            <Tab label={isInvididualView ? 'Individual Data' : 'Population Report Data'} {...a11yProps(0)} />
          </Tabs>
        </Grid>
        <TabPanel value={value} index={0}>
          {isInvididualView ? (
            <IndividualMeasureReport measureReportPayload={measureReport} measureName={measureResource.title} />
          ) : (
            <PopulationMeasureReport measureReport={measureReport} />
          )}
        </TabPanel>
      </>
    </Grid>
  );
};

export default DashboardDefault;
