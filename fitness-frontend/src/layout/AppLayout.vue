<template>
  <div class="app-layout">
    <!-- 顶部导航栏 (Glassmorphism & Bubble Aesthetic) -->
    <header class="app-header">
      <div class="header-content">
        <div class="logo">
          <!-- 小圆形深色 logo (哑铃) -->
          <div class="logo-circle">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <g transform="rotate(-45 12 12)">
                <rect x="6" y="10" width="3" height="4" rx="1" fill="#ffcc2e" />
                <rect x="15" y="10" width="3" height="4" rx="1" fill="#ffcc2e" />
                <rect x="9" y="11" width="6" height="2" fill="#ffcc2e" />
                <rect x="4" y="8" width="2" height="8" rx="1" fill="#ffcc2e" />
                <rect x="18" y="8" width="2" height="8" rx="1" fill="#ffcc2e" />
              </g>
            </svg>
          </div>
          <span class="logo-text">FitPro 健身</span>
        </div>
        <div class="user-info" @click="goToProfile">
          <el-avatar :size="40" :src="userAvatar" class="avatar-glow">
            {{ userInfo?.nickname?.charAt(0) || 'U' }}
          </el-avatar>
        </div>
      </div>
    </header>

    <!-- 内容区域 -->
    <main class="app-main">
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>

    <!-- 底部导航栏 (Floating Pill Tabbar) -->
    <div class="app-tabbar-wrapper">
      <nav class="app-tabbar">
        <router-link
          v-for="item in tabItems"
          :key="item.path"
          :to="item.path"
          class="tab-item"
          :class="{ active: isActive(item) }"
        >
          <div class="icon-wrapper">
            <el-icon :size="24">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <span class="tab-label">{{ item.label }}</span>
        </router-link>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  HomeFilled,
  Calendar,
  Basketball,
  MagicStick,
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const tabItems = ref([
  { path: '/app/home', label: '首页', icon: HomeFilled },
  { path: '/app/course', label: '课程', icon: Calendar },
  { path: '/app/exercise', label: '运动', icon: Basketball },
  { path: '/app/ai', label: '助手', icon: MagicStick },
  { path: '/app/profile', label: '我的', icon: User }
])

const userInfo = computed(() => authStore.userInfo || {})

const userAvatar = computed(() => {
  return userInfo.value?.avatar || ''
})

const isActive = (item) => {
  if (item.path === '/app/home' && route.path === '/app/home') return true;
  if (item.path === '/app/course' && route.path.startsWith('/app/course')) return true;
  if (item.path === '/app/exercise' && (route.path.startsWith('/app/exercise') || route.path.startsWith('/app/plan'))) return true;
  if (item.path === '/app/ai' && route.path.startsWith('/app/ai')) return true;
  if (item.path === '/app/profile' && (route.path.startsWith('/app/profile') || route.path.startsWith('/app/body-data') || route.path.startsWith('/app/membership') || route.path.startsWith('/app/record') || route.path.startsWith('/app/checkin'))) return true;
  return route.path.startsWith(item.path)
}

const goToProfile = () => {
  router.push('/app/profile')
}
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-base); /* Be.run 米灰色背景 */
}

/* 顶部导航栏 - 毛玻璃效果 */
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(235, 231, 223, 0.6);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 12px 20px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-circle {
  width: 32px;
  height: 32px;
  background-color: var(--accent-dark);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.5px;
}

.user-info {
  cursor: pointer;
}

.avatar-glow {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid white;
}

/* 内容区域 */
.app-main {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-bottom: 100px; /* 给底部漂浮栏留出空间 */
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  min-height: 100%;
}

/* 底部导航栏 - 悬浮胶囊设计 */
.app-tabbar-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0 16px 24px;
  pointer-events: none;
  z-index: 100;
  display: flex;
  justify-content: center;
}

.app-tabbar {
  pointer-events: auto;
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  max-width: 500px;
  height: 72px;
  background: white;
  border-radius: 36px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  padding: 0 12px;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
  text-decoration: none;
  color: #8c8c93;
  transition: all 0.3s ease;
  position: relative;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-item:active .icon-wrapper {
  transform: scale(0.9);
}

.tab-item.active {
  color: var(--text-primary);
}

.tab-item.active .icon-wrapper {
  background-color: var(--accent-dark);
  color: var(--primary-color);
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(36, 37, 41, 0.2);
}

.tab-label {
  display: none; /* Be.run 风格通常隐藏标签，只靠图标和高亮 */
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(15px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}
</style>