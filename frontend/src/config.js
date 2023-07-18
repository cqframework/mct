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
export const baseUrl =
  process.env.NODE_ENV === 'development'
    ? 'http://localhost:8088'
    : 'http://a913da9435453489fb73187d9b16edc5-1641411053.us-east-1.elb.amazonaws.com';
export const cqfServerUrl = process.env.NODE_ENV === 'development' ? 'http://cqf-ruler-a:8080' : 'http://mct-cqf-ruler';
