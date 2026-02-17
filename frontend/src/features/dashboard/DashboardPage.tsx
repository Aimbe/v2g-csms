import { Grid, Card, CardContent, Typography, Box, CircularProgress, Alert, Chip } from '@mui/material';
import EvStationIcon from '@mui/icons-material/EvStation';
import PowerIcon from '@mui/icons-material/Power';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import BlockIcon from '@mui/icons-material/Block';
import BoltIcon from '@mui/icons-material/Bolt';
import TodayIcon from '@mui/icons-material/Today';
import { useDashboard } from '../../hooks/useDashboard';
import { useStations } from '../../hooks/useStations';

interface SummaryCardProps {
  title: string;
  value: number | undefined;
  icon: React.ReactNode;
  color: string;
}

function SummaryCard({ title, value, icon, color }: SummaryCardProps) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography color="text.secondary" variant="body2" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h4" fontWeight="bold">
              {value ?? '-'}
            </Typography>
          </Box>
          <Box sx={{ color, fontSize: 48 }}>
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}

export default function DashboardPage() {
  const { data: summary, isLoading, error } = useDashboard();
  const { data: stations } = useStations();

  if (isLoading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">Failed to load dashboard data</Alert>;

  return (
    <Box>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Dashboard
      </Typography>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <SummaryCard
            title="Total Stations"
            value={summary?.totalStations}
            icon={<EvStationIcon fontSize="inherit" />}
            color="#1976d2"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <SummaryCard
            title="Total EVSEs"
            value={summary?.totalEvses}
            icon={<PowerIcon fontSize="inherit" />}
            color="#9c27b0"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <SummaryCard
            title="Available Connectors"
            value={summary?.availableConnectors}
            icon={<CheckCircleIcon fontSize="inherit" />}
            color="#2e7d32"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <SummaryCard
            title="Occupied Connectors"
            value={summary?.occupiedConnectors}
            icon={<BlockIcon fontSize="inherit" />}
            color="#ed6c02"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <SummaryCard
            title="Active Transactions"
            value={summary?.activeTransactions}
            icon={<BoltIcon fontSize="inherit" />}
            color="#d32f2f"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <SummaryCard
            title="Today's Transactions"
            value={summary?.todayTransactions}
            icon={<TodayIcon fontSize="inherit" />}
            color="#0288d1"
          />
        </Grid>
      </Grid>

      {stations && stations.length > 0 && (
        <Box>
          <Typography variant="h5" gutterBottom fontWeight="bold">
            Station Overview
          </Typography>
          <Grid container spacing={2}>
            {stations.map((station) => (
              <Grid size={{ xs: 12, sm: 6, md: 4 }} key={station.stationId}>
                <Card>
                  <CardContent>
                    <Typography variant="h6">{station.stationId}</Typography>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      {station.powerGridCapacity} kW
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mt: 1 }}>
                      {station.v2gSupported && <Chip label="V2G" size="small" color="primary" variant="outlined" />}
                      {station.iso15118Supported && <Chip label="ISO15118" size="small" color="secondary" variant="outlined" />}
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}
    </Box>
  );
}
