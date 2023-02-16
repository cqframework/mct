import { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useTheme } from '@mui/material/styles';
import { Box, Toolbar, useMediaQuery, Typography } from '@mui/material';
import Drawer from './Drawer';
import Header from './Header';
import navigation from 'menu-items';
import Breadcrumbs from 'components/@extended/Breadcrumbs';
import { openDrawer } from 'store/reducers/filter';
import { fetchOrganizations, fetchFacilities, fetchMeasures, fetchPatients } from 'store/reducers/data';
import LoadingPage from 'components/LoadingPage';
import OrganizationSelection from './OrganizationSelection';
import { inputSelection } from 'store/reducers/filter';
import { isEqual } from 'lodash';
const MainLayout = () => {
  const theme = useTheme();
  const { drawerOpen, measure, organization } = useSelector((state) => state.filter, isEqual);
  const { organizations, facilities, status } = useSelector((state) => state.data);

  const matchDownLG = useMediaQuery(theme.breakpoints.down('sm'));
  const dispatch = useDispatch();

  // drawer toggler
  const [open, setOpen] = useState(drawerOpen);
  const handleDrawerToggle = () => {
    setOpen(!open);
    dispatch(openDrawer({ drawerOpen: !open }));
  };

  // set media wise responsive drawer
  useEffect(() => {
    setOpen(!matchDownLG);
    dispatch(openDrawer({ drawerOpen: !matchDownLG }));
  }, [matchDownLG]);

  useEffect(() => {
    if (open !== drawerOpen) setOpen(drawerOpen);
  }, [drawerOpen]);

  useEffect(() => {
    if (organizations.length === 0 && status === 'idle') {
      dispatch(fetchOrganizations());
    } else if (organization.length !== 0 && status === 'succeeded') {
      dispatch(fetchMeasures());
      dispatch(fetchPatients(organization));
      dispatch(fetchFacilities(organization));
    }
  }, [dispatch, organization, organizations, status]);

  if (organizations.length === 0) {
    return <LoadingPage message={'Retrieving Organizations'} />;
  } else if (organization.length === 0) {
    return (
      <OrganizationSelection
        organizations={organizations}
        handleChange={(newOrganization) => {
          dispatch(inputSelection({ type: 'organization', value: newOrganization }));
        }}
      />
    );
  } else if (facilities.length === 0) {
    const organizationName = organizations.find((org) => org.id === organization)?.name;
    return (
      <LoadingPage>
        <Typography variant="h1">Retrieving Facilties and Measures from</Typography>
        <Typography sx={{ color: 'primary.main' }} variant="h1">
          {organizationName}
        </Typography>
      </LoadingPage>
    );
  }

  return (
    <Box sx={{ display: 'flex', width: '100%' }}>
      {measure.length > 0 && <Header open={open} handleDrawerToggle={handleDrawerToggle} />}
      <Drawer open={open} handleDrawerToggle={handleDrawerToggle} />
      <Box component="main" sx={{ width: '100%', flexGrow: 1, p: { xs: 2, sm: 3 } }}>
        <Toolbar />
        <Breadcrumbs navigation={navigation} title titleBottom card={false} divider={false} />
        <Outlet />
      </Box>
    </Box>
  );
};

export default MainLayout;
