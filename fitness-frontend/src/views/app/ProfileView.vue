<template>
  <div class="profile-view">
    <h1>个人中心</h1>
    <p>待实现：用户信息、身体数据、训练记录</p>

    <div class="logout-section">
      <el-button type="danger" @click="handleLogout" :loading="logoutLoading">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const logoutLoading = ref(false)

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
</script>

<style scoped>
.profile-view {
  padding: 20px;
}

.logout-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
  text-align: center;
}
</style>