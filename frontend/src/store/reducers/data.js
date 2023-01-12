import { createSlice, nanoid, createAsyncThunk } from '@reduxjs/toolkit';

export const fetchFacilities = createAsyncThunk('data/fetchFacilities', async () => {
  await new Promise((r) => setTimeout(r, 2000));
  return [
    {
      resourceType: 'Organization',
      id: '2',
      text: {
        status: 'generated',
        div: '<div xmlns="http://www.w3.org/1999/xhtml">\n      \n      <p>XYZ Insurance</p>\n    \n    </div>'
      },
      identifier: [
        {
          system: 'urn:oid:2.16.840.1.113883.3.19.2.3',
          value: '666666'
        }
      ],
      name: 'XYZ Insurance',
      alias: ['ABC Insurance']
    }
  ];
});

const initialState = {
  facilities: [],
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
  }
});

export default data.reducer;

export const {} = data.actions;
