<template>
  <div class="membership-view">
    <h2 class="page-title">我的会籍</h2>

    <!-- 加载状态 -->
    <div v-if="loading" v-loading="loading" style="min-height: 200px"></div>

    <template v-else>
      <!-- 活跃会籍卡片 -->
      <div v-if="activeMembership" class="active-card-wrapper">
        <div class="membership-card" :class="cardTheme(activeMembership.cardType)">
          <div class="card-header">
            <span class="card-name">{{ activeMembership.cardName }}</span>
            <el-tag size="small" :type="statusType(activeMembership.status)" effect="dark">
              {{ statusLabel(activeMembership.status) }}
            </el-tag>
          </div>
          <div class="card-body">
            <div class="card-stat" v-if="activeMembership.cardType !== 'TIMES'">
              <span class="stat-value">{{ activeMembership.remainingDays ?? '--' }}</span>
              <span class="stat-label">剩余天数</span>
            </div>
            <div class="card-stat" v-if="activeMembership.cardType === 'TIMES'">
              <span class="stat-value">{{ activeMembership.remainingTimes ?? '--' }}</span>
              <span class="stat-label">剩余次数</span>
            </div>
            <div class="card-stat">
              <span class="stat-value">{{ activeMembership.cardPrice }}元</span>
              <span class="stat-label">卡种价格</span>
            </div>
          </div>
          <div class="card-footer">
            <span>开始: {{ formatDate(activeMembership.startDate) }}</span>
            <span v-if="activeMembership.endDate">到期: {{ formatDate(activeMembership.endDate) }}</span>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无活跃会籍" class="no-membership" />

      <!-- 历史会籍 -->
      <el-card class="history-card" v-if="historyMemberships.length > 0">
        <template #header>
          <span>历史会籍</span>
        </template>
        <el-table :data="historyMemberships" stripe size="small" style="width: 100%">
          <el-table-column prop="cardName" label="卡种" width="100" />
          <el-table-column prop="cardType" label="类型" width="70" align="center">
            <template #default="{ row }">
              {{ typeLabel(row.cardType) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)" size="small">
                {{ statusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="有效期" min-width="180">
            <template #default="{ row }">
              {{ formatDate(row.startDate) }} ~ {{ row.endDate ? formatDate(row.endDate) : '不限' }}
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { membershipApi } from '@/api/membership'

const loading = ref(false)
const memberships = ref([])

const activeMembership = computed(() =>
  memberships.value.find(m => m.status === 'ACTIVE' || m.status === 'FROZEN') || null
)

const historyMemberships = computed(() =>
  memberships.value.filter(m => m.status !== 'ACTIVE' && m.status !== 'FROZEN')
)

const typeLabel = (val) => ({
  MONTH: '月卡', QUARTER: '季卡', YEAR: '年卡', TIMES: '次卡'
}[val] || val)

const statusLabel = (val) => ({
  ACTIVE: '活跃', FROZEN: '冻结', EXPIRED: '已过期', CANCELLED: '已取消'
}[val] || val)

const statusType = (val) => ({
  ACTIVE: 'success', FROZEN: 'warning', EXPIRED: 'info', CANCELLED: 'danger'
}[val] || 'info')

const cardTheme = (type) => {
  const themes = {
    MONTH: 'theme-blue',
    QUARTER: 'theme-green',
    YEAR: 'theme-purple',
    TIMES: 'theme-orange'
  }
  return themes[type] || 'theme-blue'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '--'
  return dateStr.substring(0, 10)
}

const fetchMemberships = async () => {
  loading.value = true
  try {
    memberships.value = await membershipApi.myMemberships()
  } catch (e) {
    console.error('获取会籍信息失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchMemberships()
})
</script>

<style scoped>
.membership-view {
  max-width: 900px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px;
}

.active-card-wrapper {
  margin-bottom: 24px;
}

.membership-card {
  border-radius: 16px;
  padding: 24px;
  color: white;
  position: relative;
  overflow: hidden;
}

.membership-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.theme-blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.theme-green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.theme-purple { background: linear-gradient(135deg, #7F00FF 0%, #E100FF 100%); }
.theme-orange { background: linear-gradient(135deg, #f12711 0%, #f5af19 100%); }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-name {
  font-size: 18px;
  font-weight: 600;
}

.card-body {
  display: flex;
  gap: 40px;
  margin-bottom: 16px;
}

.card-stat {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 4px;
}

.card-footer {
  display: flex;
  gap: 24px;
  font-size: 12px;
  opacity: 0.85;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 12px;
}

.no-membership {
  margin-bottom: 24px;
}

.history-card {
  border-radius: 12px;
}
</style>
