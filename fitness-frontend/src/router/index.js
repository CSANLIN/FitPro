import { createRouter, createWebHistory } from 'vue-router'
import { authApi } from '@/api/auth'

// 路由懒加载
const LoginView = () => import('@/views/auth/LoginView.vue')
const RegisterView = () => import('@/views/auth/RegisterView.vue')
const AdminLayout = () => import('@/layout/AdminLayout.vue')
const AppLayout = () => import('@/layout/AppLayout.vue')

// 错误页面
const NotFoundView = () => import('@/views/error/NotFoundView.vue')
const ForbiddenView = () => import('@/views/error/ForbiddenView.vue')

// 管理端子路由（后续填充）
const AdminDashboard = () => import('@/views/admin/DashboardView.vue')

// 会员端子路由（后续填充）
const AppProfile = () => import('@/views/app/ProfileView.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 登录注册页（公开）
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
      meta: {
        title: '登录 - FitPro',
        requiresAuth: false,
        hideLayout: true
      }
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterView,
      meta: {
        title: '注册 - FitPro',
        requiresAuth: false,
        hideLayout: true
      }
    },

    // 管理端布局
    {
      path: '/admin',
      name: 'Admin',
      component: AdminLayout,
      meta: {
        title: '管理端',
        requiresAuth: true,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_COACH']
      },
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: AdminDashboard,
          meta: {
            title: '仪表盘',
            icon: 'Dashboard'
          }
        }
        // 其他管理端路由后续添加
      ]
    },

    // 会员端布局
    {
      path: '/app',
      name: 'App',
      component: AppLayout,
      meta: {
        title: '会员端',
        requiresAuth: true,
        roles: ['ROLE_MEMBER']
      },
      redirect: '/app/profile',
      children: [
        {
          path: 'profile',
          name: 'AppProfile',
          component: AppProfile,
          meta: {
            title: '个人中心',
            icon: 'User'
          }
        }
        // 其他会员端路由后续添加
      ]
    },

    // 首页重定向
    {
      path: '/',
      redirect: '/login'
    },

    // 错误页面
    {
      path: '/404',
      name: 'NotFound',
      component: NotFoundView,
      meta: {
        title: '页面不存在',
        requiresAuth: false,
        hideLayout: true
      }
    },
    {
      path: '/403',
      name: 'Forbidden',
      component: ForbiddenView,
      meta: {
        title: '无权限访问',
        requiresAuth: false,
        hideLayout: true
      }
    },

    // 404 通配路由
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404'
    }
  ]
})

// 导航守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 白名单：不需要认证的路由直接放行
  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  let userInfo = userInfoStr ? JSON.parse(userInfoStr) : null

  // 情况1：需要认证但未登录，跳转到登录页
  if (!token) {
    next('/login')
    return
  }

  // 情况2：已登录但访问登录/注册页，根据角色重定向到对应首页
  if (to.path === '/login' || to.path === '/register') {
    // 如果本地有用户信息，直接重定向
    if (userInfo && userInfo.role) {
      const userRoleWithPrefix = `ROLE_${userInfo.role}`
      if (userRoleWithPrefix === 'ROLE_SUPER_ADMIN' || userRoleWithPrefix === 'ROLE_COACH') {
        next('/admin/dashboard')
      } else {
        next('/app/profile')
      }
    } else {
      // 本地没有用户信息，尝试获取
      try {
        const freshUserInfo = await authApi.getMe()
        localStorage.setItem('userInfo', JSON.stringify(freshUserInfo))
        const userRoleWithPrefix = `ROLE_${freshUserInfo.role}`
        if (userRoleWithPrefix === 'ROLE_SUPER_ADMIN' || userRoleWithPrefix === 'ROLE_COACH') {
          next('/admin/dashboard')
        } else {
          next('/app/profile')
        }
      } catch (error) {
        // 获取用户信息失败，可能是token无效，清除认证状态并停留在登录页
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('userInfo')
        next()
      }
    }
    return
  }

  // 情况3：已登录但本地用户信息为空，尝试获取用户信息
  if (!userInfo) {
    try {
      userInfo = await authApi.getMe()
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    } catch (error) {
      // 获取用户信息失败，清除认证状态并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      next('/login')
      return
    }
  }

  // 情况4：检查角色权限
  if (to.meta.roles && userInfo.role) {
    const userRoleWithPrefix = `ROLE_${userInfo.role}`
    if (!to.meta.roles.includes(userRoleWithPrefix)) {
      next('/403')
      return
    }
  }

  // 所有检查通过，放行
  next()
})

// 全局后置钩子（可记录页面访问日志等）
router.afterEach((to, from) => {
  // 可在此处添加页面访问统计等
})

export default router