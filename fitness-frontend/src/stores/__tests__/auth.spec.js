import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

// Mock authApi
vi.mock('@/api/auth', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    refresh: vi.fn(),
    getMe: vi.fn()
  }
}))

import { authApi } from '@/api/auth'

describe('useAuthStore', () => {
  beforeEach(() => {
    // 重置 Pinia 状态
    setActivePinia(createPinia())
    // 清理 localStorage
    localStorage.clear()
    vi.clearAllMocks()
  })

  // ========== 初始状态测试 ==========

  it('初始状态应为空', () => {
    const store = useAuthStore()
    expect(store.token).toBe('')
    expect(store.refreshToken).toBe('')
    expect(store.userInfo).toBeNull()
    expect(store.isLoggedIn).toBe(false)
  })

  it('应能从 localStorage 读取初始 Token', () => {
    localStorage.setItem('token', 'saved_token')
    localStorage.setItem('refreshToken', 'saved_refresh')

    const store = useAuthStore()
    expect(store.token).toBe('saved_token')
    expect(store.refreshToken).toBe('saved_refresh')
    expect(store.isLoggedIn).toBe(true)
  })

  // ========== setToken / clearAuth 测试 ==========

  it('setToken 应保存令牌到 state 和 localStorage', () => {
    const store = useAuthStore()
    store.setToken('access123', 'refresh456')

    expect(store.token).toBe('access123')
    expect(store.refreshToken).toBe('refresh456')
    expect(store.isLoggedIn).toBe(true)
    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'access123')
    expect(localStorage.setItem).toHaveBeenCalledWith('refreshToken', 'refresh456')
  })

  it('clearAuth 应清除所有认证状态', () => {
    const store = useAuthStore()
    store.setToken('access123', 'refresh456')
    store.setUserInfo({ id: 1, username: 'test' })
    store.clearAuth()

    expect(store.token).toBe('')
    expect(store.refreshToken).toBe('')
    expect(store.userInfo).toBeNull()
    expect(store.isLoggedIn).toBe(false)
    expect(localStorage.removeItem).toHaveBeenCalledWith('token')
    expect(localStorage.removeItem).toHaveBeenCalledWith('refreshToken')
    expect(localStorage.removeItem).toHaveBeenCalledWith('userInfo')
  })

  // ========== setUserInfo 测试 ==========

  it('setUserInfo 应保存用户信息到 state 和 localStorage', () => {
    const store = useAuthStore()
    const user = { id: 1, username: 'test', nickname: 'Test', role: 'MEMBER' }
    store.setUserInfo(user)

    expect(store.userInfo).toEqual(user)
    expect(localStorage.setItem).toHaveBeenCalledWith('userInfo', JSON.stringify(user))
  })

  // ========== login 测试 ==========

  it('login 成功应保存令牌并获取用户信息', async () => {
    const credentials = { username: 'test', password: '123456' }
    const loginRes = { accessToken: 'access_token', refreshToken: 'refresh_token' }
    const userInfo = { id: 1, username: 'test', role: 'MEMBER' }

    authApi.login.mockResolvedValue(loginRes)
    authApi.getMe.mockResolvedValue(userInfo)

    const store = useAuthStore()
    const res = await store.login(credentials)

    expect(res).toEqual(loginRes)
    expect(store.token).toBe('access_token')
    expect(store.refreshToken).toBe('refresh_token')
    expect(store.userInfo).toEqual(userInfo)
    expect(authApi.login).toHaveBeenCalledWith(credentials)
    expect(authApi.getMe).toHaveBeenCalled()
  })

  it('login 失败应清除认证状态', async () => {
    authApi.login.mockRejectedValue(new Error('用户名或密码错误'))

    const store = useAuthStore()
    store.setToken('old_token', 'old_refresh')

    await expect(store.login({ username: 'wrong', password: 'wrong' })).rejects.toThrow()
    expect(store.token).toBe('')
    expect(store.isLoggedIn).toBe(false)
  })

  // ========== register 测试 ==========

  it('register 成功应保存令牌并获取用户信息', async () => {
    const data = { username: 'new', password: '123456', nickname: 'New', phone: '13800138000' }
    const registerRes = { accessToken: 'new_access', refreshToken: 'new_refresh' }
    const userInfo = { id: 2, username: 'new', role: 'MEMBER' }

    authApi.register.mockResolvedValue(registerRes)
    authApi.getMe.mockResolvedValue(userInfo)

    const store = useAuthStore()
    const res = await store.register(data)

    expect(res).toEqual(registerRes)
    expect(store.token).toBe('new_access')
    expect(store.userInfo).toEqual(userInfo)
    expect(authApi.register).toHaveBeenCalledWith(data)
  })

  it('register 失败应清除认证状态', async () => {
    authApi.register.mockRejectedValue(new Error('用户名已存在'))

    const store = useAuthStore()
    store.setToken('old_token', 'old_refresh')

    await expect(store.register({ username: 'dup' })).rejects.toThrow()
    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
  })

  // ========== logout 测试 ==========

  it('logout 应调用后端接口并清除本地状态', async () => {
    authApi.logout.mockResolvedValue(undefined)

    const store = useAuthStore()
    store.setToken('token', 'refresh')
    store.setUserInfo({ id: 1, username: 'test' })

    await store.logout()

    expect(authApi.logout).toHaveBeenCalledWith('refresh')
    expect(store.token).toBe('')
    expect(store.userInfo).toBeNull()
  })

  it('logout 即使后端失败也应清除本地状态', async () => {
    authApi.logout.mockRejectedValue(new Error('网络错误'))

    const store = useAuthStore()
    store.setToken('token', 'refresh')

    // logout 会抛出错误（API 失败），但状态仍被清除
    await expect(store.logout()).rejects.toThrow('网络错误')

    expect(store.token).toBe('')
    expect(store.isLoggedIn).toBe(false)
  })

  it('logout 无 refreshToken 时不应调用接口', async () => {
    const store = useAuthStore()
    await store.logout()

    expect(authApi.logout).not.toHaveBeenCalled()
  })

  // ========== refreshAccessToken 测试 ==========

  it('refreshAccessToken 成功应更新令牌', async () => {
    const refreshRes = { accessToken: 'new_access', refreshToken: 'new_refresh' }
    authApi.refresh.mockResolvedValue(refreshRes)

    const store = useAuthStore()
    store.setToken('old_access', 'old_refresh')

    const newToken = await store.refreshAccessToken()

    expect(newToken).toBe('new_access')
    expect(store.token).toBe('new_access')
    expect(store.refreshToken).toBe('new_refresh')
    expect(authApi.refresh).toHaveBeenCalledWith('old_refresh')
  })

  it('refreshAccessToken 无 refreshToken 应报错', async () => {
    const store = useAuthStore()

    await expect(store.refreshAccessToken()).rejects.toThrow('没有可用的刷新令牌')
  })

  it('refreshAccessToken 失败应清除认证状态', async () => {
    authApi.refresh.mockRejectedValue(new Error('令牌已过期'))

    const store = useAuthStore()
    store.setToken('old_access', 'old_refresh')

    await expect(store.refreshAccessToken()).rejects.toThrow()
    expect(store.token).toBe('')
  })

  // ========== fetchUserInfo 测试 ==========

  it('fetchUserInfo 成功应更新用户信息', async () => {
    const user = { id: 1, username: 'test', role: 'MEMBER' }
    authApi.getMe.mockResolvedValue(user)

    const store = useAuthStore()
    store.setToken('token', 'refresh')

    const result = await store.fetchUserInfo()
    expect(result).toEqual(user)
    expect(store.userInfo).toEqual(user)
  })

  it('fetchUserInfo 无 token 应报错', async () => {
    const store = useAuthStore()
    await expect(store.fetchUserInfo()).rejects.toThrow('未登录')
  })

  it('fetchUserInfo 失败应清除认证', async () => {
    authApi.getMe.mockRejectedValue(new Error('Token无效'))

    const store = useAuthStore()
    store.setToken('invalid_token', 'refresh')

    await expect(store.fetchUserInfo()).rejects.toThrow()
    expect(store.token).toBe('') // 被清除
  })

  // ========== init 测试 ==========

  it('init 有 token 无 userInfo 时应尝试获取', async () => {
    const user = { id: 1, username: 'test' }
    authApi.getMe.mockResolvedValue(user)

    const store = useAuthStore()
    store.setToken('token', 'refresh')

    await store.init()

    expect(store.userInfo).toEqual(user)
    expect(authApi.getMe).toHaveBeenCalled()
  })

  it('init 无 token 时不应调用 API', async () => {
    const store = useAuthStore()
    await store.init()

    expect(authApi.getMe).not.toHaveBeenCalled()
  })

  it('init 获取失败不应抛出异常', async () => {
    authApi.getMe.mockRejectedValue(new Error('网络错误'))

    const store = useAuthStore()
    store.setToken('token', 'refresh')

    // init 内部 catch 了错误，不应抛出
    await expect(store.init()).resolves.toBeUndefined()
    expect(store.userInfo).toBeNull()
  })
})
