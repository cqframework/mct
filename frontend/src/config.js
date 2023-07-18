// ==============================|| THEME CONFIG  ||============================== //

const config = {
  defaultPath: '/dashboard/default',
  fontFamily: `'Public Sans', sans-serif`,
  i18n: 'en',
  miniDrawer: false,
  container: true,
  mode: 'light',
  presetColor: 'default',
  themeDirection: 'ltr'
};

export default config;
export const drawerWidth = 260;
export const baseUrl = process.env.NODE_ENV === 'development' ? 'http://localhost:8088' : 'http://URL-REMOVED';
export const cqfServerUrl = process.env.NODE_ENV === 'development' ? 'http://cqf-ruler:8080' : 'http://mct-cqf-ruler';
