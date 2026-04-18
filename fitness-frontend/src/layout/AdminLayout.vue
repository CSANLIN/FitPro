<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <el-icon class="collapse-icon" @click="toggleCollapse">
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
        <div class="logo">
          <span class="logo-text">FitPro</span>
          <span class="logo-subtitle">管理后台</span>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown>
          <div class="user-info">
            <el-avatar :size="32" :src="userAvatar" />
            <span class="user-name">{{ userName }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToProfile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主体区域 -->
    <el-container class="main-container">
      <!-- 左侧菜单 -->
      <el-aside :width="asideWidth" class="aside">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :collapse-transition="false"
          router
          class="side-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Dashboard /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>

          <el-sub-menu index="member">
            <template #title>
              <el-icon><User /></el-icon>
              <span>会员管理</span>
            </template>
            <el-menu-item index="/admin/member/list">会员列表</el-menu-item>
            <el-menu-item index="/admin/member/add">新增会员</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="coach">
            <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>教练管理</span>
            </template>
            <el-menu-item index="/admin/coach/list">教练列表</el-menu-item>
            <el-menu-item index="/admin/coach/add">新增教练</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="course">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>课程管理</span>
            </template>
            <el-menu-item index="/admin/course/list">课程列表</el-menu-item>
            <el-menu-item index="/admin/course/schedule">排课管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="exercise">
            <template #title>
              <el-icon><Basketball /></el-icon>
              <span>运动库管理</span>
            </template>
            <el-menu-item index="/admin/exercise/category">分类管理</el-menu-item>
            <el-menu-item index="/admin/exercise/list">动作管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/system/announcement">公告管理</el-menu-item>
            <el-menu-item index="/admin/system/log">操作日志</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区 -->
      <el-main class="main-content">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumb" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 页面内容 -->
        <div class="page-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Dashboard,
  User,
  UserFilled,
  Calendar,
  Basketball,
  Setting,
  Expand,
  Fold,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 状态
const isCollapsed = ref(false)

// 计算属性
const asideWidth = computed(() => isCollapsed.value ? '64px' : '240px')
const activeMenu = computed(() => route.path)

// 用户信息（模拟数据，后续从store获取）
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const userName = computed(() => authStore.userInfo?.nickname || '管理员')

// 面包屑（简化版本，后续可根据路由元信息生成）
const breadcrumb = computed(() => {
  const pathArr = route.path.split('/').filter(Boolean)
  const breadcrumbItems = []

  // 移除admin前缀
  if (pathArr[0] === 'admin') {
    pathArr.shift()
  }

  // 生成面包屑（简化逻辑）
  pathArr.forEach((path, index) => {
    const fullPath = '/admin/' + pathArr.slice(0, index + 1).join('/')
    breadcrumbItems.push({
      path: fullPath,
      title: path === 'dashboard' ? '仪表盘' :
             path === 'member' ? '会员管理' :
             path === 'course' ? '课程管理' :
             path === 'exercise' ? '运动库管理' :
             path === 'system' ? '系统管理' : path
    })
  })

  return breadcrumbItems
})

// 方法
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const goToProfile = () => {
  // 根据角色跳转到不同个人中心
  const role = authStore.userInfo?.role
  if (role === 'ROLE_SUPER_ADMIN' || role === 'ROLE_COACH') {
    router.push('/admin/profile')
  } else {
    router.push('/app/profile')
  }
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

// 监听路由变化，更新面包屑等
watch(() => route.path, () => {
  // 可以在这里添加页面访问统计等
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 20px;
  box-shadow: var(--shadow-base);
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.collapse-icon:hover {
  transform: scale(1.1);
}

.logo {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
}

.logo-subtitle {
  font-size: 12px;
  opacity: 0.8;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--border-radius-base);
  transition: background-color 0.2s;
}

.header-right .user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-name {
  font-weight: 500;
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.aside {
  background-color: white;
  box-shadow: var(--shadow-sm);
  border-right: 1px solid var(--border-light);
  transition: width 0.3s;
}

.side-menu {
  border-right: none;
  height: 100%;
}

.side-menu:not(.el-menu--collapse) {
  width: 240px;
}

.main-content {
  display: flex;
  flex-direction: column;
  padding: 0;
  background-color: var(--bg-base);
  overflow: auto;
}

.breadcrumb {
  padding: 16px 20px;
  background-color: white;
  border-bottom: 1px solid var(--border-light);
}

.page-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
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
</style>