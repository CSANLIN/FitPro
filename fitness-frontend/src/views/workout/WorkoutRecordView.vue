<template>
  <div class="workout-record-view">
    <div class="page-header">
      <div class="header-text">
        <h2 class="page-title">训练记录</h2>
        <p class="page-subtitle">回望汗水，见证每一次超越</p>
      </div>
      <el-button type="primary" color="#10b981" round class="create-btn" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>记录训练
      </el-button>
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

    <!-- 新建记录弹窗 -->
    <el-dialog v-model="createVisible" title="记录本次训练" width="750px" top="3vh" destroy-on-close class="custom-dialog">
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-position="top">
        <div class="form-grid-2">
          <el-form-item label="训练名称" prop="name">
            <el-input v-model="recordForm.name" placeholder="例如：今日胸背超级组" maxlength="100" />
          </el-form-item>
          <el-form-item label="关联计划">
            <el-select v-model="recordForm.planDayId" placeholder="选择计划中的某一天(可选)" clearable filterable style="width: 100%">
              <el-option-group v-for="plan in planOptions" :key="plan.id" :label="plan.name">
                <el-option
                  v-for="day in plan.days"
                  :key="day.id"
                  :label="plan.name + ' - 周' + '一二三四五六日'[day.dayOfWeek - 1]"
                  :value="day.id"
                />
              </el-option-group>
            </el-select>
          </el-form-item>
        </div>

        <div class="form-grid-2">
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="recordForm.startTime"
              type="datetime"
              placeholder="选填"
              style="width: 100%"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="recordForm.endTime"
              type="datetime"
              placeholder="选填"
              style="width: 100%"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </div>

        <el-form-item label="训练感受/备注">
          <el-input v-model="recordForm.note" type="textarea" :rows="2" placeholder="记录下今天的状态吧..." />
        </el-form-item>

        <el-divider border-style="dashed">训练内容</el-divider>

        <div v-for="(item, index) in recordForm.items" :key="index" class="item-row premium-shadow">
          <div class="item-header">
            <span class="item-label">动作 {{ index + 1 }}</span>
            <el-button type="danger" size="small" plain round @click="removeItem(index)">移除动作</el-button>
          </div>
          
          <div class="item-main-inputs">
            <el-form-item label="选择动作" :prop="`items.${index}.exerciseId`" :rules="recordRules.exerciseId" class="flex-item">
              <el-select v-model="item.exerciseId" placeholder="选择或搜索动作" filterable style="width: 100%">
                <el-option v-for="e in exercises" :key="e.id" :label="e.name" :value="e.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="训练组数" class="flex-item-small">
              <el-input-number v-model="item.groups" :min="1" :max="20" style="width: 100%" controls-position="right" />
            </el-form-item>
          </div>

          <!-- 每组详情 -->
          <div class="sets-container">
            <div v-for="g in item.groups" :key="g" class="set-row">
              <span class="set-label">第 {{ g }} 组</span>
              <el-input-number v-model="item.sets[g - 1].weight" :min="0" :step="0.5" placeholder="重量(kg)" style="width: 120px" controls-position="right" />
              <el-input-number v-model="item.sets[g - 1].reps" :min="1" :max="999" placeholder="次数" style="width: 120px" controls-position="right" />
              <el-checkbox v-model="item.sets[g - 1].completed" border class="checkbox-btn">已完成</el-checkbox>
            </div>
          </div>
        </div>

        <el-button type="primary" plain @click="addItem" class="add-item-btn" round>
          <el-icon><Plus /></el-icon>添加新动作
        </el-button>
      </el-form>

      <template #footer>
        <el-button @click="createVisible = false" round>取消</el-button>
        <el-button type="primary" color="#10b981" :loading="saving" @click="handleCreateRecord" round>保存记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { workoutApi } from '@/api/workout'
import { exerciseApi } from '@/api/exercise'
import { ElMessage } from 'element-plus'
import { Plus, Clock, Timer, Medal } from '@element-plus/icons-vue'

const route = useRoute()

// 数据
const records = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const createVisible = ref(false)
const saving = ref(false)
const currentRecord = ref(null)
const exercises = ref([])
const planOptions = ref([])
const dateRange = ref(null)
const recordFormRef = ref(null)

const query = reactive({
  startDate: null,
  endDate: null,
  pageNum: 1,
  pageSize: 20
})

const recordForm = reactive({
  name: '',
  planDayId: null,
  startTime: null,
  endTime: null,
  note: '',
  items: []
})

const recordRules = {
  name: [{ required: true, message: '请输入训练名称', trigger: 'blur' }],
  exerciseId: [{ required: true, message: '请选择动作', trigger: 'change' }]
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

const fetchExercises = async () => {
  try {
    const res = await exerciseApi.list({ pageNum: 1, pageSize: 999 })
    exercises.value = res.list || []
  } catch (e) {
    console.error('获取动作失败:', e)
  }
}

const fetchPlanOptions = async () => {
  try {
    const res = await workoutApi.listPlans({ status: 'ACTIVE', pageSize: 50 })
    const planList = res.list || []
    const detailPromises = planList.map(p => workoutApi.getPlanDetail(p.id).catch(() => null))
    planOptions.value = (await Promise.all(detailPromises)).filter(Boolean)
  } catch (e) {
    console.error('获取计划选项失败:', e)
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

// 新建记录
const showCreateDialog = () => {
  recordForm.name = ''
  recordForm.planDayId = null
  recordForm.startTime = null
  recordForm.endTime = null
  recordForm.note = ''
  recordForm.items = []
  createVisible.value = true
}

const addItem = () => {
  recordForm.items.push({
    exerciseId: null,
    groups: 4,
    sets: Array.from({ length: 4 }, () => ({
      weight: 0,
      reps: 12,
      completed: false
    }))
  })
}

const removeItem = (index) => {
  recordForm.items.splice(index, 1)
}

const handleCreateRecord = async () => {
  const valid = await recordFormRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const items = []
    recordForm.items.forEach((item) => {
      item.sets.forEach((set, idx) => {
        items.push({
          exerciseId: item.exerciseId,
          setNumber: idx + 1,
          reps: set.reps,
          weight: set.weight > 0 ? set.weight : null,
          completed: set.completed ? 1 : 0
        })
      })
    })

    const data = {
      name: recordForm.name,
      planDayId: recordForm.planDayId || null,
      startTime: recordForm.startTime || null,
      endTime: recordForm.endTime || null,
      note: recordForm.note || null,
      items
    }

    await workoutApi.createRecord(data)
    ElMessage.success('训练记录保存成功')
    createVisible.value = false
    await fetchRecords()
  } catch (e) {
    console.error('创建记录失败:', e)
  } finally {
    saving.value = false
  }
}

watch(() => route.query.planId, (planId) => {
  if (planId) {
    showCreateDialog()
  }
})

onMounted(() => {
  fetchRecords()
  fetchExercises()
  fetchPlanOptions()

  if (route.query.planId) {
    showCreateDialog()
  }
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
