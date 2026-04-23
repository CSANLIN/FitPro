<template>
  <div class="login-container">
    <div class="login-background">
      <div class="login-background-overlay"></div>
    </div>

    <div class="login-content">
      <div class="login-card">
        <!-- Logo和标题 -->
        <div class="login-header">
          <div class="login-logo">
            <div class="logo-icon">
              <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="20" cy="20" r="20" fill="var(--primary-color)" />
                <path d="M15 25L20 30L30 15" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M10 20L15 25" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </div>
            <h1 class="login-title">FitPro</h1>
          </div>
          <p class="login-subtitle">智能健身管理系统</p>
        </div>

        <!-- 登录表单 -->
        <div class="login-form">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="0"
            @submit.prevent="handleLogin"
          >
            <!-- 用户名输入 -->
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <!-- 密码输入 -->
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                clearable
                show-password
              />
            </el-form-item>

            <!-- 记住我和忘记密码 -->
            <div class="login-options">
              <el-checkbox v-model="form.rememberMe" label="记住我" />
              <router-link to="/forgot-password" class="forgot-password">
                忘记密码?
              </router-link>
            </div>

            <!-- 登录按钮 -->
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="login-button"
              >
                登录
              </el-button>
            </el-form-item>

            <!-- 注册链接 -->
            <div class="register-link">
              还没有账号？
              <router-link to="/register" class="register-link-button">
                立即注册
              </router-link>
            </div>
          </el-form>
        </div>

        <!-- 底部信息 -->
        <div class="login-footer">
          <p class="copyright">© 2026 FitPro 健身管理系统. All rights reserved.</p>
          <p class="version">版本 v1.0.0</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

// 路由
const router = useRouter()

// Pinia Store
const authStore = useAuthStore()

// 表单引用
const formRef = ref()

// 表单数据
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 加载状态
const loading = ref(false)

// 登录处理
const handleLogin = async () => {
  // 表单验证
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    console.log('开始登录，用户名:', form.username)

    // 调用 authStore 的登录方法
    const loginResult = await authStore.login({
      username: form.username,
      password: form.password
    })

    console.log('登录API返回结果:', loginResult)
    console.log('authStore.userInfo:', authStore.userInfo)
    console.log('authStore.token:', authStore.token)

    // 登录成功，显示消息
    ElMessage.success('登录成功')

    // 等待一小段时间确保状态更新
    await new Promise(resolve => setTimeout(resolve, 100))

    // 确保用户信息已加载
    if (!authStore.userInfo) {
      console.log('用户信息为空，尝试重新获取')
      try {
        await authStore.fetchUserInfo()
        console.log('重新获取用户信息成功:', authStore.userInfo)
      } catch (fetchError) {
        console.error('重新获取用户信息失败:', fetchError)
        throw new Error('获取用户信息失败，请重试')
      }
    }

    // 根据用户角色跳转到不同页面
    const userRole = authStore.userInfo?.role
    console.log('用户角色:', userRole)

    // 后端返回的角色不带 ROLE_ 前缀，但前端权限检查时需要带前缀
    const userRoleWithPrefix = userRole ? `ROLE_${userRole}` : null
    console.log('带前缀的角色:', userRoleWithPrefix)

    if (userRoleWithPrefix === 'ROLE_SUPER_ADMIN' || userRoleWithPrefix === 'ROLE_COACH') {
      console.log('跳转到管理端仪表盘')
      await router.push('/admin/dashboard')
    } else if (userRoleWithPrefix === 'ROLE_MEMBER') {
      console.log('跳转到会员端个人中心')
      await router.push('/app/profile')
    } else {
      // 未知角色，跳转到默认页面
      console.log('未知角色，跳转到首页')
      await router.push('/')
    }

  } catch (error) {
    // 登录失败，错误消息已在 request.js 中显示
    console.error('登录失败:', error)
    if (error.message !== '获取用户信息失败，请重试') {
      ElMessage.error(error.message || '登录失败，请检查用户名和密码')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
  background-size: cover;
  background-position: center;
  z-index: 0;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.9) 0%, rgba(103, 194, 58, 0.7) 100%);
    opacity: 0.9;
  }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: var(--space-xl);
}

.login-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: var(--space-xxl) var(--space-xl);
  box-shadow: var(--shadow-xl);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-sm);
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-title {
  font-size: var(--font-size-xxl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0;
}

.login-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.login-form {
  margin-bottom: var(--space-xl);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xl);

  :deep(.el-checkbox) {
    color: var(--text-secondary);
  }
}

.forgot-password {
  font-size: var(--font-size-sm);
  color: var(--primary-color);
  text-decoration: none;
  transition: color 0.2s;

  &:hover {
    color: var(--primary-dark);
    text-decoration: underline;
  }
}

.login-button {
  width: 100%;
  font-weight: var(--font-weight-medium);
  height: 48px;
  border-radius: var(--border-radius-lg);
}

.register-link {
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin-top: var(--space-lg);

  .register-link-button {
    color: var(--primary-color);
    font-weight: var(--font-weight-medium);
    text-decoration: none;
    margin-left: var(--space-xs);
    transition: color 0.2s;

    &:hover {
      color: var(--primary-dark);
      text-decoration: underline;
    }
  }
}

.login-footer {
  text-align: center;
  padding-top: var(--space-xl);
  border-top: 1px solid var(--border-light);
}

.copyright {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-xs);
}

.version {
  font-size: var(--font-size-xs);
  color: var(--text-placeholder);
  margin: 0;
}

// 响应式调整
@media (max-width: 768px) {
  .login-content {
    padding: var(--space-md);
  }

  .login-card {
    padding: var(--space-xl) var(--space-lg);
  }
}
</style>