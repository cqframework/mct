import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  facility: '',
  date: 'q1',
  measure: '',

  openItem: ['dashboard'],
  openComponent: 'buttons',
  drawerOpen: true,
  componentDrawerOpen: true
};

const filter = createSlice({
  name: 'filter',
  initialState,
  reducers: {
    activeItem(state, action) {
      state.openItem = action.payload.openItem;
    },

    activeComponent(state, action) {
      state.openComponent = action.payload.openComponent;
    },

    openDrawer(state, action) {
      state.drawerOpen = action.payload.drawerOpen;
    },

    inputSelection(state, action) {
      state[action.payload.type] = action.payload.value;
    },

    openComponentDrawer(state, action) {
      state.componentDrawerOpen = action.payload.componentDrawerOpen;
    }
  }
});

export default filter.reducer;

export const { activeItem, activeComponent, openDrawer, openComponentDrawer, inputSelection } = filter.actions;
