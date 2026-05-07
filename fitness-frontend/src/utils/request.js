import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000 // 10秒超时
  // 不设全局 Content-Type，让 Axios 根据请求体自动设置
})

// Token刷新相关状态
let isRefreshing = false
let failedQueue = []

// 处理队列中失败的请求
const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

// 添加请求到失败队列
const addToFailedQueue = (config) => {
  return new Promise((resolve, reject) => {
    failedQueue.push({ resolve, reject })
  }).then(token => {
    config.headers.Authorization = `Bearer ${token}`
    return config
  }).catch(err => {
    return Promise.reject(err)
  })
}

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
      // token 过期，尝试刷新
      return handleTokenExpired(errorMessage, response.config)
    }
    if (res.code === 403) {
      // 业务层面 403，清除认证并跳转登录
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      ElMessage.warning('登录已过期，请重新登录')
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
      const err = new Error(errorMessage)
      err.response = response
      return Promise.reject(err)
    }

    // 404 由拦截器统一提示
    if (res.code === 404) {
      ElMessage.warning('资源不存在')
    }

    // 所有错误统一交给组件 catch 处理
    const err = new Error(errorMessage)
    err.response = response
    return Promise.reject(err)
  },
  (error) => {
    // 处理 HTTP 错误（网络错误、超时等）
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      const message = error.response.data?.message || `请求失败 (${status})`

      if (status === 401) {
        // token 过期，尝试刷新
        return handleTokenExpired(message, error.config)
      }
      if (status === 403) {
        // 403 通常因旧/无效 token 导致（匿名认证被 Spring Security 拒绝）
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('userInfo')
        ElMessage.warning('登录已过期，请重新登录')
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
        const err = new Error(message)
        err.response = error.response
        return Promise.reject(err)
      }

      // 404 由拦截器统一提示
      if (status === 404) {
        ElMessage.warning('资源不存在')
      }

      // 所有错误统一交给组件 catch 处理
      const err = new Error(message)
      err.response = error.response
      return Promise.reject(err)
    }

    // 请求已发送但无响应（网络错误/超时）
    if (error.request) {
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

// 处理Token过期
const handleTokenExpired = async (errorMessage, originalConfig) => {
  // 如果是刷新token接口本身失败，直接跳转登录
  if (originalConfig.url === '/auth/refresh') {
    // 清除认证状态
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')

    if (router.currentRoute.value.path !== '/login') {
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
    }
    return Promise.reject(new Error('刷新令牌失败'))
  }

  // 如果没有刷新令牌，直接跳转登录
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) {
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')

    if (router.currentRoute.value.path !== '/login') {
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
    }
    return Promise.reject(new Error('没有可用的刷新令牌'))
  }

  // 如果正在刷新，将当前请求加入队列
  if (isRefreshing) {
    return addToFailedQueue(originalConfig)
      .then(config => request(config))
      .catch(err => Promise.reject(err))
  }

  // 开始刷新
  isRefreshing = true
  const authStore = useAuthStore()

  try {
    // 调用刷新接口
    const newAccessToken = await authStore.refreshAccessToken()

    // 刷新成功，处理队列中的请求
    processQueue(null, newAccessToken)
    isRefreshing = false

    // 重试原始请求
    originalConfig.headers.Authorization = `Bearer ${newAccessToken}`
    return request(originalConfig)
  } catch (refreshError) {
    // 刷新失败，清除认证状态并跳转登录
    processQueue(refreshError, null)
    isRefreshing = false

    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')

    if (router.currentRoute.value.path !== '/login') {
      ElMessage.warning('登录已过期，请重新登录')
      router.push('/login')
    }
    return Promise.reject(refreshError)
  }
}

export default request