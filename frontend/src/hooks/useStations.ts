import { useQuery } from '@tanstack/react-query';
import { stationApi } from '../api/stationApi';

export const useStations = () =>
  useQuery({
    queryKey: ['stations'],
    queryFn: stationApi.getAll,
  });

export const useStationDetail = (stationId: string) =>
  useQuery({
    queryKey: ['stations', stationId],
    queryFn: () => stationApi.getDetail(stationId),
    enabled: !!stationId,
  });
