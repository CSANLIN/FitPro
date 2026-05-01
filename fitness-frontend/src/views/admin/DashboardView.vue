<template>
  <div class="dashboard-view">
    <!-- 顶部布局：左侧概览，右侧日程 -->
    <el-row :gutter="24" class="top-row">
      <!-- 左侧 Blob 大卡片 -->
      <el-col :xs="24" :lg="16">
        <div class="hero-overview-card">
          <!-- 背景光晕 blobs -->
          <div class="blob-yellow"></div>
          <div class="blob-red"></div>
          
          <div class="hero-content">
            <h2 class="hero-title">今日健身房运营数据</h2>
            <p class="hero-sub">活跃会员与今日签到情况</p>
            
            <div class="hero-metrics">
              <div class="metric-circle dark-circle">
                <span class="m-value">{{ stats?.activeMemberships || 0 }}</span>
                <span class="m-label">活跃会员</span>
              </div>
              <div class="metric-circle yellow-circle">
                <span class="m-value">{{ stats?.todayCheckIns || 0 }}</span>
                <span class="m-label">今日签到</span>
              </div>
              <div class="metric-circle red-circle">
                <span class="m-value">{{ stats?.totalMembers || 0 }}</span>
                <span class="m-label">总会员数</span>
              </div>
            </div>
            
            <div class="hero-legend">
              <div class="legend-item"><span class="dot c-yellow"></span> 今日到店签到</div>
              <div class="legend-item"><span class="dot c-red"></span> 平台总注册会员</div>
              <div class="legend-item"><span class="dot c-dark"></span> 近期活跃锻炼用户</div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧 暗色卡片 (日程/图鉴) -->
      <el-col :xs="24" :lg="8">
        <div class="dark-schedule-card">
          <div class="card-header">
            <h3 class="card-title">即将开始的课程</h3>
            <span class="card-action">查看全部 <el-icon><ArrowRight /></el-icon></span>
          </div>
          
          <!-- 简易日历/日程占位 -->
          <div class="calendar-mini">
            <div class="cal-days">
              <span>M</span><span>T</span><span>W</span><span>T</span><span>F</span><span class="active">S</span><span>S</span>
            </div>
            <div class="cal-dates">
              <span>24</span><span>25</span><span>26</span><span>27</span><span>28</span><span class="active">29</span><span>30</span>
            </div>
          </div>
          
          <div class="schedule-list">
            <div v-if="!stats?.upcomingScheduleList?.length" class="empty-text">今日暂无即将开始的课程。</div>
            <div v-for="(item, index) in stats?.upcomingScheduleList?.slice(0, 3)" :key="item.id" class="s-item">
              <div class="s-time">{{ item.startTime }}</div>
              <div class="s-info">
                <div class="s-name">{{ item.courseName || '课程#' + item.id }}</div>
                <div class="s-coach">教练: {{ item.coachName }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 底部区域：图表与数据块 -->
    <el-row :gutter="24" class="bottom-row">
      <!-- 签到趋势图 -->
      <el-col :xs="24" :lg="12">
        <el-card class="premium-panel pill-card chart-card">
          <div class="pill-card-header">
            <div>
              <h3 class="card-title">签到活跃趋势</h3>
              <p class="card-sub">最近7日签到情况</p>
            </div>
            <div class="action-btn"><el-icon><EditPen /></el-icon></div>
          </div>
          
          <div ref="checkInChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 其他零散数据块 -->
      <el-col :xs="24" :lg="12">
        <div class="small-cards-grid">
          <el-card class="premium-panel pill-card small-stat">
            <div class="stat-content">
              <h3 class="card-title">课程总数</h3>
              <p class="card-sub">可供会员预约</p>
            </div>
            <div class="stat-circle ring-orange">
              <span class="s-val">{{ stats?.totalCourses || 0 }}</span>
            </div>
          </el-card>

          <el-card class="premium-panel pill-card small-stat">
            <div class="stat-content">
              <h3 class="card-title">活跃教练</h3>
              <p class="card-sub">平台专业教练</p>
            </div>
            <div class="stat-circle ring-blue">
              <span class="s-val">{{ stats?.totalCoaches || 0 }}</span>
            </div>
          </el-card>

          <el-card class="premium-panel pill-card small-stat">
            <div class="stat-content">
              <h3 class="card-title">系统会员</h3>
              <p class="card-sub">已注册用户数</p>
            </div>
            <div class="stat-circle ring-green">
              <span class="s-val">{{ stats?.totalMembers || 0 }}</span>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { dashboardApi } from '@/api/dashboard'
import { ArrowRight, EditPen } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const stats = ref(null)
const loading = ref(false)
const checkInChartRef = ref(null)
let checkInChartInstance = null

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
  if (checkInChartRef.value && stats.value?.checkInTrend) {
    if (!checkInChartInstance) {
      checkInChartInstance = echarts.init(checkInChartRef.value)
    }
    const data = stats.value.checkInTrend
    checkInChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 30, right: 20, bottom: 20, top: 20 },
      xAxis: {
        type: 'category',
        data: data.map(d => d.date.slice(5)),
        axisLabel: { color: '#8c8c93', fontWeight: 600, fontSize: 12 },
        axisLine: { show: false },
        axisTick: { show: false }
      },
      yAxis: {
        show: false,
        type: 'value'
      },
      series: [{
        type: 'line',
        data: data.map(d => d.count),
        smooth: 0.4,
        symbol: 'none',
        lineStyle: { color: '#ffcc2e', width: 4 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 204, 46, 0.4)' },
            { offset: 1, color: 'rgba(255, 204, 46, 0)' }
          ])
        }
      }]
    })
  }
}

