import apiClient from './client';
import type { ChargingProfileResponse, ChargingProfileRequest } from '../types';

export const chargingProfileApi = {
  getByStation: (stationId: string, activeOnly: boolean = false) =>
    apiClient.get<ChargingProfileResponse[]>('/charging-profiles', { params: { stationId, activeOnly } }).then(res => res.data),
  create: (request: ChargingProfileRequest) =>
    apiClient.post<ChargingProfileResponse>('/charging-profiles', request).then(res => res.data),
  update: (chargingProfileId: number, request: Partial<ChargingProfileRequest>) =>
    apiClient.put<ChargingProfileResponse>(`/charging-profiles/${chargingProfileId}`, request).then(res => res.data),
  deactivate: (chargingProfileId: number) =>
    apiClient.delete(`/charging-profiles/${chargingProfileId}`),
};
