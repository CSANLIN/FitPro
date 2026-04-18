import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 方法
  const setToken = (newToken, newRefreshToken) => {
    token.value = newToken
    refreshToken.value = newRefreshToken
    localStorage.setItem('token', newToken)
    localStorage.setItem('refreshToken', newRefreshToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const clearAuth = () => {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }

  // 登录方法
  const login = async (credentials) => {
    try {
      const res = await authApi.login(credentials)
      // 保存令牌
      setToken(res.accessToken, res.refreshToken)
      // 获取用户信息
      await fetchUserInfo()
      return res
    } catch (error) {
      clearAuth()
      throw error
    }
  }

  // 注册方法
  const register = async (data) => {
    try {
      const res = await authApi.register(data)
      // 注册成功后自动登录
      setToken(res.accessToken, res.refreshToken)
      await fetchUserInfo()
      return res
    } catch (error) {
      clearAuth()
      throw error
    }
  }

  // 登出方法
  const logout = async () => {
    try {
      // 调用后端登出接口，传递当前 refreshToken
      if (refreshToken.value) {
        await authApi.logout(refreshToken.value)
      }
    } finally {
      // 无论后端登出是否成功，都清除本地状态
      clearAuth()
    }
  }

  // 刷新Token方法
  const refreshAccessToken = async () => {
    if (!refreshToken.value) {
      throw new Error('没有可用的刷新令牌')
    }
    try {
      const res = await authApi.refresh(refreshToken.value)
      setToken(res.accessToken, res.refreshToken)
      return res.accessToken
    } catch (error) {
      // 刷新失败，清除认证状态
      clearAuth()
      throw error
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    if (!token.value) {
      throw new Error('未登录')
    }
    try {
      const user = await authApi.getMe()
      setUserInfo(user)
      return user
    } catch (error) {
      // 获取用户信息失败，可能是token无效，清除状态
      clearAuth()
      throw error
    }
  }

  // 初始化：如果本地有token但无用户信息，则尝试获取用户信息
  const init = async () => {
    if (token.value && !userInfo.value) {
      try {
        await fetchUserInfo()
      } catch (error) {
        console.error('初始化获取用户信息失败:', error)
      }
    }
  }

  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    clearAuth,
    login,
    register,
    logout,
    refreshAccessToken,
    fetchUserInfo,
    init
  }
})