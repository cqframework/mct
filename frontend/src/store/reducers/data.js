import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { inputSelection } from './filter';
import { baseUrl } from 'config';

export const fetchOrganizations = createAsyncThunk('data/fetchOrganizations', async () => {
  const organizationBundle = await fetch(`${baseUrl}/mct/$list-organizations`).then((res) => res.json());
  return organizationBundle.entry.map((i) => i.resource);
});

export const fetchFacilities = createAsyncThunk('data/fetchFacilities', async (organizationId, { dispatch }) => {
  const facilityBundle = await fetch(`${baseUrl}/mct/$list-facilities`).then((res) => res.json());
  await new Promise((r) => setTimeout(r, 1000));
  const mappedFacilities = facilityBundle.entry.map((i) => i.resource);
  const firstFacility = mappedFacilities?.[0]?.id // set first one as default
  dispatch(inputSelection({ type: "facility", value: firstFacility }))
  return mappedFacilities
});


export const fetchMeasures = createAsyncThunk('data/fetchMeasures', async (facilityId) => {
  const measureBundle = await fetch(`${baseUrl}/mct/$list-measures`).then((res) => res.json());
  return measureBundle.entry.map((i) => i.resource);
});

const initialState = {
  facilities: [],
  organizations: [],
  measures: [],
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
        state.facilities = state.facilities = action.payload;
      })
      .addCase(fetchFacilities.rejected, (state, action) => {
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
        state.organizations = state.organizations = action.payload;
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
        state.measures = state.measures = action.payload;
      })
      .addCase(fetchMeasures.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.error.message;
      });
  }
});

export default data.reducer;

export const {} = data.actions;
