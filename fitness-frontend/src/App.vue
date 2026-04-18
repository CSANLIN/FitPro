<script setup>
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

// 初始化认证状态（例如检查token有效性等）
const authStore = useAuthStore()

// 应用启动时初始化认证状态
onMounted(() => {
  authStore.init()
})
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
