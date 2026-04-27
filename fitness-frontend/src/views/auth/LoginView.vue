<template>
  <div class="login-container">
    <!-- 动态发光气泡背景 (Be.run Aesthetic) -->
    <div class="background-blobs">
      <div class="blob-yellow"></div>
      <div class="blob-red"></div>
      <div class="blob-accent"></div>
    </div>

    <div class="login-content">
      <div class="login-card">
        <!-- Logo和标题 -->
        <div class="login-header">
          <div class="login-logo">
            <div class="logo-icon">
              <!-- FitPro Logo (暗黑色调哑铃) -->
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="24" cy="24" r="24" fill="#242529" />
                <g transform="rotate(-45 24 24)">
                  <rect x="12" y="20" width="6" height="8" rx="2" fill="#ffcc2e" />
                  <rect x="30" y="20" width="6" height="8" rx="2" fill="#ffcc2e" />
                  <rect x="18" y="22" width="12" height="4" fill="#ffcc2e" />
                  <rect x="8" y="16" width="4" height="16" rx="2" fill="#ffcc2e" />
                  <rect x="36" y="16" width="4" height="16" rx="2" fill="#ffcc2e" />
                </g>
              </svg>
            </div>
            <h1 class="login-title">FitPro</h1>
          </div>
          <p class="login-subtitle">智能健身管理中心</p>
        </div>

        <!-- 登录表单 -->
        <div class="login-form">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="0"
            @submit.prevent="handleLogin"
            class="be-form"
          >
            <!-- 用户名输入 -->
            <el-form-item prop="username">
              <div class="input-label">用户名</div>
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
                clearable
                class="pill-input"
              />
            </el-form-item>

            <!-- 密码输入 -->
            <el-form-item prop="password">
              <div class="input-label">密码</div>
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                clearable
                show-password
                class="pill-input"
              />
            </el-form-item>

            <!-- 记住我和忘记密码 -->
            <div class="login-options">
              <el-checkbox v-model="form.rememberMe" label="记住我" class="custom-checkbox" />
              <router-link to="/forgot-password" class="forgot-password">
                忘记密码？
              </router-link>
            </div>

            <!-- 登录按钮 -->
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="login-button btn-dark"
              >
                立即登录
              </el-button>
            </el-form-item>

            <!-- 注册链接 -->
            <div class="register-link">
              新用户？
              <router-link to="/register" class="register-link-button">
                创建账号
              </router-link>
            </div>
          </el-form>
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

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

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

const handleLogin = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    await authStore.login({ username: form.username, password: form.password })
    ElMessage.success('欢迎回来！')
    await new Promise(resolve => setTimeout(resolve, 100))

    if (!authStore.userInfo) {
      await authStore.fetchUserInfo()
    }

    const userRole = authStore.userInfo?.role || ''
    const userRoleWithPrefix = userRole.startsWith('ROLE_') ? userRole : `ROLE_${userRole}`

    if (userRoleWithPrefix === 'ROLE_SUPER_ADMIN' || userRoleWithPrefix === 'ROLE_COACH') {
      await router.push('/admin/dashboard')
    } else if (userRoleWithPrefix === 'ROLE_MEMBER') {
      await router.push('/app/home')
    } else {
      await router.push('/')
    }
  } catch (error) {
    console.error('登录失败:', error)
    if (error.message !== '获取用户信息失败，请重试') {
      ElMessage.error(error.message || '登录失败，请检查您的凭证')
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
  background-color: var(--bg-base); /* 温暖的米灰色背景 */
  background-image: url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
  background-size: cover;
  background-position: center;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(20, 20, 20, 0.8) 0%, rgba(36, 37, 41, 0.9) 100%);
  z-index: 0;
}

/* 环境光晕 blobs */
.background-blobs {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.blob-yellow {
  position: absolute;
  top: -10%;
  right: -5%;
  width: 600px;
  height: 600px;
  background: var(--blob-yellow);
  filter: blur(100px);
  border-radius: 50%;
  animation: float 15s ease-in-out infinite;
}

.blob-red {
  position: absolute;
  bottom: -20%;
  left: 10%;
  width: 500px;
  height: 500px;
  background: var(--blob-red);
  filter: blur(120px);
  border-radius: 50%;
  animation: float 20s ease-in-out infinite reverse;
}

.blob-accent {
  position: absolute;
  top: 40%;
  left: 40%;
  width: 400px;
  height: 400px;
  background: rgba(255, 154, 68, 0.4);
  filter: blur(100px);
  border-radius: 50%;
  animation: float 18s ease-in-out infinite 2s;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0, 0) scale(1); }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 460px;
  padding: 24px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 36px;
  padding: 48px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0,0,0,0.02);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 8px;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-title {
  font-size: 32px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -1px;
}

.login-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  font-weight: 500;
  margin: 0;
}

/* 表单细节优化 */
.be-form {
  margin-bottom: 24px;
}

.input-label {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  margin-left: 8px;
  line-height: 1;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
}

:deep(.pill-input .el-input__wrapper) {
  background-color: var(--bg-lighter) !important;
  border: 1px solid transparent !important;
  box-shadow: none !important;
  padding: 12px 20px !important;
  border-radius: 999px !important;
  transition: all 0.2s;

  &:hover, &.is-focus {
    background-color: white !important;
    border-color: var(--primary-color) !important;
    box-shadow: 0 4px 15px rgba(255, 204, 46, 0.15) !important;
  }
}

:deep(.pill-input .el-input__inner) {
  font-weight: 600;
  color: var(--text-primary);
  height: 24px;
  
  &::placeholder {
    font-weight: 500;
    color: var(--text-placeholder);
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 0 8px;
}

:deep(.custom-checkbox .el-checkbox__label) {
  font-weight: 600;
  color: var(--text-regular);
}

:deep(.custom-checkbox .el-checkbox__inner) {
  border-radius: 6px;
  width: 18px;
  height: 18px;
  
  &::after {
    top: 2px;
    left: 6px;
  }
}

.forgot-password {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-regular);
  text-decoration: none;
  transition: color 0.2s;

  &:hover {
    color: var(--text-primary);
  }
}

.login-button {
  width: 100%;
  font-size: 16px;
  letter-spacing: 0.5px;
  height: 56px !important;
  margin-top: 8px;
}

.register-link {
  text-align: center;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-top: 32px;

  .register-link-button {
    color: var(--text-primary);
    font-weight: 700;
    text-decoration: none;
    margin-left: 6px;
    transition: color 0.2s;

    &:hover {
      color: var(--primary-color);
    }
  }
}

@media (max-width: 768px) {
  .login-card {
    padding: 40px 24px;
    border-radius: 28px;
  }
}
</style>