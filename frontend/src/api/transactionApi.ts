import apiClient from './client';
import type { TransactionResponse, MeterValueResponse } from '../types';

export const transactionApi = {
  getAll: (params?: { stationId?: string; startDate?: string; endDate?: string }) =>
    apiClient.get<TransactionResponse[]>('/transactions', { params }).then(res => res.data),
  getMeterValues: (transactionId: string) =>
    apiClient.get<MeterValueResponse[]>(`/transactions/${transactionId}/meter-values`).then(res => res.data),
};
