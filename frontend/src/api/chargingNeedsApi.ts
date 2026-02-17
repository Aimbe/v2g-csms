import apiClient from './client';
import type { ChargingNeedsResponse } from '../types';

export const chargingNeedsApi = {
  getLatest: (stationId: string, evseId: number) =>
    apiClient.get<ChargingNeedsResponse>('/charging-needs/latest', { params: { stationId, evseId } }).then(res => res.data),
};
