import axios from 'axios';

const api = axios.create({
  baseURL: (process.env.REACT_APP_API_BASE_URL || '') + '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
});

api.interceptors.response.use(
  res => res.data,
  err => Promise.reject(err.response?.data?.message || err.message || 'Request failed')
);

// Analytics
export const analyticsApi = {
  getKpi: (year) => api.get(`/analytics/kpi?year=${year}`),
  getSalesByStore: (year) => api.get(`/analytics/sales/by-store?year=${year}`),
  getSalesByCategory: (year) => api.get(`/analytics/sales/by-category?year=${year}`),
  getMonthlySales: (year) => api.get(`/analytics/sales/monthly?year=${year}`),
  getTopProducts: (year, topN = 10) => api.get(`/analytics/sales/top-products?year=${year}&topN=${topN}`),
  getSalesByRegion: (year) => api.get(`/analytics/sales/by-region?year=${year}`),
  getInventoryTurnover: () => api.get('/analytics/inventory/turnover'),
  getLowStockAlerts: () => api.get('/analytics/inventory/low-stock'),
  getInventoryMovements: (year) => api.get(`/analytics/inventory/movements?year=${year}`),
  getRecentOrders: () => api.get('/analytics/recent-orders'),
};

// ETL
export const etlApi = {
  trigger: (triggeredBy = 'MANUAL_UI') => api.post('/etl/trigger', { triggeredBy }),
  getHistory: () => api.get('/etl/history'),
  getLatestStatus: () => api.get('/etl/status/latest'),
};

// Demo
export const demoApi = {
  generateOrders: (count, scenario = 'NORMAL') => api.post(`/demo/generate-orders?count=${count}&scenario=${scenario}`),
  restockInventory: () => api.post('/demo/restock-inventory'),
};
