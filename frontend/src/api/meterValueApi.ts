import apiClient from './client';
import type { MeterValueResponse } from '../types';

export const meterValueApi = {
  getByTransaction: (transactionId: number, measurand?: string) =>
    apiClient.get<MeterValueResponse[]>('/meter-values', { params: { transactionId, measurand } }).then(res => res.data),
};
