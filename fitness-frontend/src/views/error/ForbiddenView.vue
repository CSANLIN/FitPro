<template>
  <div class="forbidden-view">
    <h1>403 - 无权限访问</h1>
    <p>抱歉，您没有权限访问此页面。</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup>
// 403页面组件
</script>

<style scoped>
.forbidden-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}
</style>