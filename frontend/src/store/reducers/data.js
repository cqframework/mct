import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { inputSelection } from './filter';
import { baseUrl } from 'config';
import { createPeriodFromQuarter } from 'utils/queryHelper';

export const fetchOrganizations = createAsyncThunk('data/fetchOrganizations', async () => {
  const organizationBundle = await fetch(`${baseUrl}/mct/$list-organizations`).then((res) => res.json());
  return organizationBundle.entry.map((i) => i.resource);
});

export const fetchFacilities = createAsyncThunk('data/fetchFacilities', async (organizationId, { dispatch }) => {
  const facilityBundle = await fetch(`${baseUrl}/mct/$list-facilities?organization=${organizationId}`).then((res) => res.json());
  await new Promise((r) => setTimeout(r, 1000));
  const mappedFacilities = facilityBundle.entry.map((i) => i.resource);
  const firstFacility = mappedFacilities?.[0]?.id; // set first one as default
  dispatch(inputSelection({ type: 'selectedFacilities', value: [firstFacility] }));
  dispatch(fetchFacilityPatients());
  return mappedFacilities;
});

export const fetchMeasures = createAsyncThunk('data/fetchMeasures', async (facilityId) => {
  const measureBundle = await fetch(`${baseUrl}/mct/$list-measures`).then((res) => res.json());
  return measureBundle.entry.map((i) => i.resource);
});

export const fetchPatients = createAsyncThunk('data/fetchPatients', async (organizationId) => {
  const patientGroup = await fetch(`${baseUrl}/mct/$list-org-patients?organizationId=${organizationId}`).then((res) => res.json());
  return patientGroup;
});

export const fetchFacilityPatients = createAsyncThunk('data/fetchFacilityPatients', async (_, { getState }) => {
  const {
    filter: { selectedFacilities }
  } = getState();
  if (!selectedFacilities?.length) return [];
  const facilityParams = selectedFacilities.map((i) => `facilityIds=${i}`).join('&');
  const patientGroup = await fetch(`${baseUrl}/mct/$list-facility-patients?${facilityParams}`).then((res) => res.json());
  return patientGroup;
});

export const executeGatherOperation = createAsyncThunk('data/gatherOperation', async (_, { getState }) => {
  const {
    filter: { selectedPatients, selectedFacilities, measure, date }
  } = getState();
  const parametersPayload = buildMeasurePayload(selectedFacilities, measure, date, selectedPatients);
  const measureReportJson = await fetch(`${baseUrl}/mct/$gather`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(parametersPayload)
  }).then((response) => response?.json());
  return measureReportJson;
});

const buildMeasurePayload = (facilityIds, measureId, quarter, patients) => {
  const period = createPeriodFromQuarter(quarter);
  const groupPatientResource = {
    resourceType: 'Group',
    member: patients.map((i) => ({ entity: { reference: i } }))
  };
  const parameterLocations = facilityIds.map((id) => ({ name: 'facilities', valueString: `Location/${id}` }));
  return {
    resourceType: 'Parameters',
    parameter: [
      ...parameterLocations,
      {
        name: 'period',
        valuePeriod: period
      },
      {
        name: 'measure',
        valueString: measureId
      },
      {
        name: 'patients',
        resource: groupPatientResource
      }
    ]
  };
};

const initialState = {
  facilities: [],
  patients: [],
  organizations: [],
  measures: [],

  measureReport: null,
  status: 'idle',
  error: null
};

const data = createSlice({
  name: 'data',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
      .addCase(fetchFacilities.pending, (state, action) => {
        state.status = 'loading';
      })
      .addCase(fetchFacilities.fulfilled, (state, action) => {
        state.status = 'finalized';
        state.facilities = action.payload;
      })
      .addCase(fetchFacilities.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });

    builder
      .addCase(fetchPatients.pending, (state, action) => {
        state.status = 'loading';
      })
      .addCase(fetchPatients.fulfilled, (state, action) => {
        state.status = 'finalized';
        state.patients = action.payload;
      })
      .addCase(fetchPatients.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });

    builder
      .addCase(fetchOrganizations.pending, (state, action) => {
        state.organizations = [];
        state.status = 'loading';
      })
      .addCase(fetchOrganizations.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.organizations = action.payload;
      })
      .addCase(fetchOrganizations.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });

    builder
      .addCase(fetchMeasures.pending, (state, action) => {
        state.measures = [];
        state.status = 'loading';
      })
      .addCase(fetchMeasures.fulfilled, (state, action) => {
        state.status = 'finalized';
        state.measures = action.payload;
      })
      .addCase(fetchMeasures.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });

    builder
      .addCase(fetchFacilityPatients.pending, (state, action) => {
        state.patients = [];
        state.status = 'loading';
      })
      .addCase(fetchFacilityPatients.fulfilled, (state, action) => {
        state.status = 'finalized';
        state.patients = action.payload;
      })
      .addCase(fetchFacilityPatients.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });

    builder
      .addCase(executeGatherOperation.pending, (state, action) => {
        state.measureReport = 'pending';
      })
      .addCase(executeGatherOperation.fulfilled, (state, action) => {
        state.measureReport = action.payload;
      });
  }
});

export default data.reducer;

export const {} = data.actions;
