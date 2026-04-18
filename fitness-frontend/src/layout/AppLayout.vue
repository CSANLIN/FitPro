<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-text">FitPro</span>
          <span class="logo-subtitle">会员端</span>
        </div>
        <div class="user-info" @click="goToProfile">
          <el-avatar :size="36" :src="userAvatar" />
        </div>
      </div>
    </header>

    <!-- 内容区域 -->
    <main class="app-main">
      <div class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>

    <!-- 底部导航栏 -->
    <nav class="app-tabbar">
      <router-link
        v-for="item in tabItems"
        :key="item.path"
        :to="item.path"
        class="tab-item"
        :class="{ active: isActive(item) }"
      >
        <el-icon :size="22">
          <component :is="item.icon" />
        </el-icon>
        <span class="tab-label">{{ item.label }}</span>
      </router-link>
    </nav>
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

// 底部 Tab 项配置
const tabItems = ref([
  { path: '/app/dashboard', label: '首页', icon: HomeFilled },
  { path: '/app/course', label: '课程', icon: Calendar },
  { path: '/app/workout', label: '训练', icon: Basketball },
  { path: '/app/profile', label: '我的', icon: User }
])

// 用户信息（模拟数据，后续从store获取）
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

// 判断当前激活的 Tab
const isActive = (item) => {
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
  background-color: var(--bg-base);
}

/* 顶部导航栏 */
.app-header {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  padding: 0 16px;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
}

.logo-subtitle {
  font-size: 12px;
  opacity: 0.9;
}

.user-info {
  cursor: pointer;
  transition: transform 0.2s;
}

.user-info:hover {
  transform: scale(1.05);
}

/* 内容区域 */
.app-main {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 60px; /* 为底部 TabBar 留出空间 */
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}

/* 底部导航栏 */
.app-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: white;
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
  text-decoration: none;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.tab-item:hover {
  background-color: rgba(64, 158, 255, 0.05);
}

.tab-item.active {
  color: var(--primary-color);
}

.tab-item.active .el-icon {
  color: var(--primary-color);
}

.tab-label {
  font-size: 12px;
  margin-top: 4px;
  font-weight: 500;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .page-content {
    padding: 12px;
  }

  .logo-text {
    font-size: 16px;
  }

  .logo-subtitle {
    font-size: 11px;
  }

  .app-header {
    padding: 0 12px;
  }
}
</style>