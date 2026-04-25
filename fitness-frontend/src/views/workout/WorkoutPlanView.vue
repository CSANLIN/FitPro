<template>
  <div class="workout-plan-view">
    <div class="page-header">
      <h2>训练计划</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>新建计划
      </el-button>
    </div>

    <!-- 本周统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ weeklyStats.weeklyCount }}</div>
          <div class="stat-label">本周训练次数</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ weeklyStats.weeklyVolume }}kg</div>
          <div class="stat-label">本周总训练量</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 计划列表 -->
    <el-card v-loading="loading" class="plan-card">
      <template #header>
        <div class="card-header">
          <span>我的计划</span>
          <el-select v-model="statusFilter" placeholder="筛选状态" size="small" clearable @change="fetchPlans">
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </div>
      </template>

      <el-empty v-if="!loading && plans.length === 0" description="暂无训练计划" />

      <div v-else class="plan-list">
        <div v-for="plan in plans" :key="plan.id" class="plan-item">
          <div class="plan-info" @click="showPlanDetail(plan.id)">
            <h3 class="plan-name">{{ plan.name }}</h3>
            <p class="plan-desc">{{ plan.description || '暂无描述' }}</p>
            <div class="plan-meta">
              <span>{{ plan.startDate }} ~ {{ plan.endDate || '长期' }}</span>
              <el-tag size="small" :type="statusType(plan.status)">
                {{ statusLabel(plan.status) }}
              </el-tag>
              <span class="day-count">{{ plan.dayCount }}天/周</span>
            </div>
          </div>
          <div class="plan-actions">
            <el-button size="small" @click="showPlanDetail(plan.id)">查看</el-button>
            <el-button
              size="small"
              type="success"
              v-if="plan.status === 'ACTIVE'"
              @click="startWorkout(plan)"
            >开始训练</el-button>
            <el-popconfirm
              title="确定删除该计划？"
              @confirm="handleDeletePlan(plan.id)"
            >
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrap" v-if="total > 0">
          <el-pagination
            v-model:current-page="query.pageNum"
            :page-size="query.pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchPlans"
          />
        </div>
      </div>
    </el-card>

    <!-- 计划详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentPlan?.name"
      width="700px"
      top="5vh"
      destroy-on-close
    >
      <template v-if="currentPlan">
        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="计划名称" :span="2">{{ currentPlan.name }}</el-descriptions-item>
          <el-descriptions-item label="状态" :span="1">
            <el-tag :type="statusType(currentPlan.status)" size="small">
              {{ statusLabel(currentPlan.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="周期" :span="1">
            {{ currentPlan.startDate }} ~ {{ currentPlan.endDate || '长期' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2" v-if="currentPlan.description">
            {{ currentPlan.description }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="day-section" v-for="day in currentPlan.days" :key="day.id">
          <h4 class="day-title">周{{ dayOfWeekLabel(day.dayOfWeek) }} - {{ day.name || '训练日' }}</h4>
          <el-table :data="day.items" stripe size="small" style="width: 100%">
            <el-table-column prop="exerciseName" label="动作" min-width="120" />
            <el-table-column prop="sets" label="组数" width="60" align="center" />
            <el-table-column prop="reps" label="次数" width="60" align="center" />
            <el-table-column prop="weight" label="重量(kg)" width="80" align="center" />
            <el-table-column prop="restSeconds" label="休息(s)" width="80" align="center" />
          </el-table>
        </div>
      </template>
    </el-dialog>

    <!-- 新建计划弹窗 -->
    <el-dialog
      v-model="createVisible"
      title="新建训练计划"
      width="800px"
      top="5vh"
      destroy-on-close
    >
      <el-form ref="formRef" :model="planForm" :rules="formRules" label-width="100px">
        <el-form-item label="计划名称" prop="name">
          <el-input v-model="planForm.name" placeholder="为计划起个名字" maxlength="100" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="planForm.description" type="textarea" :rows="2" placeholder="计划描述(可选)" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="planForm.startDate" type="date" placeholder="选择开始日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="planForm.endDate" type="date" placeholder="选填" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>训练日设置</el-divider>

        <div v-for="(day, dayIndex) in planForm.days" :key="dayIndex" class="day-block">
          <div class="day-header">
            <span class="day-label">训练日 {{ dayIndex + 1 }}</span>
            <el-button type="danger" size="small" text @click="removeDay(dayIndex)">移除</el-button>
          </div>
          <el-row :gutter="16" class="day-row">
            <el-col :span="6">
              <el-form-item label="星期" :prop="`days.${dayIndex}.dayOfWeek`" :rules="formRules.dayOfWeek">
                <el-select v-model="day.dayOfWeek" placeholder="选择" style="width: 100%">
                  <el-option v-for="d in 7" :key="d" :label="'周' + '一二三四五六日'[d-1]" :value="d" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="名称" :prop="`days.${dayIndex}.name`">
                <el-input v-model="day.name" placeholder="训练日名称(可选)" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="模板" :prop="`days.${dayIndex}.templateId`">
                <el-select v-model="day.templateId" placeholder="选模板" clearable style="width: 100%">
                  <el-option v-for="t in templates" :key="t.id" :label="t.name" :value="t.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <div class="items-section">
            <div class="items-header">
              <span>训练动作</span>
              <el-button size="small" text type="primary" @click="addDayItem(dayIndex)">+ 添加动作</el-button>
            </div>
            <el-table :data="day.items" stripe size="small" style="width: 100%">
              <el-table-column label="动作" width="180">
                <template #default="{ row }">
                  <el-select v-model="row.exerciseId" placeholder="选择动作" filterable style="width: 160px">
                    <el-option v-for="e in exercises" :key="e.id" :label="e.name" :value="e.id" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="sets" label="组数" width="80">
                <template #default="{ row }">
                  <el-input-number v-model="row.sets" :min="1" :max="20" size="small" controls-position="right" style="width: 80px" />
                </template>
              </el-table-column>
              <el-table-column prop="reps" label="次数" width="80">
                <template #default="{ row }">
                  <el-input-number v-model="row.reps" :min="1" :max="999" size="small" controls-position="right" style="width: 80px" />
                </template>
              </el-table-column>
              <el-table-column prop="weight" label="重量(kg)" width="90">
                <template #default="{ row }">
                  <el-input-number v-model="row.weight" :min="0" :step="0.5" size="small" controls-position="right" style="width: 90px" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" text @click="removeDayItem(dayIndex, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <el-button type="primary" plain @click="addDay" class="add-day-btn">
          <el-icon><Plus /></el-icon>添加训练日
        </el-button>
      </el-form>

      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleCreatePlan">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { workoutApi } from '@/api/workout'
import { exerciseApi } from '@/api/exercise'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()

// 数据
const plans = ref([])
const total = ref(0)
const loading = ref(false)
const statusFilter = ref('ACTIVE')
const detailVisible = ref(false)
const createVisible = ref(false)
const saving = ref(false)
const currentPlan = ref(null)
const templates = ref([])
const exercises = ref([])
const weeklyStats = ref({ weeklyCount: 0, weeklyVolume: 0 })
const formRef = ref(null)

const query = reactive({
  keyword: '',
  status: 'ACTIVE',
  pageNum: 1,
  pageSize: 20
})

const planForm = reactive({
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  days: []
})

const formRules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }]
}

// 状态标签
const statusLabel = (val) => ({ ACTIVE: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }[val] || val)
const statusType = (val) => ({ ACTIVE: 'success', COMPLETED: 'info', CANCELLED: 'danger' }[val] || 'info')
const dayOfWeekLabel = (val) => '一二三四五六日'[val - 1] || val

// 加载数据
const fetchPlans = async () => {
  loading.value = true
  try {
    query.status = statusFilter.value || undefined
    const res = await workoutApi.listPlans({ ...query })
    plans.value = res.list
    total.value = res.total
  } catch (e) {
    console.error('获取计划失败:', e)
  } finally {
    loading.value = false
  }
}

const fetchWeeklyStats = async () => {
  try {
    weeklyStats.value = await workoutApi.weeklyStats()
  } catch (e) {
    console.error('获取周统计失败:', e)
  }
}

const fetchTemplates = async () => {
  try {
    templates.value = await workoutApi.listTemplates()
  } catch (e) {
    console.error('获取模板失败:', e)
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

// 计划详情
const showPlanDetail = async (id) => {
  try {
    currentPlan.value = await workoutApi.getPlanDetail(id)
    detailVisible.value = true
  } catch (e) {
    console.error('获取计划详情失败:', e)
  }
}

// 开始训练
const startWorkout = (plan) => {
  router.push({ name: 'AppWorkoutRecord', query: { planId: plan.id } })
}

// 删除计划
const handleDeletePlan = async (id) => {
  try {
    await workoutApi.deletePlan(id)
    ElMessage.success('计划已删除')
    await fetchPlans()
  } catch (e) {
    console.error('删除计划失败:', e)
  }
}

// 新建计划弹窗
const showCreateDialog = () => {
  planForm.name = ''
  planForm.description = ''
  planForm.startDate = ''
  planForm.endDate = ''
  planForm.days = []
  createVisible.value = true
}

// 添加训练日
const addDay = () => {
  planForm.days.push({
    dayOfWeek: planForm.days.length + 1,
    name: '',
    templateId: null,
    items: []
  })
}

const removeDay = (index) => {
  planForm.days.splice(index, 1)
}

const addDayItem = (dayIndex) => {
  planForm.days[dayIndex].items.push({
    exerciseId: null,
    sets: 4,
    reps: 12,
    weight: 0,
    restSeconds: 60,
    sortOrder: planForm.days[dayIndex].items.length
  })
}

const removeDayItem = (dayIndex, itemIndex) => {
  planForm.days[dayIndex].items.splice(itemIndex, 1)
}

const handleCreatePlan = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const data = {
      name: planForm.name,
      description: planForm.description,
      startDate: planForm.startDate,
      endDate: planForm.endDate || null,
      days: planForm.days.map(day => ({
        dayOfWeek: day.dayOfWeek,
        name: day.name || null,
        templateId: day.templateId || null,
        items: day.items.map((item, i) => ({
          exerciseId: item.exerciseId,
          sets: item.sets,
          reps: item.reps,
          weight: item.weight || null,
          restSeconds: item.restSeconds,
          sortOrder: i
        }))
      }))
    }
    await workoutApi.createPlan(data)
    ElMessage.success('计划创建成功')
    createVisible.value = false
    await fetchPlans()
  } catch (e) {
    console.error('创建计划失败:', e)
  } finally {
    saving.value = false
  }
}

// 初始化
onMounted(() => {
  fetchPlans()
  fetchWeeklyStats()
  fetchTemplates()
  fetchExercises()
})
</script>

<style scoped>
.workout-plan-view {
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

.plan-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.plan-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  transition: background-color 0.2s;
}

.plan-item:hover {
  background-color: var(--el-fill-color-light);
}

.plan-info {
  flex: 1;
  cursor: pointer;
}

.plan-name {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
}

.plan-desc {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.plan-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.day-count {
  background: var(--el-fill-color);
  padding: 2px 8px;
  border-radius: 4px;
}

.plan-actions {
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

/* 详情弹窗 */
.detail-desc {
  margin-bottom: 24px;
}

.day-section {
  margin-bottom: 20px;
}

.day-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--el-color-primary);
}

/* 新建弹窗 */
.day-block {
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.day-label {
  font-weight: 600;
  font-size: 14px;
}

.day-row {
  margin-bottom: 8px;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.add-day-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
