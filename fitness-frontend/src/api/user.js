import request from '@/utils/request'

export const userApi = {
  /**
   * 分页查询用户列表（管理端）
   * @param {Object} params - 查询参数
   * @param {string} params.keyword - 关键词（用户名/昵称/手机号）
   * @param {string} params.role - 角色：SUPER_ADMIN, COACH, MEMBER
   * @param {number} params.status - 状态：0正常，1禁用
   * @param {number} params.pageNum - 页码，默认1
   * @param {number} params.pageSize - 每页条数，默认10
   * @returns {Promise<{list: Array, total: number, pageNum: number, pageSize: number}>}
   */
  list: (params) => request.get('/users', { params }),

  /**
   * 获取用户详情
   * @param {number} id - 用户ID
   * @returns {Promise<{id: number, username: string, nickname: string, avatar: string, email: string, phone: string, gender: number, birthday: string, role: string, status: number, createdAt: string, updatedAt: string}>}
   */
  getDetail: (id) => request.get(`/users/${id}`),

  /**
   * 更新当前用户资料（用户端）
   * @param {Object} data - 更新参数
   * @param {string} data.nickname - 昵称
   * @param {string} data.avatar - 头像URL
   * @param {string} data.email - 邮箱
   * @param {string} data.phone - 手机号
   * @param {number} data.gender - 性别：0未知，1男，2女
   * @param {string} data.birthday - 生日
   * @returns {Promise<void>}
   */
  updateProfile: (data) => request.put('/users/profile', data),

  /**
   * 修改当前用户密码（用户端）
   * @param {Object} data - 密码修改参数
   * @param {string} data.oldPassword - 原密码
   * @param {string} data.newPassword - 新密码
   * @param {string} data.confirmPassword - 确认密码
   * @returns {Promise<void>}
   */
  updatePassword: (data) => request.put('/users/password', data),

  /**
   * 切换用户状态（管理端）
   * @param {number} id - 用户ID
   * @returns {Promise<void>}
   */
  toggleStatus: (id) => request.put(`/users/${id}/status`)
}