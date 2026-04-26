import request from '@/utils/request'

export const checkinApi = {

  /**
   * 签到
   * @returns {Promise}
   */
  checkIn: () => request.post('/check-ins'),

  /**
   * 获取我的签到记录
   * @returns {Promise<Array>}
   */
  myRecords: () => request.get('/check-ins/my'),

  /**
   * 获取签到统计
   * @returns {Promise<Object>} { monthCount, streakDays, checkInDates }
   */
  myStats: () => request.get('/check-ins/my/stats'),

  /**
   * 查看所有签到记录（管理端）
   * @returns {Promise<Array>}
   */
  listAll: () => request.get('/check-ins'),

  /**
   * 查看指定会员的签到记录
   * @param {number} userId
   * @returns {Promise<Array>}
   */
  listByUser: (userId) => request.get(`/check-ins/user/${userId}`)
}
