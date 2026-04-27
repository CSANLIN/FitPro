<template>
  <div class="membership-view">
    <div class="page-header">
      <h2 class="page-title">会籍中心</h2>
      <p class="page-subtitle">尊享特权，畅想无尽运动乐趣</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" v-loading="loading" style="min-height: 200px"></div>

    <template v-else>
      <!-- 活跃会籍卡片 (Premium VIP Card) -->
      <div v-if="activeMembership" class="active-card-wrapper">
        <div class="vip-card premium-shadow" :class="cardTheme(activeMembership.cardType)">
          <!-- 卡片背景装饰 -->
          <div class="card-deco deco-1"></div>
          <div class="card-deco deco-2"></div>
          
          <div class="card-top">
            <div class="brand">FitPro VIP</div>
            <el-tag size="small" :type="statusType(activeMembership.status)" effect="dark" class="status-tag" round>
              {{ statusLabel(activeMembership.status) }}
            </el-tag>
          </div>
          
          <div class="card-middle">
            <h3 class="card-name">{{ activeMembership.cardName }}</h3>
            <div class="card-price">¥{{ activeMembership.cardPrice }}</div>
          </div>
          
          <div class="card-bottom">
            <div class="stat-group">
              <div class="stat-item" v-if="activeMembership.cardType !== 'TIMES'">
                <span class="label">剩余天数</span>
                <span class="value">{{ activeMembership.remainingDays ?? '--' }}</span>
              </div>
              <div class="stat-item" v-if="activeMembership.cardType === 'TIMES'">
                <span class="label">剩余次数</span>
                <span class="value">{{ activeMembership.remainingTimes ?? '--' }}</span>
              </div>
              <div class="stat-item">
                <span class="label">卡种类型</span>
                <span class="value">{{ typeLabel(activeMembership.cardType) }}</span>
              </div>
            </div>
            
            <div class="validity">
              {{ formatDate(activeMembership.startDate) }} 
              <span v-if="activeMembership.endDate">至 {{ formatDate(activeMembership.endDate) }}</span>
              <span v-else>至 无限期</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="您当前没有活跃的会籍卡" class="no-membership">
        <el-button color="#10b981" round @click="goMembershipManage" size="large">立即开通会籍</el-button>
      </el-empty>

      <!-- 历史会籍 -->
      <div class="history-section" v-if="historyMemberships.length > 0">
        <h3 class="section-title">历史会籍</h3>
        <div class="history-list">
          <div v-for="item in historyMemberships" :key="item.id" class="history-item">
            <div class="history-icon" :class="cardTheme(item.cardType)">
              <el-icon><Ticket /></el-icon>
            </div>
            <div class="history-info">
              <div class="history-name">{{ item.cardName }}</div>
              <div class="history-meta">
                {{ formatDate(item.startDate) }} ~ {{ item.endDate ? formatDate(item.endDate) : '不限' }}
              </div>
            </div>
            <div class="history-status">
              <el-tag :type="statusType(item.status)" size="small" round>
                {{ statusLabel(item.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { membershipApi } from '@/api/membership'
import { ElMessage } from 'element-plus'
import { Ticket } from '@element-plus/icons-vue'

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
  ACTIVE: '活跃', FROZEN: '已冻结', EXPIRED: '已过期', CANCELLED: '已取消'
}[val] || val)

const statusType = (val) => ({
  ACTIVE: 'success', FROZEN: 'warning', EXPIRED: 'info', CANCELLED: 'danger'
}[val] || 'info')

const cardTheme = (type) => {
  const themes = {
    MONTH: 'theme-blue',
    QUARTER: 'theme-green',
    YEAR: 'theme-black',
    TIMES: 'theme-orange'
  }
  return themes[type] || 'theme-blue'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '--'
  return dateStr.substring(0, 10).replace(/-/g, '/')
}

const goMembershipManage = () => {
  ElMessage.info('请前往健身房前台咨询办理会籍')
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
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.page-header {
  margin-top: 8px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  margin: 0 0 4px;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* VIP 卡片设计 */
.active-card-wrapper {
  margin-bottom: 32px;
  perspective: 1000px;
}

.vip-card {
  position: relative;
  border-radius: 24px;
  padding: 32px;
  color: white;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  transition: transform 0.5s ease;
  min-height: 220px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.vip-card:hover {
  transform: translateY(-8px) rotateX(2deg);
}

.theme-blue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); box-shadow: 0 20px 40px rgba(79, 172, 254, 0.3); }
.theme-green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); box-shadow: 0 20px 40px rgba(56, 239, 125, 0.3); }
.theme-black { background: linear-gradient(135deg, #232526 0%, #414345 100%); box-shadow: 0 20px 40px rgba(35, 37, 38, 0.3); color: #e2e8f0;}
.theme-orange { background: linear-gradient(135deg, #f12711 0%, #f5af19 100%); box-shadow: 0 20px 40px rgba(245, 175, 25, 0.3); }

/* 装饰背景 */
.card-deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.deco-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -50px;
}

.deco-2 {
  width: 150px;
  height: 150px;
  bottom: -50px;
  left: -20px;
}

/* 卡片内容 */
.card-top, .card-middle, .card-bottom {
  position: relative;
  z-index: 1;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 2px;
  text-transform: uppercase;
  opacity: 0.9;
}

.status-tag {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  backdrop-filter: blur(4px);
}

.card-middle {
  margin: 24px 0;
}

.card-name {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 8px;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.card-price {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 600;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.stat-group {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-item .label {
  font-size: 11px;
  opacity: 0.7;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 4px;
}

.stat-item .value {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
}

.validity {
  font-size: 12px;
  opacity: 0.8;
  font-weight: 500;
  letter-spacing: 1px;
}

.no-membership {
  background: white;
  border-radius: 20px;
  padding: 40px 0;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

/* 历史列表 */
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: center;
  background: white;
  padding: 16px 20px;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02);
  transition: transform 0.2s;
}

.history-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
}

.history-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 16px;
}

.history-info {
  flex: 1;
}

.history-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.history-meta {
  font-size: 13px;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .card-bottom {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .stat-group {
    gap: 20px;
  }
}
</style>