onMounted(() => {
  fetchStats()
})

watch(() => stats.value, () => {
  window.addEventListener('resize', () => {
    checkInChartInstance?.resize()
  })
})
</script>

<style scoped>
.dashboard-view {
  width: 100%;
}

.top-row { margin-bottom: 24px; }
.bottom-row { margin-bottom: 24px; }

.card-title {
  font-size: 18px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 4px;
}

.card-sub {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 500;
}

/* Hero Overview Card (Be.run Style) */
.hero-overview-card {
  background-color: #e5e2da;
  border-radius: var(--border-radius-xl);
  padding: 40px;
  position: relative;
  overflow: hidden;
  height: 100%;
  min-height: 340px;
  display: flex;
  flex-direction: column;
}

.blob-yellow {
  position: absolute;
  top: 10%;
  right: 10%;
  width: 280px;
  height: 280px;
  background: rgba(255, 204, 46, 0.9);
  filter: blur(40px);
  border-radius: 50%;
  z-index: 0;
}

.blob-red {
  position: absolute;
  bottom: 5%;
  left: 30%;
  width: 200px;
  height: 200px;
  background: rgba(255, 107, 107, 0.85);
  filter: blur(40px);
  border-radius: 50%;
  z-index: 0;
}

.hero-content {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.hero-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.hero-sub {
  font-size: 15px;
  color: var(--text-regular);
  margin: 0 0 32px;
  font-weight: 500;
}

.hero-metrics {
  display: flex;
  gap: 16px;
  margin-top: auto;
  margin-bottom: 40px;
}

.metric-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
}

.m-value { font-size: 22px; font-weight: 800; line-height: 1; margin-bottom: 4px; }
.m-label { font-size: 12px; font-weight: 600; }

.dark-circle { background: var(--accent-dark); color: white; }
.dark-circle .m-label { color: #a0a0a0; }

.yellow-circle { background: var(--primary-color); color: var(--text-primary); transform: translateY(-20px); }
.yellow-circle .m-label { color: #8a6a00; }

.red-circle { background: #ff6b6b; color: white; transform: translateY(10px); }
.red-circle .m-label { color: #ffe0e0; }

.hero-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.dot {
  width: 24px;
  height: 6px;
  border-radius: 3px;
}

.c-yellow { background: var(--primary-color); }
.c-red { background: #ff6b6b; }
.c-dark { background: var(--accent-dark); }

/* Dark Schedule Card */
.dark-schedule-card {
  background: var(--accent-dark);
  border-radius: var(--border-radius-xl);
  padding: 32px;
  height: 100%;
  color: white;
  display: flex;
  flex-direction: column;
}

.dark-schedule-card .card-title { color: white; }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-action {
  font-size: 13px;
  color: #8c8c93;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.calendar-mini {
  margin-bottom: 32px;
}

.cal-days, .cal-dates {
  display: flex;
  justify-content: space-between;
  text-align: center;
}

.cal-days span {
  width: 32px;
  font-size: 12px;
  color: #6a6a70;
  font-weight: 600;
  margin-bottom: 12px;
}

.cal-dates span {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  border-radius: 50%;
  color: white;
}

.cal-dates span.active {
  background: var(--primary-color);
  color: var(--text-primary);
}
.cal-days span.active { color: white; }

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

.empty-text {
  font-size: 14px;
  color: #6a6a70;
  text-align: center;
  margin-top: 20px;
}

.s-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.s-time {
  font-size: 14px;
  font-weight: 700;
  color: #8c8c93;
  width: 45px;
}

.s-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.s-name {
  font-size: 15px;
  font-weight: 600;
  color: white;
}

.s-coach {
  font-size: 12px;
  color: #6a6a70;
}

/* Pill Cards Bottom */
.pill-card {
  padding: 8px !important;
  border-radius: var(--border-radius-xl) !important;
  height: 100%;
}

.pill-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bg-base);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
  font-size: 16px;
  cursor: pointer;
}

.chart-card {
  display: flex;
  flex-direction: column;
}

.chart-container {
  height: 220px;
  width: 100%;
  margin-top: 16px;
}

.small-cards-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 24px;
  height: 100%;
}

.small-stat {
  display: flex !important;
  align-items: center;
  justify-content: space-between;
}

.stat-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 800;
}

.ring-orange {
  border: 4px solid #ffcc2e;
  color: var(--text-primary);
}

.ring-blue {
  border: 4px solid #4facfe;
  color: var(--text-primary);
}

.ring-green {
  border: 4px solid #10b981;
  color: var(--text-primary);
}

@media (max-width: 992px) {
  .hero-overview-card { margin-bottom: 24px; }
  .chart-card { margin-bottom: 24px; }
  .small-cards-grid {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
