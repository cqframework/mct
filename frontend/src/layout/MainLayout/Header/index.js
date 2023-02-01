import { useState } from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

// material-ui
import { useTheme } from '@mui/material/styles';
import { AppBar, IconButton, Toolbar, useMediaQuery, Button } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';

// project import
import AppBarStyled from './AppBarStyled';
import HeaderContent from './HeaderContent';

// assets
import { MenuFoldOutlined, MenuUnfoldOutlined, SendOutlined } from '@ant-design/icons';

const Header = ({ open, handleDrawerToggle }) => {
  const theme = useTheme();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const matchDownMD = useMediaQuery(theme.breakpoints.down('lg'));
  const { measure } = useSelector((state) => state.filter);

  const iconBackColor = 'grey.100';
  const iconBackColorOpen = 'grey.200';

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
      {measure?.length > 0 && (
        <LoadingButton
          loading={isSubmitting}
          onClick={() => {
            setIsSubmitting(true);
            setTimeout(() => {
              setIsSubmitting(false);
            }, 2000);
          }}
          sx={{ lineHeight: '1.85rem' }}
          variant="contained"
          endIcon={<SendOutlined />}
        >
          Submit
        </LoadingButton>
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
