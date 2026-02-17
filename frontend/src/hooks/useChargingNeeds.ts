import { useQuery } from '@tanstack/react-query';
import { chargingNeedsApi } from '../api/chargingNeedsApi';

export const useChargingNeeds = (stationId: string | null, evseId: number | null) =>
  useQuery({
    queryKey: ['charging-needs', stationId, evseId],
    queryFn: () => chargingNeedsApi.getLatest(stationId!, evseId!),
    enabled: !!stationId && !!evseId,
  });
