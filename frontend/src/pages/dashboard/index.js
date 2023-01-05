import React, { useEffect, useState } from 'react';

import { Stack, Box, Button, Grid, TextField, Tabs, Tab, Typography } from '@mui/material';

import PromptChoiceCard from './PromptChoiceCard';
import MainCard from 'components/MainCard';
import DemoGraph from './DemoGraph';

import MeasureReportJson from 'fixtures/MeasureReport.json';
import MeasureReportMCT from 'fixtures/MeasureReportMCT.json';
import { useDispatch, useSelector } from 'react-redux';
import { extractDescription } from 'utils/measureReportHelpers';
import PopulationStatistics from './PopulationStatistics';
import PatientColumnChart from './PatientColumnChart';
import moment from 'moment';

const createPeriodFromQuarter = (quarter) => {
  let start, end;
  switch (quarter) {
    case 'q1':
      start = moment('Janurary 1, 2023').startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment('Janurary 1, 2023').endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
    case 'q2':
      start = moment('April 1, 2023').startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment('April 1, 2023').endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
    case 'q3':
      start = moment('July 1, 2023').startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment('July 1, 2023').endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
    case 'q4':
      start = moment('October 1, 2023').startOf('quarter').startOf('day').format('MM-DD-YYYY');
      end = moment('October 1, 2023').endOf('quarter').startOf('day').format('MM-DD-YYYY');
      break;
  }
  return { start, end };
};

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

const buildMeasurePayload = (facility, measure, quarter) => {
  const period = createPeriodFromQuarter(quarter);
  const measureResource = measure; // TODO: need to get actual measure resource
  return {
    resourceType: 'Parameters',
    parameter: [
      {
        name: 'facility',
        valueString: facility
      },
      {
        name: 'period',
        period
      },
      {
        name: 'measure',
        resource: measureResource
      },
      {
        name: 'patient',
        resource: null // TODO: Need to get hardcoded list of group patients
      }
    ]
  };
};

const parseMeasureReport = (measureReport) => {

  const { type } = measureReport;
  switch(type) {
    case 'individual':
      break;
    case 'subject-list':
      break;
    case 'summary':
      break;
    default:
      break;
  };
}

const DashboardDefault = () => {
  const { facility, date, measure } = useSelector((state) => state.filter);
  const [value, setValue] = useState(0);
  const [measureReport, setMeasureReport] = useState(null);

  useEffect(() => {
    const callGatherApi = async () => {
      const parametersPayload = buildMeasurePayload(facility, measure, date);
      console.log(facility, measure, date);
      // const measureReportJson = await fetch('api/$gather', {
      //   method: 'POST',
      //   headers: {
      //     'Content-Type': 'application/json'
      //   },
      //   body: JSON.stringify(parametersPayload)
      // }).then((response) => response.json());
      setMeasureReport(MeasureReportJson);
    };

    if (date?.length != 0 && measure?.length != 0 && facility.length !== 0) {
      callGatherApi();
    }
  }, [measure, date, facility]);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  if ((date?.length == 0 || measure?.length == 0) && facility.length !== 0 || measureReport == null) {
    return null;
  }
  const description = extractDescription(measureReport);
  const population = populationGather(measureReport.group[0]);

  const stratifier = {};
  measureReport.group[0].stratifier.forEach((data) => {
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
          <PromptChoiceCard message={"Select a facility to Begin"} />
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
