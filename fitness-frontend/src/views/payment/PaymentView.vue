<template>
  <div class="payment-page">
    <div class="payment-card">
      <div class="payment-header">
        <h2>确认支付</h2>
        <p>请确认以下课程信息</p>
      </div>

      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="4" animated />
      </div>

      <template v-else-if="orderInfo">
        <div class="course-summary">
          <div class="info-row">
            <span class="label">课程</span>
            <span class="value">{{ orderInfo.courseName }}</span>
          </div>
          <div class="info-row">
            <span class="label">日期</span>
            <span class="value">{{ orderInfo.scheduleDate }} {{ orderInfo.startTime }} - {{ orderInfo.endTime }}</span>
          </div>
          <div class="info-row">
            <span class="label">教练</span>
            <span class="value">{{ orderInfo.coachName }}</span>
          </div>
          <div class="divider"></div>
          <div class="info-row amount-row">
            <span class="label">支付金额</span>
            <span class="amount">¥{{ orderInfo.amount }}</span>
          </div>
        </div>

        <div class="payment-actions">
          <el-button size="large" @click="goBack">取消</el-button>
          <el-button type="primary" size="large" :loading="paying" @click="handlePay">
            确认支付 ¥{{ orderInfo.amount }}
          </el-button>
        </div>
      </template>

      <div v-else class="empty-state">
        <p>未找到订单信息</p>
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { paymentApi } from '@/api/payment'
import { courseApi } from '@/api/course'

const route = useRoute()
const router = useRouter()

const orderNo = route.params.orderNo
const loading = ref(false)
const paying = ref(false)
const orderInfo = ref(null)

onMounted(async () => {
  if (!orderNo) {
    ElMessage.error('订单号不存在')
    return
  }

  loading.value = true
  try {
    // 从路由查询参数获取订单信息
    orderInfo.value = {
      orderNo: orderNo,
      amount: route.query.amount || '0.00',
      courseName: route.query.courseName || '课程',
      scheduleDate: route.query.scheduleDate || '',
      startTime: route.query.startTime || '',
      endTime: route.query.endTime || '',
      coachName: route.query.coachName || '',
      scheduleId: route.query.scheduleId || null
    }
  } catch (e) {
    console.error('获取订单信息失败:', e)
  } finally {
    loading.value = false
  }
})

const handlePay = async () => {
  paying.value = true
  try {
    // 1. 模拟支付
    await paymentApi.mockPay(orderNo)
    ElMessage.success('支付成功！')

    // 2. 支付成功，调用预约接口
    if (orderInfo.value.scheduleId) {
      try {
        await courseApi.book(orderInfo.value.scheduleId)
        // 3. 跳转到支付结果页
        router.replace(`/payment/result/${orderNo}?status=success&amount=${orderInfo.value.amount}`)
      } catch (bookErr) {
        ElMessage.error(bookErr.response?.data?.message || '预约失败')
        router.replace(`/payment/result/${orderNo}?status=failed&amount=${orderInfo.value.amount}`)
      }
    } else {
      router.replace(`/payment/result/${orderNo}?status=success&amount=${orderInfo.value.amount}`)
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '支付失败，请重试')
    paying.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.payment-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-base);
  padding: 24px;
}

.payment-card {
  width: 440px;
  max-width: 100%;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.06);
}

.payment-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    margin: 0 0 8px;
    font-size: 24px;
  }

  p {
    margin: 0;
    color: var(--text-secondary);
  }
}

.course-summary {
  margin-bottom: 32px;

  .info-row {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;

    .label {
      color: var(--text-secondary);
    }

    .value {
      font-weight: 500;
    }
  }

  .divider {
    height: 1px;
    background: var(--border-color);
    margin: 8px 0;
  }

  .amount-row {
    .amount {
      font-size: 24px;
      font-weight: 700;
      color: var(--primary-color);
    }
  }
}

.payment-actions {
  display: flex;
  gap: 16px;

  .el-button {
    flex: 1;
  }
}

.loading-state, .empty-state {
  padding: 40px 0;
  text-align: center;
}
</style>
