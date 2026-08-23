import api from './api';
import type { GenerateRoadmapRequest, GenerateRoadmapResponse, RoadmapResponse } from '../types';

export const roadmapService = {
  generateRoadmap: async (request: GenerateRoadmapRequest): Promise<GenerateRoadmapResponse> => {
    const response = await api.post<GenerateRoadmapResponse>('/ai/generate-roadmap', request);
    return response.data;
  },

  persistRoadmap: async (request: GenerateRoadmapResponse): Promise<RoadmapResponse> => {
    const response = await api.post<RoadmapResponse>('/ai/persist-roadmap', request);
    return response.data;
  },
};
