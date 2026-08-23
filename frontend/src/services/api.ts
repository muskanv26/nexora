import axios from 'axios';
import type { ApiResponse } from '../types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor to automatically unpack the standard ApiResponse envelope
api.interceptors.response.use(
  (response) => {
    const apiResponse = response.data as ApiResponse<any>;
    if (apiResponse && apiResponse.success) {
      return {
        ...response,
        data: apiResponse.data,
      };
    } else {
      const errorMessage = apiResponse?.error?.message || 'Request failed';
      return Promise.reject(new Error(errorMessage));
    }
  },
  (error) => {
    // Handle HTTP errors or backend custom validation errors wrapped in ApiResponse
    if (error.response?.data) {
      const apiResponse = error.response.data as ApiResponse<any>;
      if (apiResponse && apiResponse.error) {
        return Promise.reject(new Error(apiResponse.error.message));
      }
    }
    return Promise.reject(new Error(error.message || 'Network error occurred'));
  }
);

export default api;
