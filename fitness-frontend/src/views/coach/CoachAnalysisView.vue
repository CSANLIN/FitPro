<template>
  <div class="coach-analysis">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="summary-row">
      <el-col :xs="12" :sm="6" v-for="card in summaryCards" :key="card.label">
        <el-card class="summary-card" shadow="hover">
          <div class="summary-value">{{ card.value }}</div>
          <div class="summary-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 出勤趋势 -->
      <el-col :xs="24" :lg="14" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>出勤趋势</span>
              <el-radio-group v-model="trendPeriod" size="small" @change="fetchTrend">
                <el-radio-button value="week">按周</el-radio-button>
                <el-radio-button value="month">按月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 课程排行 -->
      <el-col :xs="24" :lg="10" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <span>热门课程排行</span>
          </template>
          <div ref="rankChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 时段分布 -->
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <span>时段分布</span>
          </template>
          <div ref="timeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 出勤状态 -->
      <el-col :xs="24" :lg="12" class="chart-col">
        <el-card class="chart-card">
          <template #header>
            <span>出勤状态分布</span>
          </template>
          <div ref="attendanceChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { coachApi } from '@/api/coach'

const summaryCards = ref([
  { label: '总排课数', value: '-' },
  { label: '总预约数', value: '-' },
  { label: '出勤率', value: '-' },
  { label: '学员数', value: '-' }
])

const trendPeriod = ref('week')
const trendChartRef = ref(null)
const rankChartRef = ref(null)
const timeChartRef = ref(null)
const attendanceChartRef = ref(null)

let trendChart = null
let rankChart = null
let timeChart = null
let attendanceChart = null

const chartInstances = []

const initChart = (refEl) => {
  if (!refEl.value) return null
  const instance = echarts.init(refEl.value)
  chartInstances.push(instance)
  return instance
}

// 获取概览数据
const fetchSummary = async () => {
  try {
    const data = await coachApi.getSummary()
    summaryCards.value = [
      { label: '总排课数', value: data.totalSchedules },
      { label: '总预约数', value: data.totalBookings },
      { label: '出勤率', value: data.attendanceRate + '%' },
      { label: '学员数', value: data.totalStudents }
    ]
  } catch (e) {
    console.error('获取概览失败:', e)
  }
}

// 出勤趋势
const fetchTrend = async () => {
  try {
    const data = await coachApi.getAttendanceTrend(trendPeriod.value)
    if (!trendChart) {
      trendChart = initChart(trendChartRef)
    }
    if (!trendChart) return

    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['总预约', '出勤数', '出勤率'] },
      grid: { left: 50, right: 50, bottom: 30, top: 40 },
      xAxis: {
        type: 'category',
        data: data.map(d => d.period),
        axisLabel: { rotate: 30 }
      },
      yAxis: [
        { type: 'value', name: '人数' },
        { type: 'value', name: '比例', min: 0, max: 100, axisLabel: { formatter: '{value}%' } }
      ],
      series: [
        {
          name: '总预约',
          type: 'bar',
          data: data.map(d => d.total),
          itemStyle: { color: '#909399' }
        },
        {
          name: '出勤数',
          type: 'bar',
          data: data.map(d => d.attended),
          itemStyle: { color: '#67c23a' }
        },
        {
          name: '出勤率',
          type: 'line',
          yAxisIndex: 1,
          data: data.map(d => d.rate),
          smooth: true,
          lineStyle: { color: '#ffcc2e', width: 3 },
          symbol: 'circle',
          symbolSize: 8
        }
      ]
    })
  } catch (e) {
    console.error('获取趋势失败:', e)
  }
}

// 课程排行
const fetchRank = async () => {
  try {
    const data = await coachApi.getCourseRank()
    if (!rankChart) {
      rankChart = initChart(rankChartRef)
    }
    if (!rankChart) return

    rankChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 100, right: 30, bottom: 20, top: 20 },
      xAxis: { type: 'value' },
      yAxis: {
        type: 'category',
        data: data.map(d => d.courseName).reverse(),
        axisLabel: { fontSize: 12 }
      },
      series: [{
        type: 'bar',
        data: data.map(d => d.bookingCount).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#79bbff' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: { show: true, position: 'right' }
      }]
    })
  } catch (e) {
    console.error('获取排行失败:', e)
  }
}

// 时段分布
const fetchTimeDistribution = async () => {
  try {
    const data = await coachApi.getTimeDistribution()
    if (!timeChart) {
      timeChart = initChart(timeChartRef)
    }
    if (!timeChart) return

    timeChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 10 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        data: data.map(d => ({ name: d.period, value: d.bookingCount })),
        label: { formatter: '{b}\n{d}%' },
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' }
        }
      }]
    })
  } catch (e) {
    console.error('获取时段分布失败:', e)
  }
}

// 出勤状态
const fetchAttendanceStatus = async () => {
  try {
    const summary = await coachApi.getSummary()
    // 没有单独接口，从 summary 拆解：总预约 - 出勤 = 未出勤
    const total = summary.totalBookings
    const attended = summary.attendanceRate > 0 && total > 0
      ? Math.round(total * summary.attendanceRate / 100)
      : 0
    const notAttended = total - attended

    if (!attendanceChart) {
      attendanceChart = initChart(attendanceChartRef)
    }
    if (!attendanceChart) return

    attendanceChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 10 },
      series: [{
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        label: { show: false },
        data: [
          { name: '已出勤', value: attended, itemStyle: { color: '#67c23a' } },
          { name: '未出勤', value: notAttended, itemStyle: { color: '#f56c6c' } }
        ]
      }]
    })
  } catch (e) {
    console.error('获取出勤状态失败:', e)
  }
}

const renderAllCharts = () => {
  nextTick(() => {
    fetchTrend()
    fetchRank()
    fetchTimeDistribution()
    fetchAttendanceStatus()
  })
}

onMounted(() => {
  fetchSummary()
  renderAllCharts()
})

onUnmounted(() => {
  chartInstances.forEach(instance => instance?.dispose())
})
</script>

<style scoped lang="scss">
.summary-row {
  margin-bottom: 24px;
}

.summary-card {
  text-align: center;
  margin-bottom: 20px;

  .summary-value {
    font-size: 32px;
    font-weight: 700;
    color: var(--text-primary);
    line-height: 1.2;
  }

  .summary-label {
    font-size: 14px;
    color: var(--text-secondary);
    margin-top: 8px;
  }
}

.charts-row {
  margin-bottom: 24px;
}

.chart-col {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}
</style>
