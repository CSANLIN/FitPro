<template>
  <div class="profile-view">
    <h1>个人中心</h1>

    <el-tabs v-model="activeTab" class="profile-tabs">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="profile">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" size="small" @click="handleSaveProfile" :loading="profileLoading">
                保存修改
              </el-button>
            </div>
          </template>

          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="头像">
                  <div class="avatar-upload">
                    <el-avatar :size="100" :src="profileForm.avatar" />
                    <div class="avatar-upload-actions">
                      <el-button type="primary" size="small" @click="triggerAvatarUpload">
                        更换头像
                      </el-button>
                      <input
                        ref="avatarInputRef"
                        type="file"
                        accept="image/*"
                        style="display: none"
                        @change="handleAvatarChange"
                      />
                      <el-button size="small" @click="profileForm.avatar = ''" v-if="profileForm.avatar">
                        移除
                      </el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" disabled />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="角色" prop="role">
                  <el-input v-model="profileForm.role" disabled />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="profileForm.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="未知" :value="0" />
                    <el-option label="男" :value="1" />
                    <el-option label="女" :value="2" />
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="生日" prop="birthday">
                  <el-date-picker
                    v-model="profileForm.birthday"
                    type="date"
                    placeholder="选择生日"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-card class="password-card">
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
            </div>
          </template>

          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                确认修改
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <div class="logout-section">
      <el-button type="danger" @click="handleLogout" :loading="logoutLoading">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user'
import { SwitchButton } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

// 标签页
const activeTab = ref('profile')

// 表单引用
const profileFormRef = ref()
const passwordFormRef = ref()
const avatarInputRef = ref()

// 加载状态
const logoutLoading = ref(false)
const profileLoading = ref(false)
const passwordLoading = ref(false)

// 个人信息表单
const profileForm = reactive({
  username: '',
  role: '',
  nickname: '',
  avatar: '',
  phone: '',
  email: '',
  gender: 0,
  birthday: ''
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '昵称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 初始化个人信息
const initProfile = () => {
  const userInfo = authStore.userInfo
  if (userInfo) {
    profileForm.username = userInfo.username || ''
    profileForm.role = userInfo.role || ''
    profileForm.nickname = userInfo.nickname || ''
    profileForm.avatar = userInfo.avatar || ''
    profileForm.phone = userInfo.phone || ''
    profileForm.email = userInfo.email || ''
    profileForm.gender = userInfo.gender || 0
    profileForm.birthday = userInfo.birthday || ''
  }
}

// 保存个人信息
const handleSaveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    profileLoading.value = true

    const updateData = {
      nickname: profileForm.nickname,
      avatar: profileForm.avatar,
      phone: profileForm.phone,
      email: profileForm.email,
      gender: profileForm.gender,
      birthday: profileForm.birthday
    }

    await userApi.updateProfile(updateData)

    // 更新本地用户信息
    const updatedUserInfo = { ...authStore.userInfo, ...updateData }
    authStore.setUserInfo(updatedUserInfo)

    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('保存个人信息失败:', error)
    ElMessage.error(error.response?.data?.message || '保存失败，请稍后重试')
  } finally {
    profileLoading.value = false
  }
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

// 处理头像文件选择
const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 简单的前端预览（实际项目中应上传到服务器）
  const reader = new FileReader()
  reader.onload = (e) => {
    profileForm.avatar = e.target.result
  }
  reader.readAsDataURL(file)

  // 清空 input 值，允许重复选择同一文件
  event.target.value = ''
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true

    await userApi.updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword
    })

    ElMessage.success('密码修改成功，请重新登录')

    // 密码修改成功后自动登出
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.response?.data?.message || '修改失败，请稍后重试')
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  nextTick(() => {
    passwordFormRef.value?.clearValidate()
  })
}

// 退出登录
const handleLogout = async () => {
  logoutLoading.value = true
  try {
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('登出失败:', error)
    // 即使登出失败，也清除本地状态并跳转到登录页
    authStore.clearAuth()
    router.push('/login')
  } finally {
    logoutLoading.value = false
  }
}

// 页面加载时初始化
onMounted(() => {
  initProfile()
})
</script>

<style scoped>
.profile-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-tabs {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-upload-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.profile-card,
.password-card {
  margin-top: 20px;
}

.logout-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
  text-align: center;
}
</style>