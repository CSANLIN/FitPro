<template>
  <div class="coach-layout">
    <div class="app-wrapper">
      <!-- 悬浮侧边栏 -->
      <aside class="floating-sidebar">
        <div class="logo-area">
          <div class="logo-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="12" fill="var(--primary-color)"/>
              <g transform="rotate(-45 12 12)">
                <rect x="6" y="10" width="3" height="4" rx="1" fill="#242529" />
                <rect x="15" y="10" width="3" height="4" rx="1" fill="#242529" />
                <rect x="9" y="11" width="6" height="2" fill="#242529" />
                <rect x="4" y="8" width="2" height="8" rx="1" fill="#242529" />
                <rect x="18" y="8" width="2" height="8" rx="1" fill="#242529" />
              </g>
            </svg>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="true"
          :collapse-transition="false"
          router
          class="be-menu"
        >
          <el-tooltip content="课程编排" placement="right" :offset="20">
            <el-menu-item index="/coach/schedule" class="menu-pill">
              <el-icon><Calendar /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="授课分析" placement="right" :offset="20">
            <el-menu-item index="/coach/analysis" class="menu-pill">
              <el-icon><DataAnalysis /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="学员分析" placement="right" :offset="20">
            <el-menu-item index="/coach/students" class="menu-pill">
              <el-icon><User /></el-icon>
            </el-menu-item>
          </el-tooltip>
        </el-menu>

        <!-- 底部用户 -->
        <div class="sidebar-footer">
          <el-dropdown trigger="click" placement="right-end">
            <div class="mini-profile">
              <el-avatar :size="36" :src="userAvatar" class="profile-avatar" />
            </div>
            <template #dropdown>
              <el-dropdown-menu class="premium-dropdown">
                <el-dropdown-item divided @click="logout" class="danger-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="main-content">
        <header class="top-header">
          <div class="header-left">
            <h2 class="welcome-text">你好，{{ userName }}！</h2>
            <p class="welcome-sub">教练工作台</p>
          </div>
        </header>

        <div class="page-scroll-area">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Calendar, DataAnalysis, User, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const userName = computed(() => authStore.userInfo?.nickname || '教练')

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.coach-layout {
  height: 100vh;
  background-color: var(--bg-base);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.app-wrapper {
  width: 100%;
  height: 100vh;
  display: flex;
  padding: 24px;
  gap: 32px;
  max-width: 1600px;
}

.floating-sidebar {
  width: 80px;
  background: white;
  border-radius: var(--border-radius-xl);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
  flex-shrink: 0;
  z-index: 10;
}

.logo-area {
  margin-bottom: 40px;
}

.be-menu {
  border-right: none;
  background: transparent;
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  width: 100%;
}

:deep(.menu-pill) {
  width: 48px !important;
  height: 48px !important;
  border-radius: 50% !important;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 !important;
  margin: 0 auto !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: var(--text-secondary);

  .el-icon {
    margin: 0 !important;
    font-size: 20px;
  }

  &:hover {
    background-color: var(--bg-lighter) !important;
    color: var(--text-primary);
  }

  &.is-active {
    background-color: var(--accent-dark) !important;
    color: var(--primary-color) !important;
    box-shadow: 0 10px 20px rgba(36, 37, 41, 0.2);
    transform: translateY(-2px);
  }

  &::before {
    display: none !important;
  }
}

.sidebar-footer {
  margin-top: auto;
}

.mini-profile {
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: scale(1.05);
  }
}

.profile-avatar {
  border: 2px solid white;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0 32px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.welcome-text {
  font-size: 28px;
  margin: 0 0 4px;
  color: var(--text-primary);
}

.welcome-sub {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 500;
}

.page-scroll-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  border-radius: var(--border-radius-xl);

  &::-webkit-scrollbar {
    width: 0px;
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@media (max-width: 768px) {
  .app-wrapper {
    padding: 16px;
    gap: 16px;
  }

  .floating-sidebar {
    width: 60px;
  }

  :deep(.menu-pill) {
    width: 40px !important;
    height: 40px !important;
  }
}
</style>
