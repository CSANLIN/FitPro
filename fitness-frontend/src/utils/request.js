import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000, // 10秒超时
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 处理成功响应
    const res = response.data

    // 统一响应格式检查
    if (res.code === undefined || res.message === undefined) {
      console.warn('非标准响应格式:', response.config.url, res)
      return res
    }

    // 业务成功
    if (res.code === 200) {
      return res.data
    }

    // 业务错误（非200状态码）
    const errorMessage = res.message || '请求失败'

    // 特殊处理某些错误码
    if (res.code === 401) {
      // token 过期，清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')

      // 跳转到登录页
      if (router.currentRoute.value.path !== '/login') {
        ElMessage.warning('登录已过期，请重新登录')
        router.push('/login')
      }
    } else if (res.code === 403) {
      ElMessage.warning('无权限访问')
    } else if (res.code === 404) {
      ElMessage.warning('资源不存在')
    } else if (res.code >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else {
      // 其他业务错误
      ElMessage.error(errorMessage)
    }

    return Promise.reject(new Error(errorMessage))
  },
  (error) => {
    // 处理 HTTP 错误（网络错误、超时等）
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      const message = error.response.data?.message || `请求失败 (${status})`

      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('userInfo')

        if (router.currentRoute.value.path !== '/login') {
          ElMessage.warning('登录已过期，请重新登录')
          router.push('/login')
        }
      } else if (status === 403) {
        ElMessage.warning('无权限访问')
      } else if (status === 404) {
        ElMessage.warning('资源不存在')
      } else if (status >= 500) {
        ElMessage.error('服务器错误，请稍后重试')
      } else {
        ElMessage.error(message)
      }
    } else if (error.request) {
      // 请求已发送但无响应（网络错误）
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('请求超时，请检查网络连接')
      } else {
        ElMessage.error('网络错误，请检查网络连接')
      }
    } else {
      // 请求配置错误
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export default request