<template>
  <div class="workout-record-view">
    <div class="page-header">
      <div class="header-text">
        <h2 class="page-title">训练记录</h2>
        <p class="page-subtitle">回望汗水，见证每一次超越</p>
      </div>
    </div>

    <!-- 本周训练概览 -->
    <div class="weekly-stats-row" v-if="weeklyStats">
      <div class="stat-pill">
        <div class="pill-icon yellow"><el-icon><Calendar /></el-icon></div>
        <div class="pill-info">
          <div class="pill-val">{{ weeklyStats.weeklyCount || 0 }}</div>
          <div class="pill-lbl">本周训练次数</div>
        </div>
      </div>
      <div class="stat-pill">
        <div class="pill-icon green"><el-icon><Medal /></el-icon></div>
        <div class="pill-info">
          <div class="pill-val">{{ weeklyStats.weeklyVolume || 0 }}<small>kg</small></div>
          <div class="pill-lbl">本周总训练量</div>
        </div>
      </div>
      <div class="stat-pill">
        <div class="pill-icon blue"><el-icon><TrendCharts /></el-icon></div>
        <div class="pill-info">
          <div class="pill-val">{{ weeklyStats.avgVolume || 0 }}<small>kg</small></div>
          <div class="pill-lbl">次均训练量</div>
        </div>
      </div>
    </div>

    <!-- 记录列表和筛选区域 -->
    <div class="record-container">
      <div class="filter-bar">
        <div class="filter-title">历史足迹</div>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="fetchRecords"
          class="glass-picker"
        />
      </div>

      <div v-loading="loading" class="record-list-wrapper">
        <el-empty v-if="!loading && records.length === 0" description="暂无训练记录，开始你的第一次训练吧！" class="premium-empty" />

        <div v-else class="record-list">
          <div v-for="record in records" :key="record.id" class="premium-record-card">
            <div class="record-content" @click="showRecordDetail(record.id)">
              <div class="record-left">
                <div class="record-date-block">
                  <div class="date-day">{{ dayOnly(record.startTime) }}</div>
                  <div class="date-month">{{ monthOnly(record.startTime) }}月</div>
                </div>
              </div>
              
              <div class="record-main">
                <h3 class="record-name">{{ record.name }}</h3>
                <div class="record-meta">
                  <div class="meta-item"><el-icon><Clock /></el-icon> {{ timeOnly(record.startTime) }}</div>
                  <div class="meta-item" v-if="record.durationMinutes"><el-icon><Timer /></el-icon> {{ record.durationMinutes }} Min</div>
                  <div class="meta-item" v-if="record.totalVolume">
                    <el-icon><Medal /></el-icon> {{ record.totalVolume }} kg
                  </div>
                </div>
              </div>
            </div>

            <div class="record-actions">
              <el-button round size="small" @click="showRecordDetail(record.id)">详情</el-button>
              <el-popconfirm title="确定删除该记录？" @confirm="handleDeleteRecord(record.id)">
                <template #reference>
                  <el-button round size="small" type="danger" plain>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>

          <div class="pagination-wrap" v-if="total > 0">
            <el-pagination
              v-model:current-page="query.pageNum"
              :page-size="query.pageSize"
              :total="total"
              background
              layout="prev, pager, next"
              @current-change="fetchRecords"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 记录详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentRecord?.name" width="700px" top="5vh" destroy-on-close class="custom-dialog">
      <template v-if="currentRecord">
        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="训练名称" :span="2">{{ currentRecord.name }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentRecord.startTime?.substring(0, 19) }}</el-descriptions-item>
          <el-descriptions-item label="时长">{{ currentRecord.durationMinutes }}分钟</el-descriptions-item>
          <el-descriptions-item label="总训练量">{{ currentRecord.totalVolume }}kg</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="currentRecord.note">{{ currentRecord.note }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="items-title">训练动作详情</h4>
        <el-table :data="currentRecord.items" stripe size="small" style="width: 100%" class="custom-table">
          <el-table-column prop="exerciseName" label="动作" min-width="120" />
          <el-table-column prop="setNumber" label="组号" width="60" align="center" />
          <el-table-column prop="weight" label="重量(kg)" width="80" align="center" />
          <el-table-column prop="reps" label="次数" width="60" align="center" />
          <el-table-column prop="durationSeconds" label="时长(s)" width="80" align="center" />
          <el-table-column label="完成" width="60" align="center">
            <template #default="{ row }">
              <el-tag :type="row.completed ? 'success' : 'danger'" size="small" effect="dark" round>
                {{ row.completed ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { workoutApi } from '@/api/workout'
import { ElMessage } from 'element-plus'
import { Clock, Timer, Medal, TrendCharts, Calendar } from '@element-plus/icons-vue'

// 数据
const records = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const weeklyStats = ref(null)
const currentRecord = ref(null)
const dateRange = ref(null)

const query = {
  startDate: null,
  endDate: null,
  pageNum: 1,
  pageSize: 20
}

// 时间处理辅助函数
const dayOnly = (timeStr) => {
  if (!timeStr) return '--'
  return timeStr.split(' ')[0].split('-')[2]
}
const monthOnly = (timeStr) => {
  if (!timeStr) return '--'
  return timeStr.split(' ')[0].split('-')[1]
}
const timeOnly = (timeStr) => {
  if (!timeStr) return '--'
  return timeStr.substring(11, 16)
}

// 获取本周统计
const fetchWeeklyStats = async () => {
  try {
    const res = await workoutApi.weeklyStats()
    if (res) {
      const avg = res.weeklyCount > 0 ? Math.round((res.weeklyVolume || 0) / res.weeklyCount) : 0
      weeklyStats.value = { ...res, avgVolume: avg }
    }
  } catch (e) {
    console.warn('获取周统计失败:', e)
  }
}

// 加载数据
const fetchRecords = async () => {
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    } else {
      params.startDate = null
      params.endDate = null
    }
    const res = await workoutApi.listRecords(params)
    records.value = res.list
    total.value = res.total
  } catch (e) {
    console.error('获取记录失败:', e)
  } finally {
    loading.value = false
  }
}

// 记录详情
const showRecordDetail = async (id) => {
  try {
    currentRecord.value = await workoutApi.getRecordDetail(id)
    detailVisible.value = true
  } catch (e) {
    console.error('获取记录详情失败:', e)
  }
}

// 删除记录
const handleDeleteRecord = async (id) => {
  try {
    await workoutApi.deleteRecord(id)
    ElMessage.success('记录已删除')
    await fetchRecords()
  } catch (e) {
    console.error('删除记录失败:', e)
  }
}

onMounted(() => {
  fetchRecords()
  fetchWeeklyStats()
})

onActivated(() => {
  fetchRecords()
  fetchWeeklyStats()
})
</script>

<style scoped>
.workout-record-view {
  max-width: 900px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.create-btn {
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

/* 本周训练概览 */
.weekly-stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.stat-pill {
  flex: 1;
  background: white;
  border-radius: 20px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}
.pill-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.pill-icon.yellow { background: #fef3c7; color: #d97706; }
.pill-icon.green { background: #d1fae5; color: #059669; }
.pill-icon.blue { background: #dbeafe; color: #2563eb; }
.pill-val {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.2;
}
.pill-val small {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}
.pill-lbl {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.premium-record-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
}

.premium-record-card:hover {
  transform: translateX(4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  border-color: #e2e8f0;
}

.record-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
}

.record-date-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  width: 70px;
  height: 70px;
  border-radius: 16px;
}

.date-day {
  font-size: 24px;
  font-weight: 800;
  color: var(--primary-color);
  line-height: 1;
}

.date-month {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  margin-top: 4px;
}

.record-main {
  flex: 1;
}

.record-name {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.record-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 20px;
}

.record-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  margin-left: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 详情 */
.detail-desc {
  margin-bottom: 24px;
}

.items-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 12px;
  color: #1e293b;
}

/* 新建弹窗 */
.form-grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.item-row {
  background: #f8fafc;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #f1f5f9;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-label {
  font-weight: 700;
  font-size: 16px;
  color: #0f172a;
}

.item-main-inputs {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.flex-item {
  flex: 2;
}

.flex-item-small {
  flex: 1;
}

.sets-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: white;
  padding: 16px;
  border-radius: 12px;
}

.set-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.set-label {
  width: 50px;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
}

.checkbox-btn {
  margin-left: auto;
}

.add-item-btn {
  width: 100%;
  margin-top: 8px;
  height: 48px;
}

@media (max-width: 768px) {
  .premium-record-card {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .record-actions {
    width: 100%;
    margin-left: 0;
    margin-top: 16px;
    justify-content: flex-end;
  }
  
  .form-grid-2 {
    grid-template-columns: 1fr;
  }
  
  .item-main-inputs {
    flex-direction: column;
  }
  
  .set-row {
    flex-wrap: wrap;
  }
  
  .checkbox-btn {
    margin-left: 0;
  }
}
</style>
