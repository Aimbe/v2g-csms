import { useNavigate } from 'react-router-dom';
import {
  Box, Typography, Grid, Card, CardContent, CardActionArea,
  Chip, CircularProgress, Alert,
} from '@mui/material';
import { useStations } from '../../hooks/useStations';

export default function StationsPage() {
  const { data: stations, isLoading, error } = useStations();
  const navigate = useNavigate();

  if (isLoading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">Failed to load stations</Alert>;

  return (
    <Box>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Charging Stations
      </Typography>

      <Grid container spacing={3}>
        {stations?.map((station) => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={station.stationId}>
            <Card>
              <CardActionArea onClick={() => navigate(`/stations/${station.stationId}`)}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {station.stationId}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Power: {station.powerGridCapacity} kW
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mt: 1 }}>
                    {station.v2gSupported && <Chip label="V2G" size="small" color="primary" />}
                    {station.iso15118Supported && <Chip label="ISO15118" size="small" color="secondary" />}
                  </Box>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
        {stations?.length === 0 && (
          <Grid size={{ xs: 12 }}>
            <Alert severity="info">No charging stations found</Alert>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
