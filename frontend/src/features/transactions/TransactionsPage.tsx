import { useState } from 'react';
import {
  Box, Typography, TextField, MenuItem, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Chip, CircularProgress,
  Alert, Card, CardContent, FormControl, InputLabel, Select,
} from '@mui/material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import dayjs from 'dayjs';
import { useTransactions, useTransactionMeterValues } from '../../hooks/useTransactions';
import { useStations } from '../../hooks/useStations';
import type { TransactionResponse, Measurand } from '../../types';

const measurandOptions: { value: Measurand; label: string }[] = [
  { value: 'POWER_ACTIVE_IMPORT', label: 'Power (W)' },
  { value: 'ENERGY_ACTIVE_IMPORT_REGISTER', label: 'Energy (Wh)' },
  { value: 'VOLTAGE', label: 'Voltage (V)' },
  { value: 'CURRENT_IMPORT', label: 'Current (A)' },
  { value: 'SOC', label: 'SoC (%)' },
];

const chargingStateColors: Record<string, 'success' | 'warning' | 'info' | 'error' | 'default'> = {
  CHARGING: 'success',
  SUSPENDED_EV: 'warning',
  SUSPENDED_EVSE: 'warning',
  IDLE: 'default',
  EV_CONNECTED: 'info',
};

export default function TransactionsPage() {
  const [stationFilter, setStationFilter] = useState<string>('');
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [selectedTxn, setSelectedTxn] = useState<string | null>(null);
  const [selectedMeasurand, setSelectedMeasurand] = useState<Measurand>('POWER_ACTIVE_IMPORT');

  const { data: stations } = useStations();
  const { data: transactions, isLoading, error } = useTransactions({
    stationId: stationFilter || undefined,
    startDate: startDate ? dayjs(startDate).toISOString() : undefined,
    endDate: endDate ? dayjs(endDate).toISOString() : undefined,
  });
  const { data: meterValues } = useTransactionMeterValues(selectedTxn);

  const filteredMeterValues = meterValues?.filter(mv => mv.measurand === selectedMeasurand) || [];

  const chartData = filteredMeterValues.map(mv => ({
    time: dayjs(mv.timestamp).format('HH:mm:ss'),
    value: mv.value,
  }));

  return (
    <Box>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Transaction History
      </Typography>

      {/* Filters */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
            <FormControl sx={{ minWidth: 200 }} size="small">
              <InputLabel>Station</InputLabel>
              <Select
                value={stationFilter}
                label="Station"
                onChange={(e) => setStationFilter(e.target.value)}
              >
                <MenuItem value="">All Stations</MenuItem>
                {stations?.map((s) => (
                  <MenuItem key={s.stationId} value={s.stationId}>{s.stationId}</MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField
              type="datetime-local"
              label="Start Date"
              size="small"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
            <TextField
              type="datetime-local"
              label="End Date"
              size="small"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              slotProps={{ inputLabel: { shrink: true } }}
            />
          </Box>
        </CardContent>
      </Card>

      {isLoading && <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>}
      {error && <Alert severity="error">Failed to load transactions</Alert>}

      {/* Transactions Table */}
      {transactions && (
        <TableContainer component={Paper} sx={{ mb: 3 }}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Transaction ID</TableCell>
                <TableCell>Station</TableCell>
                <TableCell>Event Type</TableCell>
                <TableCell>Charging State</TableCell>
                <TableCell>Start Time</TableCell>
                <TableCell>Total Energy (kWh)</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions.map((txn: TransactionResponse) => (
                <TableRow
                  key={txn.transactionId}
                  hover
                  selected={selectedTxn === txn.transactionId}
                  onClick={() => setSelectedTxn(txn.transactionId)}
                  sx={{ cursor: 'pointer' }}
                >
                  <TableCell>{txn.transactionId}</TableCell>
                  <TableCell>{txn.stationId}</TableCell>
                  <TableCell>
                    <Chip label={txn.eventType} size="small" variant="outlined" />
                  </TableCell>
                  <TableCell>
                    {txn.chargingState && (
                      <Chip
                        label={txn.chargingState}
                        size="small"
                        color={chargingStateColors[txn.chargingState] || 'default'}
                      />
                    )}
                  </TableCell>
                  <TableCell>{dayjs(txn.startTime).format('YYYY-MM-DD HH:mm')}</TableCell>
                  <TableCell>{txn.totalEnergy ?? '-'}</TableCell>
                </TableRow>
              ))}
              {transactions.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} align="center">No transactions found</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Meter Values Chart */}
      {selectedTxn && (
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6">
                Meter Values - {selectedTxn}
              </Typography>
              <FormControl sx={{ minWidth: 200 }} size="small">
                <InputLabel>Measurand</InputLabel>
                <Select
                  value={selectedMeasurand}
                  label="Measurand"
                  onChange={(e) => setSelectedMeasurand(e.target.value as Measurand)}
                >
                  {measurandOptions.map((opt) => (
                    <MenuItem key={opt.value} value={opt.value}>{opt.label}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Box>
            {chartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="value" stroke="#1976d2" name={selectedMeasurand} dot={false} />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <Typography variant="body2" color="text.secondary" align="center">
                No meter values available for this measurand
              </Typography>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
