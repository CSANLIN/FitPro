import request from '@/utils/request'

export const exerciseApi = {
  /**
   * 获取全部分类列表
   * @returns {Promise<Array>} 分类列表
   */
  listCategories: () => request.get('/exercise-categories'),

  /**
   * 创建运动分类（管理端）
   * @param {Object} data - 分类数据
   * @param {string} data.name - 分类名称
   * @param {string} [data.icon] - 图标
   * @param {number} [data.sortOrder] - 排序
   * @returns {Promise<Object>} 创建的分类
   */
  createCategory: (data) => request.post('/exercise-categories', data),

  /**
   * 更新运动分类（管理端）
   * @param {Object} data - 分类数据
   * @param {number} data.id - 分类ID
   * @param {string} data.name - 分类名称
   * @param {string} [data.icon] - 图标
   * @param {number} [data.sortOrder] - 排序
   * @returns {Promise<Object>} 更新后的分类
   */
  updateCategory: (data) => request.put('/exercise-categories', data),

  /**
   * 删除运动分类（管理端）
   * @param {number} id - 分类ID
   * @returns {Promise}
   */
  deleteCategory: (id) => request.delete(`/exercise-categories/${id}`),

  /**
   * 分页查询运动动作
   * @param {Object} params - 查询参数
   * @param {number} [params.categoryId] - 分类ID
   * @param {string} [params.muscleGroup] - 目标肌群
   * @param {string} [params.equipment] - 所需器械
   * @param {string} [params.difficulty] - 难度
   * @param {string} [params.keyword] - 关键词
   * @param {number} [params.pageNum] - 页码
   * @param {number} [params.pageSize] - 每页条数
   * @returns {Promise<Object>} 分页结果
   */
  list: (params) => request.get('/exercises', { params }),

  /**
   * 获取动作详情
   * @param {number} id - 动作ID
   * @returns {Promise<Object>} 动作详情
   */
  getById: (id) => request.get(`/exercises/${id}`),

  /**
   * 创建运动动作（管理端）
   * @param {Object} data - 动作数据
   * @returns {Promise<Object>} 创建的动作
   */
  create: (data) => request.post('/exercises', data),

  /**
   * 更新运动动作（管理端）
   * @param {Object} data - 动作数据
   * @returns {Promise<Object>} 更新后的动作
   */
  update: (data) => request.put('/exercises', data),

  /**
   * 删除运动动作（管理端）
   * @param {number} id - 动作ID
   * @returns {Promise}
   */
  delete: (id) => request.delete(`/exercises/${id}`)
}
