import { useParams, useNavigate } from 'react-router-dom';
import {
  Box, Typography, Button, Card, CardContent, Chip, CircularProgress, Alert,
  Accordion, AccordionSummary, AccordionDetails, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useQuery } from '@tanstack/react-query';
import { stationApi } from '../../api/stationApi';
import type { ConnectorStatus } from '../../types';

const statusColorMap: Record<ConnectorStatus, 'success' | 'warning' | 'info' | 'error' | 'default'> = {
  AVAILABLE: 'success',
  OCCUPIED: 'warning',
  RESERVED: 'info',
  FAULTED: 'error',
  UNAVAILABLE: 'default',
};

export default function StationDetailPage() {
  const { stationId } = useParams<{ stationId: string }>();
  const navigate = useNavigate();

  const { data: station, isLoading, error } = useQuery({
    queryKey: ['stations', stationId],
    queryFn: () => stationApi.getDetail(stationId!),
    enabled: !!stationId,
    refetchInterval: 10000,
  });

  if (isLoading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error">Failed to load station details</Alert>;
  if (!station) return <Alert severity="warning">Station not found</Alert>;

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/stations')} sx={{ mb: 2 }}>
        Back to Stations
      </Button>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h4" gutterBottom fontWeight="bold">
            {station.stationId}
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Power Grid Capacity: {station.powerGridCapacity} kW
          </Typography>
          <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
            {station.v2gSupported && <Chip label="V2G Supported" color="primary" />}
            {station.iso15118Supported && <Chip label="ISO 15118" color="secondary" />}
          </Box>
        </CardContent>
      </Card>

      <Typography variant="h5" gutterBottom fontWeight="bold">
        EVSEs ({station.evses.length})
      </Typography>

      {station.evses.map((evse) => (
        <Accordion key={evse.evseId} defaultExpanded>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
              <Typography variant="h6">EVSE #{evse.evseId}</Typography>
              <Chip
                label={evse.operationalStatus}
                size="small"
                color={evse.operationalStatus === 'OPERATIVE' ? 'success' : 'error'}
              />
              <Typography variant="body2" color="text.secondary">
                Max Power: {evse.maxPower} kW
              </Typography>
            </Box>
          </AccordionSummary>
          <AccordionDetails>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Connector ID</TableCell>
                    <TableCell>Max Power (kW)</TableCell>
                    <TableCell>Min Power (kW)</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {evse.connectors.map((connector) => (
                    <TableRow key={connector.connectorId}>
                      <TableCell>#{connector.connectorId}</TableCell>
                      <TableCell>{connector.maxPower}</TableCell>
                      <TableCell>{connector.minPower}</TableCell>
                      <TableCell>
                        <Chip
                          label={connector.status}
                          size="small"
                          color={statusColorMap[connector.status as ConnectorStatus] || 'default'}
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                  {evse.connectors.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={4} align="center">No connectors</TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </AccordionDetails>
        </Accordion>
      ))}
    </Box>
  );
}
