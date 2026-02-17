import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { chargingProfileApi } from '../api/chargingProfileApi';
import type { ChargingProfileRequest } from '../types';

export const useChargingProfiles = (stationId: string | null, activeOnly: boolean = false) =>
  useQuery({
    queryKey: ['charging-profiles', stationId, activeOnly],
    queryFn: () => chargingProfileApi.getByStation(stationId!, activeOnly),
    enabled: !!stationId,
  });

export const useCreateChargingProfile = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: ChargingProfileRequest) => chargingProfileApi.create(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['charging-profiles'] });
    },
  });
};

export const useUpdateChargingProfile = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: Partial<ChargingProfileRequest> }) =>
      chargingProfileApi.update(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['charging-profiles'] });
    },
  });
};

export const useDeactivateChargingProfile = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (chargingProfileId: number) => chargingProfileApi.deactivate(chargingProfileId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['charging-profiles'] });
    },
  });
};
