import React, { useEffect, useState } from 'react';

import { Stack, Box, Grid, Tabs, Tab, Typography, Card, CardContent } from '@mui/material';
import { ArrowLeftOutlined, ArrowUpOutlined } from '@ant-design/icons';

import PromptChoiceCard from './PromptChoiceCard';
import MainCard from 'components/MainCard';
import DemoGraph from './DemoGraph';

import MeasureReportJson from 'fixtures/MeasureReport.json';
import MeasureReportMCT from 'fixtures/MeasureReportMCT.json';
import { useDispatch, useSelector } from 'react-redux';
import { extractDescription, gatherIndividualList, populationGather, parseStratifier } from 'utils/measureReportHelpers';
import { gatherPatientDisplayData } from 'utils/patientHelper';
import PopulationStatistics from './PopulationStatistics';
import PatientColumnChart from './PatientColumnChart';
import ValidationDataTable from './ValidationDataTable';
import { createPeriodFromQuarter } from 'utils/queryHelper';

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

const PatientInfoCard = ({ name, birthDate, gender, mrn }) => (
  <Card sx={{ minWidth: 275 }}>
    <CardContent>
      <Typography sx={{ fontSize: 20 }} color="primary.main" gutterBottom>
        {name}
      </Typography>
      <Typography variant="h5" component="div">
        {birthDate}
      </Typography>
      <Typography sx={{ mb: 1.5 }} color="text.secondary">
        {gender}
      </Typography>
      <Typography variant="body2">MRN: {mrn}</Typography>
    </CardContent>
  </Card>
);

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

const DashboardDefault = () => {
  const { facility, date, measure } = useSelector((state) => state.filter);
  const [value, setValue] = useState(0);
  const [measureReport, setMeasureReport] = useState(null);

  useEffect(() => {
    const callGatherApi = async () => {
      const parametersPayload = buildMeasurePayload(facility, measure, date);
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

  const handleChange = (event, newValue) => setValue(newValue);

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
  }

  if (measure.length === 0 || measureReport == null) {
    return (
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <PromptChoiceCard>
          <Box sx={{ display: 'flex', ml: 1, fontSize: 30 }}>
            <Typography variant="h1" gutterBottom>
              <ArrowUpOutlined /> Select a{' '}
            </Typography>
            <Typography variant="h1" sx={{ ml: 1, mr: 1, color: 'primary.main' }}>
              measure
            </Typography>
          </Box>
        </PromptChoiceCard>
      </Grid>
    );
  }

  const description = extractDescription(measureReport);
  const population = populationGather(measureReport.group[0]);
  const stratifier = parseStratifier(measureReport);

  const individualListInfo = gatherIndividualList(MeasureReportMCT);
  const patientInfo = gatherPatientDisplayData(individualListInfo.patient);

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <>
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <Tabs value={value} onChange={handleChange} aria-label="basic tabs example">
            <Tab label="Individual Data" {...a11yProps(0)} />
            <Tab label="Population Data" {...a11yProps(1)} />
            <Tab label="Demo" {...a11yProps(2)} />
          </Tabs>
        </Grid>
        <TabPanel value={value} index={0}>
          <Grid item xs={4} sx={{ mb: -2.25 }}>
            <PatientInfoCard {...patientInfo} />
          </Grid>
          <Grid item xs={8} sx={{ mb: -2.25 }}>
            <ValidationDataTable resources={individualListInfo.resources} />
          </Grid>
        </TabPanel>
        <TabPanel value={value} index={1}>
          <Grid item xs={12} sx={{ mb: -2.25 }}>
            <Typography variant="h1">Diabetes Report Population Data Demographics</Typography>
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
        <TabPanel value={value} index={2}>
          <DemoGraph />
        </TabPanel>
      </>
    </Grid>
  );
};

export default DashboardDefault;
