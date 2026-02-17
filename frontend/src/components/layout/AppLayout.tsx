import { Outlet } from 'react-router-dom';
import { Box } from '@mui/material';
import Sidebar, { DRAWER_WIDTH } from './Sidebar';

export default function AppLayout() {
  return (
    <Box sx={{ display: 'flex' }}>
      <Sidebar />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
          minHeight: '100vh',
          backgroundColor: 'background.default',
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
}
