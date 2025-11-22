import Axios, { type AxiosRequestConfig, type AxiosError } from 'axios';

/**
 * Custom Axios Instance for API Requests
 *
 * This mutator provides:
 * - Base URL configuration for backend API
 * - Request interceptor for authentication headers
 * - Response interceptor for error handling
 * - Cookie-based session management
 */

export const AXIOS_INSTANCE = Axios.create({
  baseURL: import.meta.env.PUBLIC_API_URL || 'http://localhost:7171/api',
  withCredentials: true, // Send cookies for session management
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Add authentication headers if needed
AXIOS_INSTANCE.interceptors.request.use(
  (config) => {
    // Add any custom headers here (e.g., CSRF tokens)
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle common errors
AXIOS_INSTANCE.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    // Handle 401 Unauthorized - redirect to login
    if (error.response?.status === 401) {
      console.warn('Unauthorized request, redirecting to login');
      // Redirect to login page or show login modal
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }

    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      console.error('Forbidden: insufficient permissions');
    }

    // Handle 500 Internal Server Error
    if (error.response?.status === 500) {
      console.error('Server error:', error.response.data);
    }

    return Promise.reject(error);
  }
);

/**
 * Custom instance for Orval-generated client
 *
 * @param config - Axios request configuration
 * @returns Promise with response data
 */
export const customInstance = <T>(config: AxiosRequestConfig): Promise<T> => {
  const source = Axios.CancelToken.source();

  const promise = AXIOS_INSTANCE({
    ...config,
    cancelToken: source.token,
  }).then(({ data }) => data);

  // @ts-expect-error - Adding cancel method for Orval compatibility
  promise.cancel = () => {
    source.cancel('Query was cancelled');
  };

  return promise;
};

/**
 * API Error Type
 * Matches the Error schema from OpenAPI spec
 */
export interface ApiError {
  message: string;
  field?: string;
  code?: string;
}
