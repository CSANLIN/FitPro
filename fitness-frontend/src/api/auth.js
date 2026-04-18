import request from '@/utils/request'

export const authApi = {
  /**
   * 用户注册
   * @param {Object} data - 注册数据
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @param {string} data.nickname - 昵称
   * @param {string} data.phone - 手机号
   * @param {string} data.role - 角色（默认 MEMBER）
   * @returns {Promise<{accessToken: string, refreshToken: string, expiresIn: number}>}
   */
  register: (data) => request.post('/auth/register', data),

  /**
   * 用户登录
   * @param {Object} data - 登录数据
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise<{accessToken: string, refreshToken: string, expiresIn: number}>}
   */
  login: (data) => request.post('/auth/login', data),

  /**
   * 刷新访问令牌
   * @param {string} refreshToken - 刷新令牌
   * @returns {Promise<{accessToken: string, refreshToken: string, expiresIn: number}>}
   */
  refresh: (refreshToken) => request.post('/auth/refresh', refreshToken),

  /**
   * 用户登出
   * @param {string} refreshToken - 刷新令牌
   * @returns {Promise<void>}
   */
  logout: (refreshToken) => request.post('/auth/logout', refreshToken),

  /**
   * 获取当前用户信息
   * @returns {Promise<{id: number, username: string, nickname: string, avatar: string, role: string}>}
   */
  getMe: () => request.get('/auth/me')
}