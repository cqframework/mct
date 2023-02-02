import React, { useEffect, useState } from 'react';

import { Stack, Box, Grid, Tabs, Tab, Typography, Card, CardContent } from '@mui/material';
import { ArrowLeftOutlined, ArrowUpOutlined } from '@ant-design/icons';
import Selection from 'components/Selection'
import PromptChoiceCard from './PromptChoiceCard';
import MainCard from 'components/MainCard';
import Patient from 'fixtures/Patient.json'
import LoadingPage from 'components/LoadingPage'
import { useDispatch, useSelector } from 'react-redux';
import { extractDescription, gatherIndividualList, populationGather, parseStratifier } from 'utils/measureReportHelpers';
import { gatherPatientDisplayData } from 'utils/patientHelper';

import ValidationDataTable from './ValidationDataTable';
import { createPeriodFromQuarter } from 'utils/queryHelper';
import { inputSelection } from 'store/reducers/filter';

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
          "resourceType": "Group",
          "id": "102",
          "type": "person",
          "actual": true,
          "member": [
              {
                  "entity": {
                      "reference": "Patient/denom-EXM104"
                  }
              }
          ]
        }
      }
    ]
  };
};

const DashboardDefault = () => {
  const { facility, date, measure } = useSelector((state) => state.filter);
  const { facilities, measures } = useSelector((state) => state.data);
  const [value, setValue] = useState(0);
  const [measureReport, setMeasureReport] = useState(null);

  const dispatch = useDispatch();

  useEffect(() => {
    const callGatherApi = async () => {
      const facilityResource = facilities.find(i => i.id === facility)
      const measureResource = measures.find(i => i.id === measure)
      const parametersPayload = buildMeasurePayload(facilityResource.id, measureResource.url, date);
      const measureReportJson = await fetch('http://localhost:8088/mct/$gather', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(parametersPayload)
      }).then((response) => response.json());
      setMeasureReport(measureReportJson?.parameter?.[0]?.resource);
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
  const description = extractDescription(measureReport);

  const patientInfo = gatherPatientDisplayData(Patient);

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <>
        <Grid item xs={12} sx={{ mb: -2.25 }}>
          <Tabs value={value} onChange={handleChange} aria-label="basic tabs example">
            <Tab label="Patient Data" {...a11yProps(0)} />
          </Tabs>
        </Grid>
        <TabPanel value={value} index={0}>
          { measureReport == null ? <LoadingPage /> : (
            <>
          <Grid item xs={4} sx={{ mb: -2.25 }}>
            <PatientInfoCard {...patientInfo} />
          </Grid>
          <Grid item xs={8} sx={{ mb: -2.25 }}>
            <Selection
              options={facilities}
              label="Facilities"
              currentSelection={facility}
              handleChange={(newFacility) => {
                dispatch(inputSelection({ type: 'facility', value: newFacility }));
              }}
            />
            <ValidationDataTable resources={gatherIndividualList(measureReport).resources} />
          </Grid>
          </>
          )}
        </TabPanel>
      </>
    </Grid>
  );
};

export default DashboardDefault;
