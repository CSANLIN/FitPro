<template>
  <div class="dashboard-view">
    <!-- 数据概览卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card" :style="{ borderTop: `3px solid ${card.color}` }">
          <div class="stat-content">
            <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
            <el-icon class="stat-icon" :style="{ color: card.color }">
              <component :is="card.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>签到趋势（近7日）</span>
            </div>
          </template>
          <div ref="checkInChartRef" class="chart-container"></div>
          <div v-if="checkInTrendEmpty" class="chart-empty">
            <el-empty description="暂无签到数据" :image-size="80" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>会员注册趋势（近7日）</span>
            </div>
          </template>
          <div ref="registerChartRef" class="chart-container"></div>
          <div v-if="registerTrendEmpty" class="chart-empty">
            <el-empty description="暂无注册数据" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区域 -->
    <el-row :gutter="16" class="list-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span>近期待办排课</span>
              <el-tag v-if="stats?.upcomingSchedules" type="warning" size="small">
                {{ stats.upcomingSchedules }} 项
              </el-tag>
            </div>
          </template>
          <div v-if="!stats?.upcomingScheduleList?.length" class="list-empty">
            <el-empty description="暂无待办排课" :image-size="60" />
          </div>
          <div v-else class="schedule-list">
            <div v-for="item in stats.upcomingScheduleList" :key="item.id" class="schedule-item">
              <div class="schedule-info">
                <span class="schedule-course">{{ item.courseName || '课程#' + item.id }}</span>
                <span class="schedule-coach">{{ item.coachName }}</span>
              </div>
              <div class="schedule-time">
                <el-tag size="small" effect="plain">
                  {{ item.scheduleDate }} {{ item.startTime }}-{{ item.endTime }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span>今日签到记录</span>
              <el-tag v-if="stats?.todayCheckIns" type="success" size="small">
                {{ stats.todayCheckIns }} 人
              </el-tag>
            </div>
          </template>
          <div v-if="!stats?.todayCheckInList?.length" class="list-empty">
            <el-empty description="今日暂无签到" :image-size="60" />
          </div>
          <div v-else class="checkin-list">
            <div v-for="item in stats.todayCheckInList" :key="item.id" class="checkin-item">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="checkin-user">{{ item.userName }}</span>
              <span class="checkin-time">{{ item.checkInTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { dashboardApi } from '@/api/dashboard'
import { User, Select, Ticket, Notebook, Calendar, UserFilled, Trophy } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const stats = ref(null)
const loading = ref(false)
const checkInChartRef = ref(null)
const registerChartRef = ref(null)
let checkInChartInstance = null
let registerChartInstance = null

const statCards = computed(() => [
  { label: '会员总数', value: stats.value?.totalMembers ?? '-', icon: User, color: '#409EFF' },
  { label: '今日签到', value: stats.value?.todayCheckIns ?? '-', icon: Select, color: '#67C23A' },
  { label: '有效会籍', value: stats.value?.activeMemberships ?? '-', icon: Ticket, color: '#E6A23C' },
  { label: '课程总数', value: stats.value?.totalCourses ?? '-', icon: Notebook, color: '#F56C6C' },
  { label: '待开课数', value: stats.value?.upcomingSchedules ?? '-', icon: Calendar, color: '#909399' },
  { label: '教练总数', value: stats.value?.totalCoaches ?? '-', icon: UserFilled, color: '#9B59B6' }
])

const checkInTrendEmpty = computed(() => {
  return stats.value?.checkInTrend?.every(item => item.count === 0)
})

const registerTrendEmpty = computed(() => {
  return stats.value?.registerTrend?.every(item => item.count === 0)
})

const fetchStats = async () => {
  loading.value = true
  try {
    stats.value = await dashboardApi.getStats()
    await nextTick()
    renderCharts()
  } catch (e) {
    console.error('获取仪表盘数据失败:', e)
  } finally {
    loading.value = false
  }
}

const renderCharts = () => {
  // 签到趋势图
  if (checkInChartRef.value && stats.value?.checkInTrend) {
    if (!checkInChartInstance) {
      checkInChartInstance = echarts.init(checkInChartRef.value)
    }
    const data = stats.value.checkInTrend
    checkInChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, bottom: 30, top: 20 },
      xAxis: {
        type: 'category',
        data: data.map(d => d.date.slice(5)),
        axisLabel: { fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        minInterval: 1
      },
      series: [{
        type: 'line',
        data: data.map(d => d.count),
        smooth: true,
        lineStyle: { color: '#67C23A', width: 3 },
        itemStyle: { color: '#67C23A' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      }]
    })
  }

  // 注册趋势图
  if (registerChartRef.value && stats.value?.registerTrend) {
    if (!registerChartInstance) {
      registerChartInstance = echarts.init(registerChartRef.value)
    }
    const data = stats.value.registerTrend
    registerChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, bottom: 30, top: 20 },
      xAxis: {
        type: 'category',
        data: data.map(d => d.date.slice(5)),
        axisLabel: { fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        minInterval: 1
      },
      series: [{
        type: 'bar',
        data: data.map(d => d.count),
        barWidth: '40%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.3)' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }]
    })
  }
}

onMounted(() => {
  fetchStats()
})

// 窗口大小变化时自适应图表
watch(() => stats.value, () => {
  window.addEventListener('resize', () => {
    checkInChartInstance?.resize()
    registerChartInstance?.resize()
  })
})
</script>

<style scoped>
.dashboard-view {
  max-width: 1200px;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 12px;
  margin-bottom: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-content {
  position: relative;
  padding: 4px 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.stat-icon {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 32px;
  opacity: 0.2;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card,
.list-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 15px;
}

.chart-container {
  height: 280px;
  width: 100%;
}

.chart-empty {
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.list-empty {
  padding: 20px 0;
}

.schedule-list,
.checkin-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.schedule-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.schedule-item:hover {
  background-color: var(--el-fill-color-light);
}

.schedule-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.schedule-course {
  font-weight: 500;
  font-size: 14px;
}

.schedule-coach {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.checkin-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.checkin-item:hover {
  background-color: var(--el-fill-color-light);
}

.checkin-user {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.checkin-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
