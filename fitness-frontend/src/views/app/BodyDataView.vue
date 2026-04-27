<template>
  <div class="body-data-view">
    <div class="page-header">
      <h2 class="page-title">身体数据</h2>
      <p class="page-subtitle">记录你的改变，看见每一次蜕变</p>
    </div>

    <!-- 最新数据卡片 (Premium) -->
    <div v-if="latestRecord" class="latest-data-hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <div class="hero-top">
          <div class="hero-title">最新记录</div>
          <div class="hero-date"><el-icon><Calendar /></el-icon> {{ latestRecord.recordDate }}</div>
        </div>
        
        <div class="metrics-grid">
          <div class="metric-box">
            <div class="metric-icon"><el-icon><Odometer /></el-icon></div>
            <div class="metric-info">
              <div class="m-value">{{ latestRecord.weight || '-' }}<small>kg</small></div>
              <div class="m-label">体重</div>
            </div>
          </div>
          <div class="metric-box">
            <div class="metric-icon"><el-icon><DataLine /></el-icon></div>
            <div class="metric-info">
              <div class="m-value">{{ latestRecord.bmi || '-' }}</div>
              <div class="m-label">BMI</div>
            </div>
          </div>
          <div class="metric-box">
            <div class="metric-icon"><el-icon><PieChart /></el-icon></div>
            <div class="metric-info">
              <div class="m-value">{{ latestRecord.bodyFat || '-' }}<small>%</small></div>
              <div class="m-label">体脂率</div>
            </div>
          </div>
          <div class="metric-box">
            <div class="metric-icon"><el-icon><User /></el-icon></div>
            <div class="metric-info">
              <div class="m-value">{{ latestRecord.height || '-' }}<small>cm</small></div>
              <div class="m-label">身高</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 无数据提示 -->
    <el-empty v-else-if="!pageLoading" description="暂无身体数据记录，快来记录第一次变化吧" :image-size="120" class="premium-empty" />
    <el-skeleton :loading="pageLoading" animated class="latest-data-hero" v-else>
      <template #template>
        <div style="height: 180px; width: 100%; border-radius: 24px; background: #f1f5f9;"></div>
      </template>
    </el-skeleton>

    <!-- 标签页：录入 + 历史 -->
    <div class="tabs-container">
      <el-tabs v-model="activeTab" class="premium-tabs">
        <!-- 录入数据 -->
        <el-tab-pane label="录入数据" name="create">
          <div class="glass-form-card">
            <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
              <div class="form-grid">
                <el-form-item label="体重 (kg)" prop="weight">
                  <el-input-number v-model="form.weight" :min="20" :max="300" :precision="1" :step="0.1" class="full-width-num" />
                </el-form-item>
                <el-form-item label="身高 (cm)" prop="height">
                  <el-input-number v-model="form.height" :min="100" :max="250" :precision="1" :step="0.5" class="full-width-num" />
                </el-form-item>
                <el-form-item label="体脂率 (%)" prop="bodyFat">
                  <el-input-number v-model="form.bodyFat" :min="3" :max="60" :precision="1" :step="0.1" class="full-width-num" />
                </el-form-item>
                <el-form-item label="胸围 (cm)" prop="chest">
                  <el-input-number v-model="form.chest" :min="50" :max="200" :precision="1" :step="0.5" class="full-width-num" />
                </el-form-item>
                <el-form-item label="腰围 (cm)" prop="waist">
                  <el-input-number v-model="form.waist" :min="40" :max="200" :precision="1" :step="0.5" class="full-width-num" />
                </el-form-item>
                <el-form-item label="臀围 (cm)" prop="hip">
                  <el-input-number v-model="form.hip" :min="50" :max="200" :precision="1" :step="0.5" class="full-width-num" />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="记录日期" prop="recordDate" class="flex-item">
                  <el-date-picker v-model="form.recordDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" :disabled-date="disableFutureDate" style="width: 100%" />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="备注说明" prop="remark" class="flex-item">
                  <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="例如：早晨空腹测量" maxlength="200" show-word-limit />
                </el-form-item>
              </div>
              
              <div class="form-actions">
                <el-button @click="resetForm" round>重置</el-button>
                <el-button type="primary" color="#3b82f6" @click="handleCreate" :loading="createLoading" round class="submit-btn">
                  保存数据记录
                </el-button>
              </div>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 历史记录 -->
        <el-tab-pane label="历史记录" name="history">
          <div class="history-container">
            <!-- 趋势图 -->
            <div class="chart-card-wrapper" v-if="historyList.length > 1">
              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-title">体重 / 体脂趋势</span>
                </div>
                <div ref="trendChartRef" class="trend-chart"></div>
              </div>
            </div>
            <div class="history-filter">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                @change="handleDateChange"
                clearable
                class="glass-picker"
              />
            </div>

            <div class="history-list-wrapper" v-loading="historyLoading">
              <el-empty v-if="historyList.length === 0" description="暂无历史记录" />
              
              <div v-else class="data-timeline">
                <div v-for="item in historyList" :key="item.id" class="timeline-item">
                  <div class="t-date">
                    <span class="d-day">{{ dayOnly(item.recordDate) }}</span>
                    <span class="d-month">{{ monthOnly(item.recordDate) }}月</span>
                  </div>
                  <div class="t-content">
                    <div class="t-main-stats">
                      <div class="s-box"><span class="s-val">{{ item.weight || '-' }}</span><span class="s-lbl">kg</span></div>
                      <div class="s-box"><span class="s-val">{{ item.bodyFat || '-' }}</span><span class="s-lbl">%</span></div>
                      <div class="s-box"><span class="s-val">{{ item.bmi || '-' }}</span><span class="s-lbl">BMI</span></div>
                    </div>
                    <div class="t-sub-stats">
                      围度: 胸 {{ item.chest||'-' }} / 腰 {{ item.waist||'-' }} / 臀 {{ item.hip||'-' }}
                    </div>
                    <div class="t-remark" v-if="item.remark">备注: {{ item.remark }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { bodyRecordApi } from '@/api/bodyRecord'
import { ElMessage } from 'element-plus'
import { Calendar, Odometer, DataLine, PieChart, User } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 标签页
const activeTab = ref('create')

// 表单引用
const formRef = ref()

// 加载状态
const createLoading = ref(false)
const pageLoading = ref(true)
const historyLoading = ref(false)

// 最新记录
const latestRecord = ref(null)

// 录入表单
const form = reactive({
  weight: undefined,
  height: undefined,
  bodyFat: undefined,
  chest: undefined,
  waist: undefined,
  hip: undefined,
  recordDate: '',
  remark: ''
})

// 日期范围筛选
const dateRange = ref([])

// 历史记录
const historyList = ref([])

// 趋势图
const trendChartRef = ref(null)
let trendChartInstance = null

const renderTrendChart = async () => {
  await nextTick()
  if (!trendChartRef.value || historyList.value.length < 2) return
  if (!trendChartInstance) {
    trendChartInstance = echarts.init(trendChartRef.value)
  }
  const sorted = [...historyList.value].sort((a, b) => a.recordDate.localeCompare(b.recordDate))
  const dates = sorted.map(d => (d.recordDate || '').slice(5))
  const weights = sorted.map(d => d.weight)
  const bodyFats = sorted.map(d => d.bodyFat)

  const series = [
    {
      name: '体重 (kg)',
      type: 'line',
      smooth: true,
      data: weights,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#3b82f6', width: 2 },
      itemStyle: { color: '#3b82f6' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59,130,246,0.25)' },
          { offset: 1, color: 'rgba(59,130,246,0.02)' }
        ])
      }
    }
  ]

  const hasBodyFat = bodyFats.some(f => f != null)
  if (hasBodyFat) {
    series.push({
      name: '体脂率 (%)',
      type: 'line',
      smooth: true,
      data: bodyFats,
      symbol: 'diamond',
      symbolSize: 6,
      lineStyle: { color: '#ef4444', width: 2 },
      itemStyle: { color: '#ef4444' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(239,68,68,0.2)' },
          { offset: 1, color: 'rgba(239,68,68,0.02)' }
        ])
      }
    })
  }

  trendChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderWidth: 0,
      borderRadius: 12,
      padding: [10, 14]
    },
    legend: {
      data: ['体重 (kg)', '体脂率 (%)'].slice(0, hasBodyFat ? 2 : 1),
      bottom: 0,
      itemWidth: 12,
      itemHeight: 8
    },
    grid: { left: 40, right: 16, top: 20, bottom: 36 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { fontSize: 11, color: '#94a3b8' },
      axisLine: { show: false },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } },
      axisLabel: { fontSize: 11, color: '#94a3b8' }
    },
    series
  })
}

