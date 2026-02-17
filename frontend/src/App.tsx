import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider, CssBaseline } from '@mui/material';
import theme from './theme';
import AppLayout from './components/layout/AppLayout';
import DashboardPage from './features/dashboard/DashboardPage';
import StationsPage from './features/stations/StationsPage';
import StationDetailPage from './features/stations/StationDetailPage';
import V2GPage from './features/v2g/V2GPage';
import TransactionsPage from './features/transactions/TransactionsPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <Routes>
            <Route element={<AppLayout />}>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/stations" element={<StationsPage />} />
              <Route path="/stations/:stationId" element={<StationDetailPage />} />
              <Route path="/v2g" element={<V2GPage />} />
              <Route path="/transactions" element={<TransactionsPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
