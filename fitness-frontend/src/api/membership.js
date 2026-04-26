import request from '@/utils/request'

export const membershipApi = {

  // ========== 卡种管理 ==========

  /**
   * 获取卡种列表
   * @param {Object} params - { keyword }
   * @returns {Promise<Array>}
   */
  listCards: (params) => request.get('/membership-cards', { params }),

  /**
   * 创建卡种
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  createCard: (data) => request.post('/membership-cards', data),

  /**
   * 更新卡种
   * @param {number} id
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  updateCard: (id, data) => request.put(`/membership-cards/${id}`, data),

  /**
   * 切换卡种上下架
   * @param {number} id
   * @returns {Promise}
   */
  toggleCardStatus: (id) => request.put(`/membership-cards/${id}/status`),

  // ========== 会籍管理 ==========

  /**
   * 获取我的会籍列表（会员端）
   * @returns {Promise<Array>}
   */
  myMemberships: () => request.get('/memberships/my'),

  /**
   * 获取当前活跃会籍（会员端）
   * @returns {Promise<Object>}
   */
  myActiveMembership: () => request.get('/memberships/my/active'),

  /**
   * 获取全部会籍（管理端）
   * @returns {Promise<Array>}
   */
  listAllMemberships: () => request.get('/memberships'),

  /**
   * 查看指定会员的会籍
   * @param {number} userId
   * @returns {Promise<Array>}
   */
  listByUser: (userId) => request.get(`/memberships/user/${userId}`),

  /**
   * 办理会籍
   * @param {Object} data - { userId, cardId }
   * @returns {Promise<Object>}
   */
  assign: (data) => request.post('/memberships', data),

  /**
   * 续费会籍
   * @param {Object} data - { membershipId, cardId }
   * @returns {Promise<Object>}
   */
  renew: (data) => request.put('/memberships/renew', data),

  /**
   * 冻结会籍
   * @param {number} id
   * @returns {Promise}
   */
  freeze: (id) => request.put(`/memberships/${id}/freeze`),

  /**
   * 解冻会籍
   * @param {number} id
   * @returns {Promise}
   */
  unfreeze: (id) => request.put(`/memberships/${id}/unfreeze`),

  /**
   * 退卡/取消会籍
   * @param {number} id
   * @returns {Promise}
   */
  cancel: (id) => request.put(`/memberships/${id}/cancel`)
}
