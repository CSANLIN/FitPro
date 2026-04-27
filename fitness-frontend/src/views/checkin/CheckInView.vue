<template>
  <div class="checkin-view">
    <div class="page-header">
      <h2 class="page-title">打卡中心</h2>
      <p class="page-subtitle">坚持的力量，时间看得见</p>
    </div>

    <!-- 签到统计 (Premium Cards) -->
    <div class="stats-overview">
      <div class="stat-card glass-card">
        <div class="stat-icon"><el-icon><Calendar /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.monthCount }}<small>天</small></span>
          <span class="stat-label">本月已签到</span>
        </div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-icon warning"><el-icon><Trophy /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.streakDays }}<small>天</small></span>
          <span class="stat-label">当前连续签到</span>
        </div>
      </div>
    </div>

    <div class="main-content-grid">
      <!-- 左侧：签到按钮区域 -->
      <div class="checkin-action-area">
        <div class="checkin-btn-wrap">
          <!-- 动态发光按钮 -->
          <div 
            class="glow-button"
            :class="{ 'is-checked': checkedIn, 'is-loading': checkingIn }"
            @click="!checkedIn && !checkingIn ? handleCheckIn() : null"
          >
            <div class="glow-bg"></div>
            <div class="btn-content">
              <el-icon v-if="checkedIn" :size="40"><Select /></el-icon>
              <el-icon v-else-if="checkingIn" :size="40" class="is-loading-icon"><Loading /></el-icon>
              <el-icon v-else :size="40"><Pointer /></el-icon>
              
              <span class="btn-text">{{ checkedIn ? '今日已签到' : '点击签到' }}</span>
            </div>
          </div>
          
          <div class="hint-text-box">
            <p class="checkin-hint" v-if="!checkedIn">签到消耗会籍次数，次卡每次签到扣1次</p>
            <p class="checkin-hint success" v-else>继续保持，自律带来自由！</p>
          </div>
        </div>
        
        <!-- 签到时间线 -->
        <div class="timeline-section" v-loading="loading">
          <h3 class="section-title">最近签到</h3>
          <el-empty v-if="!loading && records.length === 0" description="暂无签到记录" :image-size="80" />
          <el-timeline v-else class="custom-timeline">
            <el-timeline-item
              v-for="item in records.slice(0, 5)"
              :key="item.id"
              :type="item.checkInType === 'MANUAL' ? 'primary' : 'success'"
              :hollow="true"
            >
              <div class="timeline-content">
                <span class="time-text">{{ formatDateTime(item.checkInTime) }}</span>
                <el-tag size="small" :type="item.checkInType === 'MANUAL' ? 'info' : 'success'" round effect="plain">
                  {{ item.checkInType === 'MANUAL' ? '手动打卡' : '扫码签到' }}
                </el-tag>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <!-- 右侧：签到日历 -->
      <div class="calendar-area">
        <div class="premium-calendar">
          <div class="calendar-header-bar">
            <el-button circle plain @click="prevMonth"><el-icon><ArrowLeft /></el-icon></el-button>
            <span class="calendar-title">{{ calendarTitle }}</span>
            <el-button circle plain @click="nextMonth"><el-icon><ArrowRight /></el-icon></el-button>
          </div>
          
          <div class="calendar-grid">
            <div class="calendar-header">
              <span v-for="day in weekDays" :key="day">{{ day }}</span>
            </div>
            <div class="calendar-body">
              <template v-for="(week, wi) in calendarWeeks" :key="wi">
                <div class="calendar-row">
                  <div v-for="(day, di) in week" :key="di" class="calendar-day-wrapper">
                    <div class="calendar-day" :class="{ checked: day.checked, empty: !day.value, today: day.today }">
                      <span v-if="day.value">{{ day.value }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { checkinApi } from '@/api/checkin'
import { ElMessage } from 'element-plus'
import { Select, Pointer, ArrowLeft, ArrowRight, Calendar, Trophy, Loading } from '@element-plus/icons-vue'

const loading = ref(false)
const checkingIn = ref(false)
const records = ref([])
const stats = ref({ monthCount: 0, streakDays: 0, checkInDates: [] })
const checkedIn = ref(false)
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const calendarTitle = computed(() => `${currentYear.value}年 ${currentMonth.value + 1}月`)

const calendarWeeks = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  const firstDay = new Date(year, month, 1).getDay()
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const today = new Date()
  const isCurrentMonth = year === today.getFullYear() && month === today.getMonth()
  const todayDate = today.getDate()

  const checkedSet = new Set(stats.value.checkInDates || [])

  const weeks = []
  let week = []

  for (let i = 0; i < firstDay; i++) {
    week.push({ value: null, checked: false, empty: true, today: false })
  }

  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    week.push({
      value: d,
      checked: checkedSet.has(dateStr),
      empty: false,
      today: isCurrentMonth && d === todayDate
    })
    if (week.length === 7) {
      weeks.push(week)
      week = []
    }
  }

  if (week.length > 0) {
    while (week.length < 7) {
      week.push({ value: null, checked: false, empty: true, today: false })
    }
    weeks.push(week)
  }

  return weeks
})

