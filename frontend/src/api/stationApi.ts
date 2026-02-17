import apiClient from './client';
import type { StationResponse } from '../types';

export const stationApi = {
  getAll: () => apiClient.get<StationResponse[]>('/stations').then(res => res.data),
  getDetail: (stationId: string) => apiClient.get<StationResponse>(`/stations/${stationId}`).then(res => res.data),
};
