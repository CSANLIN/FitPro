<template>
  <div class="register-container">
    <div class="register-background">
      <div class="register-background-overlay"></div>
    </div>

    <div class="register-content">
      <div class="register-card">
        <!-- Logo和标题 -->
        <div class="register-header">
          <div class="register-logo">
            <div class="logo-icon">
              <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="20" cy="20" r="20" fill="var(--primary-color)" />
                <path d="M15 25L20 30L30 15" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M10 20L15 25" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </div>
            <h1 class="register-title">FitPro</h1>
          </div>
          <p class="register-subtitle">创建您的健身账户</p>
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

    if (userRoleWithPrefix === 'ROLE_SUPER_ADMIN' || userRoleWithPrefix === 'ROLE_COACH') {
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
}

.register-background {
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

.register-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 450px;
  padding: var(--space-xl);
}

.register-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: var(--space-xxl) var(--space-xl);
  box-shadow: var(--shadow-xl);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
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