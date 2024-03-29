import { useState, forwardRef } from 'react';
import PropTypes from 'prop-types';
import { useSelector, useDispatch } from 'react-redux';

import { useTheme } from '@mui/material/styles';
import { AppBar, IconButton, Toolbar, useMediaQuery, Button } from '@mui/material';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import { MenuFoldOutlined, MenuUnfoldOutlined, CloudUploadOutlined } from '@ant-design/icons';

import AppBarStyled from './AppBarStyled';
import HeaderContent from './HeaderContent';
import AlertDialog from 'components/AlertDialog';

const Alert = forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const Header = ({ open, handleDrawerToggle }) => {
  const theme = useTheme();
  const [openSubmitPrompt, setOpenSubmitPrompt] = useState(false);
  const [isStatusMessageVisible, setIsStatusMessageVisible] = useState(false);
  const matchDownMD = useMediaQuery(theme.breakpoints.down('lg'));
  const { measureReport } = useSelector((state) => state.data);

  const iconBackColor = 'grey.100';
  const iconBackColorOpen = 'grey.200';

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }

    setIsStatusMessageVisible(false);
  };

  const mainHeader = (
    <Toolbar>
      <IconButton
        disableRipple
        aria-label="open drawer"
        onClick={handleDrawerToggle}
        edge="start"
        color="secondary"
        sx={{ color: 'text.primary', bgcolor: open ? iconBackColorOpen : iconBackColor, ml: { xs: 0, lg: -2 } }}
      >
        {!open ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
      </IconButton>
      <HeaderContent />
      <Snackbar
        open={isStatusMessageVisible}
        autoHideDuration={3000}
        onClose={handleClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert severity="success" sx={{ width: '100%', color: 'white' }}>
          Successful Submission of Measure Report
        </Alert>
      </Snackbar>

      {measureReport && measureReport !== 'pending' && (
        <>
          <AlertDialog isVisible={openSubmitPrompt} setVisibility={setOpenSubmitPrompt} setStatusMessage={setIsStatusMessageVisible} />
          <Button
            onClick={() => {
              setOpenSubmitPrompt(true);
            }}
            sx={{ lineHeight: '1.85rem' }}
            variant="contained"
            endIcon={<CloudUploadOutlined />}
          >
            Submit
          </Button>
        </>
      )}
    </Toolbar>
  );

  const appBar = {
    position: 'fixed',
    color: 'inherit',
    elevation: 0,
    sx: {
      borderBottom: `1px solid ${theme.palette.divider}`
    }
  };

  return (
    <>
      {!matchDownMD ? (
        <AppBarStyled open={open} {...appBar}>
          {mainHeader}
        </AppBarStyled>
      ) : (
        <AppBar {...appBar}>{mainHeader}</AppBar>
      )}
    </>
  );
};

Header.propTypes = {
  open: PropTypes.bool,
  handleDrawerToggle: PropTypes.func
};

export default Header;
