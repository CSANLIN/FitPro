import request from '@/utils/request'

export const workoutApi = {

  // ========== 训练模板 ==========

  /**
   * 获取全部训练模板
   * @returns {Promise<Array>} 模板列表
   */
  listTemplates: () => request.get('/workout-templates'),

  /**
   * 获取模板详情
   * @param {number} id
   * @returns {Promise<Object>} 模板详情（含动作列表）
   */
  getTemplateDetail: (id) => request.get(`/workout-templates/${id}`),

  /**
   * 创建训练模板（教练/管理端）
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  createTemplate: (data) => request.post('/workout-templates', data),

  /**
   * 更新训练模板（教练/管理端）
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  updateTemplate: (data) => request.put('/workout-templates', data),

  /**
   * 删除训练模板（教练/管理端）
   * @param {number} id
   * @returns {Promise}
   */
  deleteTemplate: (id) => request.delete(`/workout-templates/${id}`),

  // ========== 训练计划 ==========

  /**
   * 分页查询训练计划
   * @param {Object} params - 查询参数
   * @returns {Promise<Object>} 分页结果
   */
  listPlans: (params) => request.get('/workout-plans', { params }),

  /**
   * 获取计划详情
   * @param {number} id
   * @returns {Promise<Object>} 计划详情（含训练日和动作）
   */
  getPlanDetail: (id) => request.get(`/workout-plans/${id}`),

  /**
   * 创建训练计划
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  createPlan: (data) => request.post('/workout-plans', data),

  /**
   * 更新计划状态
   * @param {number} id
   * @param {string} status - ACTIVE/COMPLETED/CANCELLED
   * @returns {Promise}
   */
  updatePlanStatus: (id, status) => request.put(`/workout-plans/${id}/status`, null, { params: { status } }),

  /**
   * 删除训练计划
   * @param {number} id
   * @returns {Promise}
   */
  deletePlan: (id) => request.delete(`/workout-plans/${id}`),

  // ========== 训练记录 ==========

  /**
   * 分页查询当前用户训练记录
   * @param {Object} params
   * @returns {Promise<Object>}
   */
  listRecords: (params) => request.get('/workout-records', { params }),

  /**
   * 获取训练记录详情
   * @param {number} id
   * @returns {Promise<Object>} 记录详情（含训练组）
   */
  getRecordDetail: (id) => request.get(`/workout-records/${id}`),

  /**
   * 创建训练记录
   * @param {Object} data
   * @returns {Promise<Object>}
   */
  createRecord: (data) => request.post('/workout-records', data),

  /**
   * 删除训练记录
   * @param {number} id
   * @returns {Promise}
   */
  deleteRecord: (id) => request.delete(`/workout-records/${id}`),

  /**
   * 本周训练统计
   * @returns {Promise<Object>} { weeklyCount, weeklyVolume }
   */
  weeklyStats: () => request.get('/workout-records/stats/weekly')
}
