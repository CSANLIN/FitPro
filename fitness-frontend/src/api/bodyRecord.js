import request from '@/utils/request'

export const bodyRecordApi = {
  /**
   * 录入身体数据
   * @param {Object} data - 身体数据
   * @param {number} data.weight - 体重(kg)
   * @param {number} [data.height] - 身高(cm)
   * @param {number} [data.bodyFat] - 体脂率(%)
   * @param {number} [data.chest] - 胸围(cm)
   * @param {number} [data.waist] - 腰围(cm)
   * @param {number} [data.hip] - 臀围(cm)
   * @param {string} data.recordDate - 记录日期 (YYYY-MM-DD)
   * @param {string} [data.remark] - 备注
   * @returns {Promise<Object>} 创建的身体数据记录
   */
  create: (data) => request.post('/body-records', data),

  /**
   * 查询当前用户的身体数据记录
   * @param {Object} params - 查询参数
   * @param {string} [params.startDate] - 开始日期 (YYYY-MM-DD)
   * @param {string} [params.endDate] - 结束日期 (YYYY-MM-DD)
   * @returns {Promise<Array>} 身体数据记录列表
   */
  list: (params) => request.get('/body-records', { params }),

  /**
   * 获取用户最新的身体数据
   * @returns {Promise<Object|null>} 最新的身体数据记录
   */
  getLatest: () => request.get('/body-records/latest')
}
