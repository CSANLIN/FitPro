<template>
  <div class="profile-view">
    <!-- ===== 区域一：高级用户信息卡片 ===== -->
    <div class="user-hero">
      <div class="hero-bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
      </div>
      <div class="hero-glass">
        <div class="hero-content">
          <div class="avatar-wrapper" @click="openEditProfile">
            <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
              {{ userInfo.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="edit-badge"><el-icon><Edit /></el-icon></div>
          </div>
          <div class="hero-text">
            <div class="hero-name">
              {{ userInfo.nickname || '运动健将' }}
              <el-tag :type="roleTagType" size="small" effect="dark" round class="hero-role-tag">
                {{ roleLabel }}
              </el-tag>
            </div>
            <div class="hero-phone">
              <el-icon><Iphone /></el-icon> {{ userInfo.phone || '未绑定手机号' }}
            </div>
          </div>
        </div>
        
        <div class="hero-stats">
          <div class="stat-block">
            <div class="stat-num">{{ statsData.streakDays }}</div>
            <div class="stat-desc">连续签到</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-block">
            <div class="stat-num">{{ statsData.monthCount }}</div>
            <div class="stat-desc">本月打卡</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-block" @click="goMembership" style="cursor: pointer;">
            <div class="stat-num highlight">{{ membershipText || '--' }}</div>
            <div class="stat-desc">会籍状态 <el-icon><ArrowRight /></el-icon></div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== 区域二：功能列表 ===== -->
    <div class="menu-group">
      <div class="menu-title">数据与记录</div>
      <div class="menu-list">
        <div class="menu-item" @click="goBodyData">
          <div class="menu-icon" style="background: #e0f2fe; color: #0ea5e9;"><el-icon><DataLine /></el-icon></div>
          <div class="menu-content">
            <span class="menu-label">身体数据</span>
            <span class="menu-hint">记录你的蜕变</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="goBooking">
          <div class="menu-icon" style="background: #e0ffe0; color: #10b981;"><el-icon><Ticket /></el-icon></div>
          <div class="menu-content">
            <span class="menu-label">我的预约</span>
            <span class="menu-hint">查看已预约的课程</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="goRecord">
          <div class="menu-icon" style="background: #f3e8ff; color: #a855f7;"><el-icon><List /></el-icon></div>
          <div class="menu-content">
            <span class="menu-label">训练记录</span>
            <span class="menu-hint">查看历史表现</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <div class="menu-group">
      <div class="menu-title">账户安全</div>
      <div class="menu-list">
        <div class="menu-item" @click="openChangePassword">
          <div class="menu-icon" style="background: #fee2e2; color: #ef4444;"><el-icon><Lock /></el-icon></div>
          <div class="menu-content">
            <span class="menu-label">修改密码</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="handleFeedback">
          <div class="menu-icon" style="background: #fef3c7; color: #f59e0b;"><el-icon><ChatLineSquare /></el-icon></div>
          <div class="menu-content">
            <span class="menu-label">帮助与反馈</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- ===== 退出登录 ===== -->
    <div class="logout-section">
      <el-button color="#f87171" plain @click="handleLogout" :loading="logoutLoading" class="logout-btn" round>
        退出当前账号
      </el-button>
    </div>

    <!-- ===== Dialog：编辑资料 ===== -->
    <el-dialog v-model="editDialogVisible" title="编辑个人资料" width="90%" max-width="520px" top="5vh" destroy-on-close class="custom-dialog">
      <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="70px" label-position="left">
        <div class="avatar-upload-container">
          <el-avatar :size="80" :src="profileForm.avatar" class="edit-avatar-preview">
            {{ userInfo.nickname?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="avatar-actions">
            <el-button type="primary" size="small" round @click="triggerAvatarUpload">更换头像</el-button>
            <input ref="avatarInputRef" type="file" accept="image/*" style="display:none" @change="handleAvatarChange" />
          </div>
        </div>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="20" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="profileForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker v-model="profileForm.birthday" type="date" placeholder="选择生日" style="width:100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false" round>取消</el-button>
          <el-button type="primary" @click="handleSaveProfile" :loading="profileLoading" round>保存修改</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ===== Dialog：修改密码 ===== -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="90%" max-width="460px" top="20vh" destroy-on-close class="custom-dialog">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false" round>取消</el-button>
          <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading" round>确认修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user'
import { checkinApi } from '@/api/checkin'
import { membershipApi } from '@/api/membership'
import { uploadApi } from '@/api/upload'
import {
  DataLine, Lock, List, ChatLineSquare, Edit, Iphone, ArrowRight, Ticket
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

// ===================== 用户信息 =====================
const userInfo = computed(() => authStore.userInfo || {})

const roleTagType = computed(() => {
  const map = { SUPER_ADMIN: 'danger', COACH: 'warning', MEMBER: 'primary' }
  return map[userInfo.value.role] || 'info'
})

const roleLabel = computed(() => {
  const map = { SUPER_ADMIN: '管理员', COACH: '教练', MEMBER: 'VIP会员' }
  return map[userInfo.value.role] || userInfo.value.role
})

// ===================== 快捷统计 =====================
const statsLoading = ref(false)
const statsData = reactive({ streakDays: 0, monthCount: 0 })
const membershipText = ref('')

const fetchStats = async () => {
  statsLoading.value = true
  try {
    const [stats, memberships] = await Promise.allSettled([
      checkinApi.myStats(),
      membershipApi.myActiveMembership()
    ])
    if (stats.status === 'fulfilled' && stats.value) {
      statsData.streakDays = stats.value.streakDays ?? 0
      statsData.monthCount = stats.value.monthCount ?? 0
    }
    if (memberships.status === 'fulfilled' && memberships.value) {
      const m = memberships.value
      if (m.remainingDays != null && m.remainingDays > 0) {
        membershipText.value = `剩 ${m.remainingDays} 天`
      } else if (m.remainingTimes != null && m.remainingTimes > 0) {
        membershipText.value = `剩 ${m.remainingTimes} 次`
      } else {
        membershipText.value = m.cardName || '已激活'
      }
    } else {
      membershipText.value = '去开通'
    }
  } catch (e) {
    console.warn('获取统计信息失败:', e)
  } finally {
    statsLoading.value = false
  }
}

// ===================== 功能导航 =====================
const goBodyData = () => router.push('/app/body-data')
const goMembership = () => router.push('/app/membership')
const goRecord = () => router.push('/app/record')
const goBooking = () => router.push('/app/course/booking')
const handleFeedback = () => {
  ElMessage.info('帮助反馈功能开发中，敬请期待')
}

// ===================== 编辑资料 =====================
const editDialogVisible = ref(false)
const profileFormRef = ref()
const profileLoading = ref(false)
const avatarInputRef = ref()

const profileForm = reactive({
  nickname: '',
  avatar: '',
  phone: '',
  email: '',
  gender: 0,
  birthday: ''
})

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '昵称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const openEditProfile = () => {
  const u = userInfo.value
  profileForm.nickname = u.nickname || ''
  profileForm.avatar = u.avatar || ''
  profileForm.phone = u.phone || ''
  profileForm.email = u.email || ''
  profileForm.gender = u.gender ?? 0
  profileForm.birthday = u.birthday || ''
  editDialogVisible.value = true
}

const handleSaveProfile = async () => {
  try {
    await profileFormRef.value.validate()
    profileLoading.value = true
    const data = {
      nickname: profileForm.nickname,
      avatar: profileForm.avatar,
      phone: profileForm.phone,
      email: profileForm.email,
      gender: profileForm.gender,
      birthday: profileForm.birthday
    }
    await userApi.updateProfile(data)
    authStore.setUserInfo({ ...authStore.userInfo, ...data })
    ElMessage.success('资料已更新')
    editDialogVisible.value = false
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message
    if (msg) {
      ElMessage.error(msg)
    }
  } finally {
    profileLoading.value = false
  }
}

const triggerAvatarUpload = () => avatarInputRef.value?.click()

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  try {
    const url = await uploadApi.upload(file)
    profileForm.avatar = url
    ElMessage.success('头像已上传')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '头像上传失败')
  }
  event.target.value = ''
}

// ===================== 修改密码 =====================
const passwordDialogVisible = ref(false)
const passwordFormRef = ref()
const passwordLoading = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
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

const openChangePassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

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
    passwordDialogVisible.value = false
    await authStore.logout()
    router.push('/login')
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message
    if (msg) {
      ElMessage.error(msg)
    }
  } finally {
    passwordLoading.value = false
  }
}

