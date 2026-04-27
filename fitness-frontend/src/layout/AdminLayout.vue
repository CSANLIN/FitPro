<template>
  <div class="admin-layout">
    <!-- 主体区域：增加整体内边距，让内部元素漂浮 -->
    <div class="app-wrapper">
      
      <!-- 悬浮侧边栏 (Floating Pill Sidebar) -->
      <aside class="floating-sidebar" :class="{ 'is-collapsed': isCollapsed }">
        <div class="logo-area">
          <div class="logo-icon">
            <!-- 极简 Logo 标志 (哑铃) -->
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
          <!-- 通过 Tooltip 或手写 Tooltip 替代文字，只显示图标 -->
          <el-tooltip content="仪表盘" placement="right" :offset="20">
            <el-menu-item index="/admin/dashboard" class="menu-pill">
              <el-icon><Odometer /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="会员管理" placement="right" :offset="20">
            <el-menu-item index="/admin/member/list" class="menu-pill">
              <el-icon><User /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="教练管理" placement="right" :offset="20">
            <el-menu-item index="/admin/coach/list" class="menu-pill">
              <el-icon><UserFilled /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="课程中心" placement="right" :offset="20">
            <el-menu-item index="/admin/course/list" class="menu-pill">
              <el-icon><Notebook /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="动作图鉴" placement="right" :offset="20">
            <el-menu-item index="/admin/exercise/list" class="menu-pill">
              <el-icon><Basketball /></el-icon>
            </el-menu-item>
          </el-tooltip>

          <el-tooltip content="会籍管理" placement="right" :offset="20">
            <el-menu-item index="/admin/membership" class="menu-pill">
              <el-icon><Ticket /></el-icon>
            </el-menu-item>
          </el-tooltip>
          
          <el-tooltip content="系统设置" placement="right" :offset="20">
            <el-menu-item index="/admin/system/config" class="menu-pill">
              <el-icon><Setting /></el-icon>
            </el-menu-item>
          </el-tooltip>
        </el-menu>
        
        <!-- 底部用户头像 -->
        <div class="sidebar-footer">
          <el-dropdown trigger="click" placement="right-end">
            <div class="mini-profile">
              <el-avatar :size="36" :src="userAvatar" class="profile-avatar" />
            </div>
            <template #dropdown>
              <el-dropdown-menu class="premium-dropdown">
                <el-dropdown-item @click="goToProfile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
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
        <!-- 顶部功能区 (隐藏的面包屑，替换为搜索或问候语) -->
        <header class="top-header">
          <div class="header-left">
            <h2 class="welcome-text">你好，{{ userName }}！</h2>
            <p class="welcome-sub">准备好今天的高效健身房管理了吗？</p>
          </div>
          
          <div class="header-right">
            <!-- 悬浮胶囊搜索框 -->
            <div class="search-pill hidden-mobile">
              <el-input placeholder="搜索会员、课程或教练..." prefix-icon="Search" />
            </div>
            
            <el-button class="btn-dark upgrade-btn">
              工作台
            </el-button>
          </div>
        </header>

        <!-- 页面内容主体 -->
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
  Odometer, User, UserFilled, Calendar, Basketball, List, Ticket, Setting,
  Expand, Fold, ArrowDown, SwitchButton, FullScreen, Bell, Notebook, Search
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(true) // Be.run 风格侧边栏通常只显图标
const activeMenu = computed(() => {
  // 简化 active 状态
  const path = route.path
  if (path.includes('/member')) return '/admin/member/list'
  if (path.includes('/coach')) return '/admin/coach/list'
  if (path.includes('/course')) return '/admin/course/list'
  if (path.includes('/exercise')) return '/admin/exercise/list'
  if (path.includes('/system')) return '/admin/system/config'
  return path
})

const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const userName = computed(() => authStore.userInfo?.nickname || 'Admin')

const goToProfile = () => router.push('/admin/profile')
const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.admin-layout {
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

/* 悬浮侧边栏 (胶囊风格) */
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
  
  /* 去除 element plus 默认的 active 样式 */
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

/* 右侧主体内容 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

/* 顶部导航 */
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

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-pill {
  width: 300px;
}

.upgrade-btn {
  padding: 0 24px !important;
}

/* 页面内容区 */
.page-scroll-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  border-radius: var(--border-radius-xl);
  
  /* 隐藏滚动条但保留功能 */
  &::-webkit-scrollbar {
    width: 0px;
  }
}

/* 页面切换动画 */
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
  
  .hidden-mobile {
    display: none !important;
  }
}
</style>