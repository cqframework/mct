import { useRoutes } from 'react-router-dom';
import MainRoutes from './MainRoutes';

export default function ThemeRoutes() {
  return useRoutes([MainRoutes]);
}
