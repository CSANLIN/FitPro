import request from '@/utils/request'

export const courseApi = {

  // ========== 课程 ==========

  /**
   * 获取课程列表
   * @param {Object} params - 查询参数
   * @param {boolean} params.all - 是否获取全部（含下架），管理端使用
   * @param {string} params.courseType - 课程类型筛选
   * @param {string} params.keyword - 搜索关键词
   * @returns {Promise<Array>} 课程列表
   */
  list: (params) => request.get('/courses', { params }),

  /**
   * 获取课程详情
   * @param {number} id
   * @returns {Promise<Object>}
   */
  getById: (id) => request.get(`/courses/${id}`),

  /**
   * 创建课程
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  create: (data) => request.post('/courses', data),

  /**
   * 更新课程
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  update: (data) => request.put('/courses', data),

  /**
   * 切换上下架状态
   * @param {number} id
   * @returns {Promise}
   */
  toggleStatus: (id) => request.put(`/courses/${id}/status`),

  /**
   * 删除课程
   * @param {number} id
   * @returns {Promise}
   */
  delete: (id) => request.delete(`/courses/${id}`),

  // ========== 排课 ==========

  /**
   * 按日期范围查询排课
   * @param {Object} params - courseId, coachId, startDate, endDate
   * @returns {Promise<Array>} 排课列表（含课程名、教练名、预约状态）
   */
  listSchedules: (params) => request.get('/course-schedules', { params }),

  /**
   * 获取排课详情
   * @param {number} id
   * @returns {Promise<Object>}
   */
  getScheduleDetail: (id) => request.get(`/course-schedules/${id}`),

  /**
   * 创建排课
   * @param {Object} data - courseId, coachId, scheduleDate, startTime, endTime, location
   * @returns {Promise<Object>}
   */
  createSchedule: (data) => request.post('/course-schedules', data),

  /**
   * 取消排课
   * @param {number} id
   * @returns {Promise}
   */
  cancelSchedule: (id) => request.put(`/course-schedules/${id}/cancel`),

  // ========== 预约 ==========

  /**
   * 预约课程
   * @param {number} scheduleId
   * @returns {Promise}
   */
  book: (scheduleId) => request.post('/course-bookings', { scheduleId }),

  /**
   * 取消预约
   * @param {number} id - 预约记录ID
   * @returns {Promise}
   */
  cancelBooking: (id) => request.put(`/course-bookings/${id}/cancel`),

  /**
   * 获取我的预约列表
   * @returns {Promise<Array>}
   */
  myBookings: () => request.get('/course-bookings/my'),

  /**
   * 查看排课的预约列表（管理端）
   * @param {number} scheduleId
   * @returns {Promise<Array>}
   */
  listBookingsBySchedule: (scheduleId) => request.get(`/course-bookings/schedule/${scheduleId}`),

  /**
   * 查看所有预约记录（管理端）
   * @returns {Promise<Array>}
   */
  listAllBookings: () => request.get('/course-bookings')
}
