<template>
  <div class="workout-record-view">
    <div class="page-header">
      <h2>训练记录</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>记录训练
      </el-button>
    </div>

    <!-- 记录列表和创建共用容器 -->
    <el-card v-loading="loading" class="record-card">
      <template #header>
        <div class="card-header">
          <span>历史记录</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="YYYY-MM-DD"
            @change="fetchRecords"
          />
        </div>
      </template>

      <el-empty v-if="!loading && records.length === 0" description="暂无训练记录，开始你的第一次训练吧！" />

      <div v-else class="record-list">
        <div v-for="record in records" :key="record.id" class="record-item">
          <div class="record-info" @click="showRecordDetail(record.id)">
            <h3 class="record-name">{{ record.name }}</h3>
            <div class="record-meta">
              <span>{{ record.startTime?.substring(0, 16) }}</span>
              <span v-if="record.durationMinutes">时长: {{ record.durationMinutes }}分钟</span>
              <span v-if="record.totalVolume">训练量: {{ record.totalVolume }}kg</span>
            </div>
          </div>
          <div class="record-actions">
            <el-button size="small" @click="showRecordDetail(record.id)">详情</el-button>
            <el-popconfirm title="确定删除该记录？" @confirm="handleDeleteRecord(record.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>

        <div class="pagination-wrap" v-if="total > 0">
          <el-pagination
            v-model:current-page="query.pageNum"
            :page-size="query.pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchRecords"
          />
        </div>
      </div>
    </el-card>

    <!-- 记录详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentRecord?.name" width="650px" top="5vh" destroy-on-close>
      <template v-if="currentRecord">
        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="训练名称" :span="2">{{ currentRecord.name }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentRecord.startTime?.substring(0, 19) }}</el-descriptions-item>
          <el-descriptions-item label="时长">{{ currentRecord.durationMinutes }}分钟</el-descriptions-item>
          <el-descriptions-item label="总训练量">{{ currentRecord.totalVolume }}kg</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="currentRecord.note">{{ currentRecord.note }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="items-title">训练组详情</h4>
        <el-table :data="currentRecord.items" stripe size="small" style="width: 100%">
          <el-table-column prop="exerciseName" label="动作" min-width="120" />
          <el-table-column prop="setNumber" label="组号" width="60" align="center" />
          <el-table-column prop="weight" label="重量(kg)" width="80" align="center" />
          <el-table-column prop="reps" label="次数" width="60" align="center" />
          <el-table-column prop="durationSeconds" label="时长(s)" width="80" align="center" />
          <el-table-column label="完成" width="60" align="center">
            <template #default="{ row }">
              <el-tag :type="row.completed ? 'success' : 'danger'" size="small">
                {{ row.completed ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>

    <!-- 新建记录弹窗 -->
    <el-dialog v-model="createVisible" title="记录训练" width="700px" top="3vh" destroy-on-close>
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="100px">
        <el-form-item label="训练名称" prop="name">
          <el-input v-model="recordForm.name" placeholder="例如：今日胸肌训练" maxlength="100" />
        </el-form-item>
        <el-form-item label="关联计划">
          <el-select v-model="recordForm.planDayId" placeholder="可选" clearable filterable style="width: 100%">
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
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="recordForm.startTime"
                type="datetime"
                placeholder="选填"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="recordForm.endTime"
                type="datetime"
                placeholder="选填"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="recordForm.note" type="textarea" :rows="2" placeholder="训练感受(可选)" />
        </el-form-item>

        <el-divider>训练组</el-divider>

        <div v-for="(item, index) in recordForm.items" :key="index" class="item-row">
          <div class="item-header">
            <span class="item-label">第 {{ index + 1 }} 个动作</span>
            <el-button type="danger" size="small" text @click="removeItem(index)">移除</el-button>
          </div>
          <el-row :gutter="12">
            <el-col :span="6">
              <el-form-item label="动作" :prop="`items.${index}.exerciseId`" :rules="recordRules.exerciseId">
                <el-select v-model="item.exerciseId" placeholder="选择动作" filterable style="width: 100%">
                  <el-option v-for="e in exercises" :key="e.id" :label="e.name" :value="e.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="组数">
                <el-input-number v-model="item.groups" :min="1" :max="20" size="small" style="width: 100%" controls-position="right" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 每组详情 -->
          <div v-for="g in item.groups" :key="g" class="set-row">
            <span class="set-label">第{{ g }}组</span>
            <el-input-number v-model="item.sets[g - 1].weight" :min="0" :step="0.5" size="small" placeholder="重量kg" style="width: 110px" controls-position="right" />
            <el-input-number v-model="item.sets[g - 1].reps" :min="1" :max="999" size="small" placeholder="次数" style="width: 100px" controls-position="right" />
            <el-checkbox v-model="item.sets[g - 1].completed">完成</el-checkbox>
          </div>
        </div>

        <el-button type="primary" plain @click="addItem" class="add-item-btn">
          <el-icon><Plus /></el-icon>添加动作
        </el-button>
      </el-form>

      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleCreateRecord">保存记录</el-button>
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
import { Plus } from '@element-plus/icons-vue'

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
    // 只获取进行中的计划
    const res = await workoutApi.listPlans({ status: 'ACTIVE', pageSize: 50 })
    const planList = res.list || []
    // 加载每个计划的详情（获取训练日）
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
  recordForm.name = '今日训练'
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
      completed: true
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
    // 将表单数据转换为后端需要的格式
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

// 如果路由带了 planId 参数，自动填充
watch(() => route.query.planId, (planId) => {
  if (planId) {
    showCreateDialog()
  }
})

// 初始化
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
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.record-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  transition: background-color 0.2s;
}

.record-item:hover {
  background-color: var(--el-fill-color-light);
}

.record-info {
  flex: 1;
  cursor: pointer;
}

.record-name {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 600;
}

.record-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
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
  margin-top: 16px;
  padding: 8px 0;
}

/* 详情 */
.detail-desc {
  margin-bottom: 20px;
}

.items-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 8px;
}

/* 新建弹窗 */
.item-row {
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-label {
  font-weight: 600;
  font-size: 14px;
}

.set-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}

.set-label {
  width: 40px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.add-item-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
