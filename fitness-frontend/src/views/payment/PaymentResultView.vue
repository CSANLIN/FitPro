<template>
  <div class="result-page">
    <div class="result-card">
      <div class="result-icon" :class="isSuccess ? 'success' : 'failed'">
        <el-icon :size="64" v-if="isSuccess"><CircleCheckFilled /></el-icon>
        <el-icon :size="64" v-else><CircleCloseFilled /></el-icon>
      </div>

      <h2>{{ isSuccess ? '支付成功' : '支付失败' }}</h2>
      <p class="result-desc">
        {{ isSuccess ? '课程预约已确认，记得按时参加哦！' : '支付未完成，请重新尝试' }}
      </p>

      <div v-if="isSuccess && amount" class="amount-info">
        支付金额：<span class="amount">¥{{ amount }}</span>
      </div>

      <div class="result-actions">
        <el-button type="primary" size="large" @click="goToSchedule">
          查看课程
        </el-button>
        <el-button size="large" @click="goToApp">
          返回首页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isSuccess = computed(() => route.query.status === 'success')
const amount = computed(() => route.query.amount || '')

const goToSchedule = () => {
  router.push('/app/course/booking')
}

const goToApp = () => {
  router.push('/app/home')
}
</script>

<style scoped lang="scss">
.result-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-base);
  padding: 24px;
}

.result-card {
  width: 420px;
  max-width: 100%;
  background: white;
  border-radius: 16px;
  padding: 48px 40px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0,0,0,0.06);
}

.result-icon {
  margin-bottom: 24px;

  &.success {
    color: #67c23a;
  }

  &.failed {
    color: #f56c6c;
  }
}

h2 {
  font-size: 24px;
  margin: 0 0 8px;
}

.result-desc {
  color: var(--text-secondary);
  margin: 0 0 16px;
}

.amount-info {
  font-size: 16px;
  margin-bottom: 32px;
  color: var(--text-secondary);

  .amount {
    font-size: 20px;
    font-weight: 700;
    color: var(--primary-color);
  }
}

.result-actions {
  display: flex;
  gap: 16px;
  justify-content: center;

  .el-button {
    min-width: 120px;
  }
}
</style>
