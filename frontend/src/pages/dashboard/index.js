import React, { useEffect, useState } from 'react';

import { Box, Grid, Tabs, Tab, Typography, Card, CardContent } from '@mui/material';
import { ArrowLeftOutlined, ArrowUpOutlined } from '@ant-design/icons';
import PromptChoiceCard from './PromptChoiceCard';
import { useDispatch, useSelector } from 'react-redux';
import { createPeriodFromQuarter } from 'utils/queryHelper';
import { baseUrl } from 'config';
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

const buildMeasurePayload = (facilityId, measureResourceUrl, quarter) => {
  const period = createPeriodFromQuarter(quarter);
  return {
    resourceType: 'Parameters',
    parameter: [
      {
        name: 'facilities',
        valueString: `Location/${facilityId}`
      },
      {
        name: 'period',
        valuePeriod: period
      },
      {
        name: 'measure',
        valueString: measureResourceUrl
      },
      {
        name: 'patients',
        resource: {
          resourceType: 'Group',
          id: '102',
          type: 'person',
          actual: true,
          member: [
            {
              entity: {
                reference: 'Patient/denom-EXM104'
              }
            }
          ]
        }
      }
    ]
  };
};

const DashboardDefault = () => {
  const { facility, date, measure, patient } = useSelector((state) => state.filter);
  const { facilities, measures, patients } = useSelector((state) => state.data);
  const [value, setValue] = useState(0);
  const [measureReport, setMeasureReport] = useState(null);
  const measureResource = measures.find((i) => i.id === measure);

  useEffect(() => {
    const callGatherApi = async () => {
      const facilityResource = facilities.find((i) => i.id === facility);
      const parametersPayload = buildMeasurePayload(facilityResource.id, measureResource.url, date);
      try {
        const measureReportJson = await fetch(`${baseUrl}/mct/$gather`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(parametersPayload)
        }).then((response) => response?.json());

        if (measureReportJson?.resourceType === 'OperationOutcome') {
          console.error(measureReportJson);
        } else {
          setMeasureReport(measureReportJson?.parameter?.[0]?.resource);
        }
      } catch (err) {
        console.error(err);
      }
    };

    if (date?.length != 0 && measure?.length != 0) {
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

  if (patient.length === 0) {
    return (
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <PromptChoiceCard>
          <Box sx={{ display: 'flex', mt: 10, fontSize: 30 }}>
            <Typography variant="h1" gutterBottom>
              <ArrowLeftOutlined /> Select a
            </Typography>
            <Typography variant="h1" sx={{ ml: 1, mr: 1, color: 'primary.main' }}>
              Patient
            </Typography>
            <Typography variant="h1" gutterBottom>
              to Begin
            </Typography>
          </Box>
        </PromptChoiceCard>
      </Grid>
    );
  }

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <>
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <Tabs value={value} onChange={handleChange} aria-label="basic tabs example">
            <Tab label="Patient Data" {...a11yProps(0)} />
            <Tab label="Population Report Data" {...a11yProps(1)} />
          </Tabs>
        </Grid>
        <TabPanel value={value} index={0}>
          <IndividualMeasureReport measureReport={measureReport} measureName={measureResource.title} />
        </TabPanel>
        <TabPanel value={value} index={1}>
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
      </>
    </Grid>
  );
};

export default DashboardDefault;
