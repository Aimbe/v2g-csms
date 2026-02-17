import { useState } from 'react';
import {
  Box, Typography, Card, CardContent, FormControl, InputLabel, Select, MenuItem,
  Tabs, Tab, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Chip, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, CircularProgress, Alert, LinearProgress,
} from '@mui/material';
import { useStations, useStationDetail } from '../../hooks/useStations';
import { useChargingProfiles, useCreateChargingProfile, useDeactivateChargingProfile } from '../../hooks/useChargingProfiles';
import { useChargingNeeds } from '../../hooks/useChargingNeeds';
import type { ChargingProfileRequest, ChargingProfilePurpose, ChargingProfileKind } from '../../types';

function TabPanel({ children, value, index }: { children: React.ReactNode; value: number; index: number }) {
  return value === index ? <Box sx={{ pt: 2 }}>{children}</Box> : null;
}

export default function V2GPage() {
  const [selectedStation, setSelectedStation] = useState<string>('');
  const [tabValue, setTabValue] = useState(0);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedEvseId, setSelectedEvseId] = useState<number | null>(null);

  const { data: stations } = useStations();
  const { data: stationDetail } = useStationDetail(selectedStation);
  const { data: profiles, isLoading: profilesLoading } = useChargingProfiles(selectedStation || null);
  const { data: chargingNeeds, isLoading: needsLoading } = useChargingNeeds(
    selectedStation || null,
    selectedEvseId
  );
  const createProfile = useCreateChargingProfile();
  const deactivateProfile = useDeactivateChargingProfile();

  // Form state
  const [formData, setFormData] = useState<Partial<ChargingProfileRequest>>({
    stackLevel: 0,
    chargingProfilePurpose: 'TX_DEFAULT_PROFILE',
    chargingProfileKind: 'ABSOLUTE',
    chargingRateUnit: 'W',
  });

  const handleCreateProfile = () => {
    if (!selectedStation) return;
    createProfile.mutate({
      ...formData,
      stationId: selectedStation,
    } as ChargingProfileRequest, {
      onSuccess: () => setDialogOpen(false),
    });
  };

  const handleDeactivate = (profileId: number) => {
    deactivateProfile.mutate(profileId);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        V2G Control
      </Typography>

      {/* Station Selector */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <FormControl sx={{ minWidth: 300 }} size="small">
            <InputLabel>Select Station</InputLabel>
            <Select
              value={selectedStation}
              label="Select Station"
              onChange={(e) => {
                setSelectedStation(e.target.value);
                setSelectedEvseId(null);
              }}
            >
              {stations?.map((s) => (
                <MenuItem key={s.stationId} value={s.stationId}>
                  {s.stationId} {s.v2gSupported ? '(V2G)' : ''}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </CardContent>
      </Card>

      {selectedStation && (
        <>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)} sx={{ mb: 2 }}>
            <Tab label="Charging Profiles" />
            <Tab label="Charging Needs" />
          </Tabs>

          {/* Charging Profiles Tab */}
          <TabPanel value={tabValue} index={0}>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
              <Button variant="contained" onClick={() => setDialogOpen(true)}>
                Create Profile
              </Button>
            </Box>

            {profilesLoading && <CircularProgress />}

            <TableContainer component={Paper}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Profile ID</TableCell>
                    <TableCell>Purpose</TableCell>
                    <TableCell>Kind</TableCell>
                    <TableCell>Stack Level</TableCell>
                    <TableCell>Rate Unit</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Max Discharge</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {profiles?.map((profile) => (
                    <TableRow key={profile.chargingProfileId}>
                      <TableCell>{profile.chargingProfileId}</TableCell>
                      <TableCell>{profile.chargingProfilePurpose}</TableCell>
                      <TableCell>{profile.chargingProfileKind}</TableCell>
                      <TableCell>{profile.stackLevel}</TableCell>
                      <TableCell>{profile.chargingRateUnit}</TableCell>
                      <TableCell>
                        <Chip
                          label={profile.isActive ? 'Active' : 'Inactive'}
                          size="small"
                          color={profile.isActive ? 'success' : 'default'}
                        />
                      </TableCell>
                      <TableCell>
                        {profile.maxDischargePower ? `${profile.maxDischargePower} W` : '-'}
                      </TableCell>
                      <TableCell>
                        {profile.isActive && (
                          <Button
                            size="small"
                            color="error"
                            onClick={() => handleDeactivate(profile.chargingProfileId)}
                          >
                            Deactivate
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                  {(!profiles || profiles.length === 0) && (
                    <TableRow>
                      <TableCell colSpan={8} align="center">No charging profiles found</TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>

            {/* Create Profile Dialog */}
            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
              <DialogTitle>Create Charging Profile</DialogTitle>
              <DialogContent>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                  <TextField
                    label="Stack Level"
                    type="number"
                    value={formData.stackLevel ?? 0}
                    onChange={(e) => setFormData({ ...formData, stackLevel: parseInt(e.target.value) })}
                    size="small"
                  />
                  <FormControl size="small">
                    <InputLabel>Purpose</InputLabel>
                    <Select
                      value={formData.chargingProfilePurpose || 'TX_DEFAULT_PROFILE'}
                      label="Purpose"
                      onChange={(e) => setFormData({ ...formData, chargingProfilePurpose: e.target.value as ChargingProfilePurpose })}
                    >
                      <MenuItem value="CHARGE_POINT_MAX_PROFILE">Charge Point Max</MenuItem>
                      <MenuItem value="TX_DEFAULT_PROFILE">TX Default</MenuItem>
                      <MenuItem value="TX_PROFILE">TX Profile</MenuItem>
                      <MenuItem value="TX_DISCHARGE_PROFILE">TX Discharge (V2G)</MenuItem>
                    </Select>
                  </FormControl>
                  <FormControl size="small">
                    <InputLabel>Kind</InputLabel>
                    <Select
                      value={formData.chargingProfileKind || 'ABSOLUTE'}
                      label="Kind"
                      onChange={(e) => setFormData({ ...formData, chargingProfileKind: e.target.value as ChargingProfileKind })}
                    >
                      <MenuItem value="ABSOLUTE">Absolute</MenuItem>
                      <MenuItem value="RECURRING">Recurring</MenuItem>
                      <MenuItem value="RELATIVE">Relative</MenuItem>
                    </Select>
                  </FormControl>
                  <FormControl size="small">
                    <InputLabel>Rate Unit</InputLabel>
                    <Select
                      value={formData.chargingRateUnit || 'W'}
                      label="Rate Unit"
                      onChange={(e) => setFormData({ ...formData, chargingRateUnit: e.target.value })}
                    >
                      <MenuItem value="W">Watts (W)</MenuItem>
                      <MenuItem value="A">Amperes (A)</MenuItem>
                    </Select>
                  </FormControl>
                  <TextField
                    label="Max Discharge Power (W)"
                    type="number"
                    value={formData.maxDischargePower ?? ''}
                    onChange={(e) => setFormData({ ...formData, maxDischargePower: e.target.value ? parseFloat(e.target.value) : undefined })}
                    size="small"
                  />
                  <TextField
                    label="Min Discharge Power (W)"
                    type="number"
                    value={formData.minDischargePower ?? ''}
                    onChange={(e) => setFormData({ ...formData, minDischargePower: e.target.value ? parseFloat(e.target.value) : undefined })}
                    size="small"
                  />
                </Box>
              </DialogContent>
              <DialogActions>
                <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
                <Button variant="contained" onClick={handleCreateProfile}>Create</Button>
              </DialogActions>
            </Dialog>
          </TabPanel>

          {/* Charging Needs Tab */}
          <TabPanel value={tabValue} index={1}>
            <Card sx={{ mb: 2 }}>
              <CardContent>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                  <FormControl sx={{ minWidth: 200 }} size="small">
                    <InputLabel>EVSE</InputLabel>
                    <Select
                      value={selectedEvseId ?? ''}
                      label="EVSE"
                      onChange={(e) => setSelectedEvseId(e.target.value as number)}
                    >
                      {stationDetail?.evses.map((evse) => (
                        <MenuItem key={evse.evseId} value={evse.evseId}>
                          EVSE #{evse.evseId}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Box>
              </CardContent>
            </Card>

            {needsLoading && <CircularProgress />}

            {chargingNeeds && (
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    EV Charging Requirements
                  </Typography>

                  <Box sx={{ display: 'flex', gap: 4, flexWrap: 'wrap' }}>
                    {/* Energy Transfer Mode */}
                    <Box>
                      <Typography variant="body2" color="text.secondary">Energy Transfer Mode</Typography>
                      <Chip label={chargingNeeds.requestedEnergyTransfer} color="primary" />
                    </Box>

                    {/* Departure Time */}
                    {chargingNeeds.departureTime && (
                      <Box>
                        <Typography variant="body2" color="text.secondary">Departure Time</Typography>
                        <Typography variant="body1">{chargingNeeds.departureTime}</Typography>
                      </Box>
                    )}
                  </Box>

                  {/* SoC Gauge */}
                  {chargingNeeds.dcTargetSoc != null && (
                    <Box sx={{ mt: 3 }}>
                      <Typography variant="subtitle1" gutterBottom>Battery SoC</Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <Box sx={{ flexGrow: 1 }}>
                          <LinearProgress
                            variant="determinate"
                            value={chargingNeeds.dcTargetSoc}
                            sx={{ height: 20, borderRadius: 2 }}
                          />
                        </Box>
                        <Typography variant="body1" fontWeight="bold">
                          Target: {chargingNeeds.dcTargetSoc}%
                        </Typography>
                      </Box>
                      {chargingNeeds.dcEnergyCapacity && (
                        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                          Battery Capacity: {chargingNeeds.dcEnergyCapacity} Wh
                        </Typography>
                      )}
                    </Box>
                  )}

                  {/* DC Parameters */}
                  {(chargingNeeds.dcMaxPower || chargingNeeds.dcMaxCurrent) && (
                    <Box sx={{ mt: 3 }}>
                      <Typography variant="subtitle1" gutterBottom>DC Parameters</Typography>
                      <TableContainer component={Paper} variant="outlined">
                        <Table size="small">
                          <TableBody>
                            {chargingNeeds.dcMaxPower != null && (
                              <TableRow>
                                <TableCell>Max Power</TableCell>
                                <TableCell>{chargingNeeds.dcMaxPower} W</TableCell>
                              </TableRow>
                            )}
                            {chargingNeeds.dcMaxCurrent != null && (
                              <TableRow>
                                <TableCell>Max Current</TableCell>
                                <TableCell>{chargingNeeds.dcMaxCurrent} A</TableCell>
                              </TableRow>
                            )}
                            {chargingNeeds.dcMaxVoltage != null && (
                              <TableRow>
                                <TableCell>Max Voltage</TableCell>
                                <TableCell>{chargingNeeds.dcMaxVoltage} V</TableCell>
                              </TableRow>
                            )}
                            {chargingNeeds.dcMinPower != null && (
                              <TableRow>
                                <TableCell>Min Power</TableCell>
                                <TableCell>{chargingNeeds.dcMinPower} W</TableCell>
                              </TableRow>
                            )}
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </Box>
                  )}

                  {/* AC Parameters */}
                  {(chargingNeeds.acMaxCurrent || chargingNeeds.acMaxVoltage) && (
                    <Box sx={{ mt: 3 }}>
                      <Typography variant="subtitle1" gutterBottom>AC Parameters</Typography>
                      <TableContainer component={Paper} variant="outlined">
                        <Table size="small">
                          <TableBody>
                            {chargingNeeds.acMaxCurrent != null && (
                              <TableRow>
                                <TableCell>Max Current</TableCell>
                                <TableCell>{chargingNeeds.acMaxCurrent} A</TableCell>
                              </TableRow>
                            )}
                            {chargingNeeds.acMaxVoltage != null && (
                              <TableRow>
                                <TableCell>Max Voltage</TableCell>
                                <TableCell>{chargingNeeds.acMaxVoltage} V</TableCell>
                              </TableRow>
                            )}
                            {chargingNeeds.acMinCurrent != null && (
                              <TableRow>
                                <TableCell>Min Current</TableCell>
                                <TableCell>{chargingNeeds.acMinCurrent} A</TableCell>
                              </TableRow>
                            )}
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </Box>
                  )}
                </CardContent>
              </Card>
            )}

            {!chargingNeeds && selectedEvseId && !needsLoading && (
              <Alert severity="info">No charging needs data available for this EVSE</Alert>
            )}

            {!selectedEvseId && (
              <Alert severity="info">Select an EVSE to view charging needs</Alert>
            )}
          </TabPanel>
        </>
      )}

      {!selectedStation && (
        <Alert severity="info">Select a charging station to manage V2G settings</Alert>
      )}
    </Box>
  );
}
