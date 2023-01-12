import { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useTheme } from '@mui/material/styles';
import { Box, Toolbar, useMediaQuery } from '@mui/material';
import Drawer from './Drawer';
import Header from './Header';
import navigation from 'menu-items';
import Breadcrumbs from 'components/@extended/Breadcrumbs';
import { openDrawer } from 'store/reducers/filter';
import { fetchFacilities } from 'store/reducers/data';
import LoadingPage from 'components/LoadingPage';

const MainLayout = () => {
  const theme = useTheme();
  const { facilities, status } = useSelector((state) => state.data);

  const matchDownLG = useMediaQuery(theme.breakpoints.down('sm'));
  const dispatch = useDispatch();

  const { drawerOpen, facility, date, measure } = useSelector((state) => state.filter);

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

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [matchDownLG]);

  useEffect(() => {
    if (open !== drawerOpen) setOpen(drawerOpen);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [drawerOpen]);

  useEffect(() => {
    if (status === 'idle') {
      console.log('gathering facilities');
      dispatch(fetchFacilities());
    }
  }, [dispatch, status]);
  if (facilities.length === 0) {
    return <LoadingPage message={'Retrieving Facilities'} />;
  }

  return (
    <Box sx={{ display: 'flex', width: '100%' }}>
      {facility.length > 0 && <Header open={open} handleDrawerToggle={handleDrawerToggle} />}
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
