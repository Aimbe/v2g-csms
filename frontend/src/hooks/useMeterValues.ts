import { useQuery } from '@tanstack/react-query';
import { meterValueApi } from '../api/meterValueApi';

export const useMeterValues = (transactionId: number | null, measurand?: string) =>
  useQuery({
    queryKey: ['meter-values', transactionId, measurand],
    queryFn: () => meterValueApi.getByTransaction(transactionId!, measurand),
    enabled: !!transactionId,
  });
