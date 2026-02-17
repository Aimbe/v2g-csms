// Enums mirroring Java enums
export type ConnectorStatus = 'AVAILABLE' | 'OCCUPIED' | 'RESERVED' | 'UNAVAILABLE' | 'FAULTED';
export type OperationalStatus = 'OPERATIVE' | 'INOPERATIVE';
export type TransactionEvent = 'STARTED' | 'UPDATED' | 'ENDED';
export type ChargingState = 'CHARGING' | 'SUSPENDED_EV' | 'SUSPENDED_EVSE' | 'IDLE' | 'EV_CONNECTED';
export type Measurand = 'POWER_ACTIVE_IMPORT' | 'ENERGY_ACTIVE_IMPORT_REGISTER' | 'SOC' | 'VOLTAGE' | 'CURRENT_IMPORT' | 'POWER_ACTIVE_EXPORT' | 'ENERGY_ACTIVE_EXPORT_REGISTER' | 'CURRENT_EXPORT';
export type ChargingProfilePurpose = 'CHARGE_POINT_MAX_PROFILE' | 'TX_DEFAULT_PROFILE' | 'TX_PROFILE' | 'TX_DISCHARGE_PROFILE';
export type ChargingProfileKind = 'ABSOLUTE' | 'RECURRING' | 'RELATIVE';
export type EnergyTransferMode = 'DC' | 'AC_SINGLE_PHASE' | 'AC_TWO_PHASE' | 'AC_THREE_PHASE' | 'AC_BPT_SINGLE_PHASE' | 'AC_BPT_THREE_PHASE' | 'DC_BPT';

// Response interfaces mirroring Java DTOs
export interface ConnectorResponse {
  id: number;
  connectorId: number;
  evseId: number;
  stationId: string;
  maxPower: number;
  minPower: number;
  status: ConnectorStatus;
}

export interface EvseResponse {
  id: number;
  evseId: number;
  stationId: string;
  maxPower: number;
  operationalStatus: OperationalStatus;
  connectors: ConnectorResponse[];
}

export interface StationResponse {
  id: number;
  stationId: string;
  powerGridCapacity: number;
  v2gSupported: boolean;
  iso15118Supported: boolean;
  evses: EvseResponse[];
}

export interface TransactionResponse {
  id: number;
  transactionId: string;
  evseId: number;
  stationId: string;
  connectorId: number;
  idToken: string;
  eventType: TransactionEvent;
  chargingState: ChargingState;
  startTime: string;
  stopTime: string | null;
  totalEnergy: number | null;
  stopReason: string | null;
}

export interface MeterValueResponse {
  id: number;
  timestamp: string;
  measurand: Measurand;
  value: number;
  unit: string;
  phase: string | null;
  location: string | null;
}

export interface ChargingProfileResponse {
  id: number;
  chargingProfileId: number;
  stationId: string;
  evseId: number | null;
  transactionId: string | null;
  stackLevel: number;
  chargingProfilePurpose: ChargingProfilePurpose;
  chargingProfileKind: ChargingProfileKind;
  validFrom: string | null;
  validTo: string | null;
  duration: number | null;
  startSchedule: string | null;
  chargingRateUnit: string;
  minChargingRate: number | null;
  isActive: boolean;
  maxDischargePower: number | null;
  minDischargePower: number | null;
  dischargeRateUnit: string | null;
}

export interface ChargingProfileRequest {
  stationId: string;
  evseId?: number;
  stackLevel: number;
  chargingProfilePurpose: ChargingProfilePurpose;
  chargingProfileKind: ChargingProfileKind;
  chargingRateUnit: string;
  validFrom?: string;
  validTo?: string;
  minChargingRate?: number;
  maxDischargePower?: number;
  minDischargePower?: number;
  dischargeRateUnit?: string;
}

export interface ChargingNeedsResponse {
  id: number;
  stationId: string;
  evseId: number;
  requestedEnergyTransfer: EnergyTransferMode;
  departureTime: string | null;
  acMaxCurrent: number | null;
  acMaxVoltage: number | null;
  acMinCurrent: number | null;
  dcMaxCurrent: number | null;
  dcMaxVoltage: number | null;
  dcMaxPower: number | null;
  dcMinCurrent: number | null;
  dcMinVoltage: number | null;
  dcMinPower: number | null;
  dcTargetSoc: number | null;
  dcBulkSoc: number | null;
  dcFullSoc: number | null;
  dcEnergyCapacity: number | null;
}

export interface DashboardSummaryResponse {
  totalStations: number;
  totalEvses: number;
  availableConnectors: number;
  occupiedConnectors: number;
  activeTransactions: number;
  todayTransactions: number;
}