// 表单验证规则
const rules = {
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' }
  ],
  recordDate: [
    { required: true, message: '请选择记录日期', trigger: 'change' }
  ]
}

const disableFutureDate = (time) => time.getTime() > Date.now()

const resetForm = () => {
  form.weight = undefined
  form.height = undefined
  form.bodyFat = undefined
  form.chest = undefined
  form.waist = undefined
  form.hip = undefined
  form.recordDate = ''
  form.remark = ''
  formRef.value?.clearValidate()
}

const handleCreate = async () => {
  try {
    await formRef.value.validate()
    createLoading.value = true

    const submitData = {
      weight: form.weight,
      recordDate: form.recordDate
    }
    if (form.height !== undefined) submitData.height = form.height
    if (form.bodyFat !== undefined) submitData.bodyFat = form.bodyFat
    if (form.chest !== undefined) submitData.chest = form.chest
    if (form.waist !== undefined) submitData.waist = form.waist
    if (form.hip !== undefined) submitData.hip = form.hip
    if (form.remark) submitData.remark = form.remark

    const result = await bodyRecordApi.create(submitData)
    ElMessage.success('身体数据保存成功')
    resetForm()
    latestRecord.value = result
    await fetchHistory()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    createLoading.value = false
  }
}

const handleDateChange = () => {
  fetchHistory()
}

