import request from '@/utils/request'

export const adminApi = {
  // ====== 会员管理 ======
  getMemberDetail: (id) => request.get(`/users/${id}`),
  listCheckIns: (userId) => request.get(`/check-ins/user/${userId}`),

  // ====== 教练管理 ======
  listCoaches: (params) => request.get('/users', { params }),
  createCoach: (data) => request.post('/auth/register', data),
  updateCoach: (id, data) => request.put(`/users/profile`, data),
  toggleCoachStatus: (id) => request.put(`/users/${id}/status`),

  // ====== 课程管理 ======
  listCourses: (params) => request.get('/courses', { params }),
  createCourse: (data) => request.post('/courses', data),
  updateCourse: (id, data) => request.put('/courses', { ...data, id }),
  deleteCourse: (id) => request.delete(`/courses/${id}`),
  listAllBookings: (params) => request.get('/course-bookings', { params }),

  // ====== 公告管理 ======
  listAnnouncements: (params) => request.get('/announcements', { params }),
  createAnnouncement: (data) => request.post('/announcements', data),
  updateAnnouncement: (id, data) => request.put(`/announcements/${id}`, data),
  deleteAnnouncement: (id) => request.delete(`/announcements/${id}`),
  toggleAnnouncementTop: (id) => request.put(`/announcements/${id}/top`),
  toggleAnnouncementStatus: (id) => request.put(`/announcements/${id}/status`),

  // ====== 操作日志 ======
  listOperationLogs: (params) => request.get('/operation-logs', { params }),

  // ====== 系统配置 ======
  getSystemConfig: () => request.get('/system/config'),
  updateSystemConfig: (data) => request.put('/system/config', data)
}
