import { useQuery } from '@tanstack/react-query';
import { transactionApi } from '../api/transactionApi';

export const useTransactions = (params?: { stationId?: string; startDate?: string; endDate?: string }) =>
  useQuery({
    queryKey: ['transactions', params],
    queryFn: () => transactionApi.getAll(params),
  });

export const useTransactionMeterValues = (transactionId: string | null) =>
  useQuery({
    queryKey: ['transactions', transactionId, 'meter-values'],
    queryFn: () => transactionApi.getMeterValues(transactionId!),
    enabled: !!transactionId,
  });
