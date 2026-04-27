<template>
  <div class="app-layout">
    <!-- 顶部导航栏 (Glassmorphism) -->
    <header class="app-header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-text">FitPro</span>
          <span class="logo-subtitle">Premium</span>
        </div>
        <div class="user-info" @click="goToProfile">
          <el-avatar :size="38" :src="userAvatar" class="avatar-glow">
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

    <!-- 底部导航栏 (Floating & Glassmorphism) -->
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
            <div class="active-dot" v-if="isActive(item)"></div>
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
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 底部 Tab 项配置 (精简为 4 个主要功能，符合现代移动端设计)
const tabItems = ref([
  { path: '/app/home', label: '主页', icon: HomeFilled },
  { path: '/app/course', label: '课程', icon: Calendar },
  { path: '/app/exercise', label: '探索', icon: Basketball },
  { path: '/app/profile', label: '我的', icon: User }
])

const userInfo = computed(() => authStore.userInfo || {})

const userAvatar = computed(() => {
  return userInfo.value?.avatar || ''
})

// 判断当前激活的 Tab
const isActive = (item) => {
  if (item.path === '/app/home' && route.path === '/app/home') return true;
  if (item.path === '/app/course' && route.path.startsWith('/app/course')) return true;
  if (item.path === '/app/exercise' && (route.path.startsWith('/app/exercise') || route.path.startsWith('/app/plan'))) return true;
  if (item.path === '/app/profile' && (route.path.startsWith('/app/profile') || route.path.startsWith('/app/body-data') || route.path.startsWith('/app/membership') || route.path.startsWith('/app/record') || route.path.startsWith('/app/checkin'))) return true;
  return route.path.startsWith(item.path)
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/app/profile')
}
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8fafc; /* 更明亮干净的背景色 */
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
}

/* 顶部导航栏 - 毛玻璃效果 */
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 0 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--primary-color) 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.logo-subtitle {
  font-size: 11px;
  font-weight: 600;
  color: #8b5cf6;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 2px 6px;
  background: rgba(139, 92, 246, 0.1);
  border-radius: 4px;
}

.user-info {
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.user-info:hover {
  transform: scale(1.1);
}

.avatar-glow {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  border: 2px solid white;
}

/* 内容区域 */
.app-main {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-bottom: 90px; /* 增加底部留白，适应悬浮 TabBar */
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  min-height: 100%;
}

/* 底部导航栏 - 悬浮毛玻璃设计 */
.app-tabbar-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0 16px 20px;
  pointer-events: none; /* 让外层不拦截点击 */
  z-index: 100;
  display: flex;
  justify-content: center;
}

.app-tabbar {
  pointer-events: auto; /* 恢复内层点击 */
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  max-width: 500px;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08), 0 0 1px rgba(0, 0, 0, 0.1);
  padding: 0 10px;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
  text-decoration: none;
  color: #94a3b8;
  transition: all 0.3s ease;
  position: relative;
}

.icon-wrapper {
  position: relative;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.tab-item:hover {
  color: #64748b;
}

.tab-item:active .icon-wrapper {
  transform: scale(0.9);
}

.tab-item.active {
  color: var(--primary-color);
}

.tab-item.active .icon-wrapper {
  transform: translateY(-4px);
}

.tab-item.active .el-icon {
  color: var(--primary-color);
  filter: drop-shadow(0 4px 6px rgba(64, 158, 255, 0.3));
}

.active-dot {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 4px;
  background-color: var(--primary-color);
  border-radius: 50%;
  box-shadow: 0 0 8px var(--primary-color);
}

.tab-label {
  font-size: 11px;
  font-weight: 600;
  margin-top: 4px;
  opacity: 0;
  transform: translateY(4px);
  transition: all 0.3s ease;
  position: absolute;
  bottom: 6px;
}

.tab-item.active .tab-label {
  opacity: 1;
  transform: translateY(0);
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

/* 移动端适配 */
@media (max-width: 768px) {
  .page-content {
    padding: 12px;
  }

  .app-tabbar-wrapper {
    padding: 0 12px 16px;
  }
}
</style>