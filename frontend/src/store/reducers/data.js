import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import organizationBundle from 'fixtures/BundleOrganization.json';
import facilityBundle from 'fixtures/BundleLocation.json';
// import measureBundle from 'fixtures/Measure.json';

const BASE_URL = process.env.BASE_URL || 'http://localhost:8088'

//TODO: Hookup apis here
export const fetchOrganizations = createAsyncThunk('data/fetchOrganizations', async () => {
  await new Promise((r) => setTimeout(r, 1500));
  return organizationBundle.entry.map((i) => i.resource);
});

export const fetchFacilities = createAsyncThunk('data/fetchFacilities', async (organizationId) => {
  await new Promise((r) => setTimeout(r, 2000));
  return facilityBundle.entry.map((i) => i.resource);
});


export const fetchMeasures = createAsyncThunk('data/fetchMeasures', async (facilityId) => {
  const measureBundle = await fetch(`${BASE_URL}/mct/$list-measures`).then((res) => res.json());
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
        state.status = 'succeeded';
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
        state.status = 'succeeded';
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
