export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

export const API_ENDPOINTS = {
    services: `${API_BASE_URL}/services`,
    appointments: `${API_BASE_URL}/appointments`,
    patients: `${API_BASE_URL}/patients`,
} as const;