// ===================== 退出登录 =====================
const logoutLoading = ref(false)

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出当前账号吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'custom-message-box'
    })
    logoutLoading.value = true
    await authStore.logout()
    router.push('/login')
  } catch {
    // cancelled or error
  } finally {
    logoutLoading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.profile-view {
  padding-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ===== 高级用户信息卡片 ===== */
.user-hero {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--primary-color) 0%, #6366f1 100%);
  padding: 2px;
  box-shadow: 0 12px 32px rgba(99, 102, 241, 0.25);
}

.hero-bg-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
}

.shape-1 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.2);
  top: -50px;
  right: -20px;
}

.shape-2 {
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.1);
  bottom: -40px;
  left: -40px;
}

.hero-glass {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 22px;
  padding: 24px 20px 20px;
}

.hero-content {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  border: 3px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  font-size: 32px;
  font-weight: bold;
}

.edit-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: white;
  color: var(--primary-color);
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  font-size: 14px;
}

.hero-text {
  color: white;
  flex: 1;
}

.hero-name {
  font-size: 22px;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.hero-role-tag {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.hero-phone {
  font-size: 13px;
  opacity: 0.9;
  display: flex;
  align-items: center;
  gap: 4px;
}

.hero-stats {
  display: flex;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 16px;
  padding: 16px;
  justify-content: space-between;
  align-items: center;
}

.stat-block {
  flex: 1;
  text-align: center;
  color: white;
}

.stat-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
}

.stat-num {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-num.highlight {
  color: #fde047;
}

.stat-desc {
  font-size: 11px;
  opacity: 0.8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

/* ===== 菜单列表 ===== */
.menu-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-title {
  font-size: 14px;
  font-weight: 700;
  color: #64748b;
  padding-left: 8px;
}

.menu-list {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 16px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.menu-item:not(:last-child)::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 68px;
  right: 16px;
  height: 1px;
  background: #f1f5f9;
}

.menu-item:hover {
  background: #f8fafc;
}

.menu-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.menu-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.menu-label {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.menu-hint {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.menu-arrow {
  color: #cbd5e1;
  font-size: 16px;
}

/* ===== 退出登录 ===== */
.logout-section {
  margin-top: 10px;
  padding: 0 16px;
}

.logout-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

/* ===== 弹窗样式调整 ===== */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.edit-avatar-preview {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  font-size: 32px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-glass {
    padding: 20px 16px 16px;
  }
  
  .user-avatar {
    width: 64px !important;
    height: 64px !important;
  }
  
  .hero-name {
    font-size: 18px;
  }
  
  .menu-list {
    border-radius: 16px;
  }
}
</style>