const fetchHistory = async () => {
  historyLoading.value = true
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    historyList.value = await bodyRecordApi.list(params)
    await renderTrendChart()
  } catch (error) {
    console.error('获取历史记录失败:', error)
  } finally {
    historyLoading.value = false
  }
}

const fetchLatest = async () => {
  try {
    latestRecord.value = await bodyRecordApi.getLatest()
  } catch (error) {
    // 首次静默
  }
}

const dayOnly = (dateStr) => dateStr ? dateStr.split('-')[2] : ''
const monthOnly = (dateStr) => dateStr ? dateStr.split('-')[1] : ''

const handleResize = () => {
  window.addEventListener('resize', () => {
    trendChartInstance?.resize()
  })
}

onMounted(async () => {
  pageLoading.value = true
  try {
    await fetchLatest()
    await fetchHistory()
    handleResize()
  } finally {
    pageLoading.value = false
  }
})
</script>

<style scoped>
.body-data-view {
  max-width: 900px;
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

/* 最新数据英雄卡片 */
.latest-data-hero {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 30px;
  padding: 30px;
  color: white;
  background: linear-gradient(135deg, #3b82f6 0%, #2dd4bf 100%);
  box-shadow: 0 15px 30px rgba(59, 130, 246, 0.2);
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at top right, rgba(255,255,255,0.2) 0%, transparent 60%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
}

.hero-date {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255,255,255,0.2);
  padding: 6px 12px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.metric-box {
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: transform 0.3s;
}

.metric-box:hover {
  transform: translateY(-4px);
  background: rgba(255,255,255,0.25);
}

.metric-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.metric-info {
  text-align: center;
}

.m-value {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.2;
}

.m-value small {
  font-size: 12px;
  font-weight: 500;
  opacity: 0.8;
  margin-left: 2px;
}

.m-label {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 4px;
}

/* Tabs */
.tabs-container {
  background: white;
  border-radius: 24px;
  padding: 20px 30px 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}

:deep(.premium-tabs .el-tabs__item) {
  font-size: 16px;
  font-weight: 600;
  height: 48px;
}

:deep(.premium-tabs .el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

/* 录入表单 */
.glass-form-card {
  padding-top: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.full-width-num {
  width: 100%;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.02) !important;
}

.form-row {
  margin-top: 10px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.submit-btn {
  width: 150px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* 趋势图卡片 */
.chart-card-wrapper {
  margin-bottom: 24px;
}
.chart-card {
  background: white;
  border-radius: 20px;
  padding: 20px 16px 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}
.chart-card-header {
  margin-bottom: 12px;
  padding-left: 4px;
}
.chart-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}
.trend-chart {
  height: 220px;
  width: 100%;
}

/* 历史记录时间轴 */
.history-container {
  padding-top: 16px;
}

.history-filter {
  margin-bottom: 24px;
  display: flex;
  justify-content: flex-end;
}

.data-timeline {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.timeline-item {
  display: flex;
  gap: 20px;
  background: #f8fafc;
  border-radius: 16px;
  padding: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #f1f5f9;
}

.timeline-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.04);
  background: white;
  border-color: #e2e8f0;
}

.t-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 70px;
  border-right: 2px dashed #e2e8f0;
  padding-right: 20px;
}

.d-day {
  font-size: 28px;
  font-weight: 800;
  color: var(--primary-color);
  line-height: 1;
}

.d-month {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-top: 4px;
}

.t-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
}

.t-main-stats {
  display: flex;
  gap: 30px;
}

.s-box {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.s-val {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.s-lbl {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.t-sub-stats {
  font-size: 13px;
  color: #64748b;
}

.t-remark {
  font-size: 13px;
  color: #475569;
  background: #f1f5f9;
  padding: 8px 12px;
  border-radius: 8px;
  display: inline-block;
  align-self: flex-start;
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .tabs-container {
    padding: 16px;
  }
  
  .timeline-item {
    flex-direction: column;
    gap: 16px;
  }
  
  .t-date {
    width: 100%;
    flex-direction: row;
    border-right: none;
    border-bottom: 2px dashed #e2e8f0;
    padding-right: 0;
    padding-bottom: 16px;
    justify-content: flex-start;
    gap: 8px;
  }
  
  .d-day { font-size: 24px; }
  .d-month { margin-top: 0; }
  
  .t-main-stats { gap: 16px; flex-wrap: wrap; }
}
</style>
