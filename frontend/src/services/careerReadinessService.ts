import api from './api';
import type { CareerReadinessRequest, CareerReadinessResponse } from '../types';

export const careerReadinessService = {
  evaluateCareerReadiness: async (request: CareerReadinessRequest): Promise<CareerReadinessResponse> => {
    const response = await api.post<CareerReadinessResponse>('/ai/career-readiness', request);
    return response.data;
  },
};
