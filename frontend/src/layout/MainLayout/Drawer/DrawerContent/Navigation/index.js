import { Box } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';

import NavGroup from './NavGroup';
import menuItem from 'menu-items';

const Navigation = () => {
  const { measureReport } = useSelector((state) => state.data);

  const navGroups = menuItem.items.map((item) => {
    switch (item.type) {
      case 'group':
        return <NavGroup key={item.id} item={item} />;
    }
  });

  return <Box sx={{ pt: 2 }}>{navGroups}</Box>;
};

export default Navigation;
