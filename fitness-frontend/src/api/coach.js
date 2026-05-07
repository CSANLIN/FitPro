import request from '@/utils/request'

export const coachApi = {
  // 课程编排
  listSchedules: (params) => request.get('/coach/schedules', { params }),
  createSchedule: (data) => request.post('/coach/schedules', data),
  cancelSchedule: (id) => request.put(`/coach/schedules/${id}/cancel`),
  listBookings: (scheduleId) => request.get(`/coach/schedules/${scheduleId}/bookings`),

  // 授课分析
  getSummary: () => request.get('/coach/analysis/summary'),
  getAttendanceTrend: (period) => request.get('/coach/analysis/attendance-trend', { params: { period } }),
  getCourseRank: () => request.get('/coach/analysis/course-rank'),
  getTimeDistribution: () => request.get('/coach/analysis/time-distribution'),

  // 学员分析
  listStudents: () => request.get('/coach/students'),
  getStudentStats: (userId) => request.get(`/coach/students/${userId}/stats`)
}
