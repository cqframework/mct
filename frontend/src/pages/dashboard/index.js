import React, { useState } from 'react';

// material-ui
import { Stack, Box, Button, Grid, TextField, Tabs, Tab, Typography } from '@mui/material';

import AnalyticEcommerce from 'components/cards/statistics/AnalyticEcommerce';
import PromptChoiceCard from './PromptChoiceCard';
import MainCard from 'components/MainCard';
import DemoGraph from './DemoGraph';

import measureReportJson from 'fixtures/MeasureReport.json';
import { useDispatch, useSelector } from 'react-redux';
import { extractDescription } from 'utils/measureReportHelpers';
import PopulationStatistics from './PopulationStatistics';
import PatientColumnChart from './PatientColumnChart';

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

const populationGather = (measureReportGroup) => {
  const population = {};
  measureReportGroup?.population?.forEach((data) => {
    const key = data.code.coding?.[0]?.code;

    population[key] = {
      ...data.code.coding?.[0],
      id: data.id,
      count: data.count,
      reference: data.subjectResults?.reference,
      description: data.extension?.[0]?.valueString
    };
  });

  return population;
};

const DashboardDefault = () => {
  const { facility, date, measure } = useSelector((state) => state.filter);
  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };
  if ((date?.length == 0 || measure?.length == 0) && facility.length !== 0) {
    return null;
  }
  const description = extractDescription(measureReportJson);
  const population = populationGather(measureReportJson.group[0]);

  const stratifier = {};
  measureReportJson.group[0].stratifier.forEach((data) => {
    const stratKey = data.code[0].coding?.[0]?.code;
    const stratumData = {};
    data?.stratum?.forEach((stratum) => {
      const key = stratum.value.text;
      stratumData[key] = populationGather(stratum);
    });

    stratifier[stratKey] = {
      ...data.code[0].coding?.[0],
      data: stratumData,
      title: data.extension?.[0]?.valueString
    };
  });

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      {facility.length === 0 ? (
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <PromptChoiceCard />
        </Grid>
      ) : (
        <>
          <Grid item xs={12} sx={{ mb: -2.25 }}>
            <Tabs value={value} onChange={handleChange} aria-label="basic tabs example">
              <Tab label="MeasureReport Data" {...a11yProps(0)} />
              <Tab label="Demo" {...a11yProps(1)} />
            </Tabs>
          </Grid>
          <TabPanel value={value} index={0}>
            <Grid item xs={12} sx={{ mb: -2.25 }}>
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
            </Grid>
          </TabPanel>
          <TabPanel value={value} index={1}>
            <DemoGraph />
          </TabPanel>
        </>
      )}
    </Grid>
  );
};

export default DashboardDefault;
