import apiClient from './client';
import type { DashboardSummaryResponse } from '../types';

export const dashboardApi = {
  getSummary: () => apiClient.get<DashboardSummaryResponse>('/dashboard/summary').then(res => res.data),
};
