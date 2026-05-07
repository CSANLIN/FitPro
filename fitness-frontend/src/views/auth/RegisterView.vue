<template>
  <div class="register-container">
    <!-- 动态发光气泡背景 (Be.run Aesthetic) -->
    <div class="background-blobs">
      <div class="blob-yellow"></div>
      <div class="blob-red"></div>
      <div class="blob-accent"></div>
    </div>

    <div class="register-content">
      <div class="register-card">
        <!-- Logo和标题 -->
        <div class="register-header">
          <div class="register-logo">
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
            <h1 class="register-title">FitPro</h1>
          </div>
          <p class="register-subtitle">智能健身管理中心</p>
        </div>

        <!-- 注册表单 -->
        <div class="register-form">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="0"
            @submit.prevent="handleRegister"
          >
            <!-- 用户名输入 -->
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名（字母、数字、下划线）"
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
                placeholder="请输入密码（6-20位字符）"
                size="large"
                :prefix-icon="Lock"
                clearable
                show-password
              />
            </el-form-item>

            <!-- 确认密码 -->
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                :prefix-icon="Lock"
                clearable
                show-password
              />
            </el-form-item>

            <!-- 昵称输入 -->
            <el-form-item prop="nickname">
              <el-input
                v-model="form.nickname"
                placeholder="请输入昵称（1-20位字符）"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <!-- 手机号输入 -->
            <el-form-item prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号"
                size="large"
                :prefix-icon="Phone"
                clearable
              />
            </el-form-item>

            <!-- 注册协议 -->
            <div class="register-agreement">
              <el-checkbox v-model="form.agreed">
                我已阅读并同意 <router-link to="/terms" class="agreement-link">《用户服务协议》</router-link> 和 <router-link to="/privacy" class="agreement-link">《隐私政策》</router-link>
              </el-checkbox>
            </div>

            <!-- 注册按钮 -->
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleRegister"
                class="register-button"
              >
                注册
              </el-button>
            </el-form-item>

            <!-- 登录链接 -->
            <div class="login-link">
              已有账号？
              <router-link to="/login" class="login-link-button">
                立即登录
              </router-link>
            </div>
          </el-form>
        </div>

        <!-- 底部信息 -->
        <div class="register-footer">
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
import { User, Lock, Phone } from '@element-plus/icons-vue'
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
  confirmPassword: '',
  nickname: '',
  phone: '',
  agreed: false
})

// 自定义验证规则：确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '昵称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  agreed: [
    { required: true, message: '请阅读并同意用户协议和隐私政策', trigger: 'change' }
  ]
}

// 加载状态
const loading = ref(false)

// 注册处理
const handleRegister = async () => {
  // 表单验证
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    // 调用 authStore 的注册方法
    await authStore.register({
      username: form.username,
      password: form.password,
      confirmPassword: form.confirmPassword,
      nickname: form.nickname,
      phone: form.phone,
      role: 'MEMBER' // 默认角色为普通会员
    })

    // 注册成功，显示消息
    ElMessage.success('注册成功，已自动登录')

    // 注册成功后自动登录，根据用户角色跳转到不同页面
    const userRole = authStore.userInfo?.role
    // 后端返回的角色不带 ROLE_ 前缀，但前端权限检查时需要带前缀
    const userRoleWithPrefix = userRole ? `ROLE_${userRole}` : null

    if (userRoleWithPrefix === 'ROLE_COACH') {
      await router.push('/coach/schedule')
    } else if (userRoleWithPrefix === 'ROLE_SUPER_ADMIN') {
      await router.push('/admin/dashboard')
    } else if (userRoleWithPrefix === 'ROLE_MEMBER') {
      await router.push('/app/profile')
    } else {
      // 未知角色，跳转到默认页面
      await router.push('/')
    }

  } catch (error) {
    // 注册失败，错误消息已在 request.js 中显示
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background-color: var(--bg-base);
  background-image: url('https://images.unsplash.com/photo-1534438327276-14e5300c3a48?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
  background-size: cover;
  background-position: center;
}

.register-container::before {
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

.register-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 450px;
  padding: var(--space-xl);
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 36px;
  padding: 40px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0,0,0,0.02);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.register-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.register-logo {
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

.register-title {
  font-size: var(--font-size-xxl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0;
}

.register-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.register-form {
  margin-bottom: var(--space-xl);
}

.register-agreement {
  margin-bottom: var(--space-xl);

  :deep(.el-checkbox) {
    color: var(--text-secondary);
    font-size: var(--font-size-sm);
  }

  .agreement-link {
    color: var(--primary-color);
    text-decoration: none;

    &:hover {
      color: var(--primary-dark);
      text-decoration: underline;
    }
  }
}

.register-button {
  width: 100%;
  font-weight: var(--font-weight-medium);
  height: 48px;
  border-radius: var(--border-radius-lg);
}

.login-link {
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin-top: var(--space-lg);

  .login-link-button {
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

.register-footer {
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
  .register-content {
    padding: var(--space-md);
  }

  .register-card {
    padding: var(--space-xl) var(--space-lg);
  }
}
</style>