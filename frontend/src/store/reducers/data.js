import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import organizationBundle from 'fixtures/BundleOrganization.json';
import facilityBundle from 'fixtures/BundleLocation.json';

export const fetchOrganizations = createAsyncThunk('data/fetchOrganizations', async () => {
  await new Promise((r) => setTimeout(r, 2000));
  return organizationBundle.entry.map((i) => i.resource);
});

export const fetchFacilities = createAsyncThunk('data/fetchFacilities', async () => {
  await new Promise((r) => setTimeout(r, 2000));
  return facilityBundle.entry.map((i) => i.resource);
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
  }
});

export default data.reducer;

export const {} = data.actions;