const prevMonth = () => {
  if (currentMonth.value === 0) {
    currentYear.value--
    currentMonth.value = 11
  } else {
    currentMonth.value--
  }
}

const nextMonth = () => {
  const now = new Date()
  if (currentYear.value >= now.getFullYear() && currentMonth.value >= now.getMonth()) return
  if (currentMonth.value === 11) {
    currentYear.value++
    currentMonth.value = 0
  } else {
    currentMonth.value++
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const [recordList, statsData] = await Promise.all([
      checkinApi.myRecords(),
      checkinApi.myStats()
    ])
    records.value = recordList
    stats.value = statsData

    const today = new Date().toISOString().split('T')[0]
    checkedIn.value = (statsData.checkInDates || []).includes(today)
  } catch (e) {
    console.error('获取签到数据失败:', e)
  } finally {
    loading.value = false
  }
}

const handleCheckIn = async () => {
  checkingIn.value = true
  try {
    await checkinApi.checkIn()
    ElMessage.success('签到成功!')
    await fetchData()
  } catch (e) {
    console.error('签到失败:', e)
    ElMessage.error(e.response?.data?.message || '签到失败，请稍后重试')
  } finally {
    checkingIn.value = false
  }
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const parts = dateStr.split('T')
  if (parts.length >= 2) {
    return parts[0] + ' ' + parts[1].substring(0, 5)
  }
  return dateStr
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.checkin-view {
  max-width: 1000px;
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

/* 统计卡片 */
.stats-overview {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.glass-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: #eff6ff;
  color: #3b82f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-icon.warning {
  background: #fffbeb;
  color: #f59e0b;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.2;
}

.stat-value small {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  margin-left: 4px;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

/* 主内容网格 */
.main-content-grid {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 24px;
}

/* 左侧签到区 */
.checkin-action-area {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.checkin-btn-wrap {
  background: white;
  border-radius: 24px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
}

/* 发光按钮设计 */
.glow-button {
  position: relative;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.glow-button:hover:not(.is-checked):not(.is-loading) {
  transform: scale(1.05);
}

.glow-button:active:not(.is-checked):not(.is-loading) {
  transform: scale(0.95);
}

.glow-bg {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #3b82f6 100%);
  opacity: 0.8;
  filter: blur(15px);
  transition: all 0.5s ease;
}

.btn-content {
  position: relative;
  z-index: 1;
  width: 170px;
  height: 170px;
  background: linear-gradient(135deg, var(--primary-color) 0%, #2563eb 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: inset 0 -4px 12px rgba(0,0,0,0.2), inset 0 4px 12px rgba(255,255,255,0.3);
}

.btn-text {
  margin-top: 12px;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
}

.is-loading-icon {
  animation: rotating 2s linear infinite;
}

/* 已签到状态 */
.glow-button.is-checked .glow-bg {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  filter: blur(20px);
}

.glow-button.is-checked .btn-content {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  cursor: default;
}

.hint-text-box {
  margin-top: 30px;
  text-align: center;
}

.checkin-hint {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.checkin-hint.success {
  color: #10b981;
  font-weight: 600;
}

/* 签到时间线 */
.timeline-section {
  background: white;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 20px;
}

.custom-timeline {
  padding-left: 4px;
}

.timeline-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-text {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

/* 右侧日历 */
.premium-calendar {
  background: white;
  border-radius: 24px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
  height: 100%;
}

.calendar-header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding: 0 10px;
}

.calendar-title {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #94a3b8;
  margin-bottom: 16px;
}

.calendar-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.calendar-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.calendar-day-wrapper {
  display: flex;
  justify-content: center;
}

.calendar-day {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 500;
  border-radius: 50%;
  color: #334155;
  transition: all 0.2s;
  cursor: default;
}

.calendar-day.checked {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  font-weight: 700;
  box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3);
}

.calendar-day.today:not(.checked) {
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
  font-weight: 700;
}

.calendar-day.empty {
  visibility: hidden;
}

@media (max-width: 768px) {
  .main-content-grid {
    grid-template-columns: 1fr;
  }
  
  .glow-button {
    width: 160px;
    height: 160px;
  }
  
  .btn-content {
    width: 130px;
    height: 130px;
  }
  
  .calendar-day {
    width: 36px;
    height: 36px;
    font-size: 14px;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
}
</style>
