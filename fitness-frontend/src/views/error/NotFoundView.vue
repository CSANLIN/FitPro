<template>
  <div class="not-found-view">
    <h1>404 - 页面不存在</h1>
    <p>抱歉，您访问的页面不存在。</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup>
// 404页面组件
</script>

<style scoped>
.not-found-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}
</style>