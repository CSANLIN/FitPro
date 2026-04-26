<template>
  <div class="checkin-view">
    <h2 class="page-title">签到打卡</h2>

    <!-- 签到统计 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.monthCount }}</div>
          <div class="stat-label">本月签到</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.streakDays }}</div>
          <div class="stat-label">连续签到</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 签到按钮 -->
    <el-card class="checkin-card">
      <div class="checkin-btn-wrap">
        <el-button
          :type="checkedIn ? 'success' : 'primary'"
          :disabled="checkedIn"
          :loading="checkingIn"
          size="large"
          class="checkin-btn"
          @click="handleCheckIn"
        >
          <el-icon v-if="checkedIn" :size="28"><Select /></el-icon>
          <el-icon v-else :size="28"><Pointer /></el-icon>
          <span>{{ checkedIn ? '今日已签到' : '点击签到' }}</span>
        </el-button>
        <p class="checkin-hint" v-if="!checkedIn">签到消耗会籍次数，次卡每次签到扣1次</p>
        <p class="checkin-hint success" v-else>继续保持，坚持就是胜利!</p>
      </div>
    </el-card>

    <!-- 本月签到日历 -->
    <el-card class="calendar-card">
      <template #header>
        <span>本月签到日历</span>
      </template>
      <div class="calendar-grid">
        <div class="calendar-header">
          <span v-for="day in weekDays" :key="day">{{ day }}</span>
        </div>
        <div class="calendar-body">
          <template v-for="(week, wi) in calendarWeeks" :key="wi">
            <div
              v-for="(day, di) in week"
              :key="di"
              class="calendar-day"
              :class="{ checked: day.checked, empty: !day.value, today: day.today }"
            >
              <span v-if="day.value">{{ day.value }}</span>
            </div>
          </template>
        </div>
      </div>
    </el-card>

    <!-- 签到记录列表 -->
    <el-card v-loading="loading" class="record-card">
      <template #header>
        <span>签到记录</span>
      </template>
      <el-empty v-if="!loading && records.length === 0" description="暂无签到记录" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="item in records"
          :key="item.id"
          :timestamp="formatDateTime(item.checkInTime)"
          placement="top"
        >
          <span>{{ item.checkInTime ? item.checkInTime.substring(0, 10) : '' }} 签到</span>
          <el-tag size="small" type="info" style="margin-left: 8px">{{ item.checkInType === 'MANUAL' ? '手动签到' : '扫码签到' }}</el-tag>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { checkinApi } from '@/api/checkin'
import { ElMessage } from 'element-plus'
import { Select, Pointer } from '@element-plus/icons-vue'

const loading = ref(false)
const checkingIn = ref(false)
const records = ref([])
const stats = ref({ monthCount: 0, streakDays: 0, checkInDates: [] })
const checkedIn = ref(false)

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const calendarWeeks = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth()
  const firstDay = new Date(year, month, 1).getDay()
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const today = now.getDate()

  const checkedSet = new Set(stats.value.checkInDates || [])

  const weeks = []
  let week = []

  // 填充月前空白
  for (let i = 0; i < firstDay; i++) {
    week.push({ value: null, checked: false, empty: true, today: false })
  }

  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    week.push({
      value: d,
      checked: checkedSet.has(dateStr),
      empty: false,
      today: d === today
    })
    if (week.length === 7) {
      weeks.push(week)
      week = []
    }
  }

  // 填充月后空白
  if (week.length > 0) {
    while (week.length < 7) {
      week.push({ value: null, checked: false, empty: true, today: false })
    }
    weeks.push(week)
  }

  return weeks
})

const fetchData = async () => {
  loading.value = true
  try {
    const [recordList, statsData] = await Promise.all([
      checkinApi.myRecords(),
      checkinApi.myStats()
    ])
    records.value = recordList
    stats.value = statsData

    // 判断今天是否已签到
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
  max-width: 900px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 20px;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
  border-radius: 12px;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--el-color-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.checkin-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.checkin-btn-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.checkin-btn {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  font-size: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.checkin-btn span {
  font-size: 14px;
}

.checkin-hint {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin: 12px 0 0;
}

.checkin-hint.success {
  color: var(--el-color-success);
}

.calendar-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.calendar-grid {
  user-select: none;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

.calendar-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.calendar-body > div {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  margin: auto;
}

.calendar-day.checked {
  background: var(--el-color-success);
  color: white;
  font-weight: 600;
}

.calendar-day.today:not(.checked) {
  border: 2px solid var(--el-color-primary);
  font-weight: 600;
}

.calendar-day.empty {
  visibility: hidden;
}

.record-card {
  border-radius: 12px;
}

@media (max-width: 768px) {
  .checkin-btn {
    width: 120px;
    height: 120px;
    font-size: 14px;
  }

  .calendar-day {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
}
</style>
