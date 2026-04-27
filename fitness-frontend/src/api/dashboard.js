import request from '@/utils/request'

export const dashboardApi = {
  getStats: () => request.get('/admin/dashboard/stats')
}
