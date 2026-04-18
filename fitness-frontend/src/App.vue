<script setup>
import { RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 初始化认证状态（例如检查token有效性等）
const authStore = useAuthStore()

// 应用启动时可以做的一些初始化操作
// 例如：检查token是否过期，自动刷新token等
</script>

<template>
  <!-- 路由出口 -->
  <RouterView />
</template>

<style>
#app {
  min-height: 100vh;
}
</style>
