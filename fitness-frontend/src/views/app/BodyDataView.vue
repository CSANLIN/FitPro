<template>
  <div class="body-data-view">
    <h1>身体数据</h1>

    <!-- 最新数据卡片 -->
    <el-card v-if="latestRecord" class="latest-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最新数据</span>
          <span class="record-date">{{ latestRecord.recordDate }}</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6" class="metric-item">
          <div class="metric-label">体重</div>
          <div class="metric-value">{{ latestRecord.weight || '-' }} <small>kg</small></div>
        </el-col>
        <el-col :span="6" class="metric-item">
          <div class="metric-label">BMI</div>
          <div class="metric-value">{{ latestRecord.bmi || '-' }}</div>
        </el-col>
        <el-col :span="6" class="metric-item">
          <div class="metric-label">体脂率</div>
          <div class="metric-value">{{ latestRecord.bodyFat || '-' }} <small>%</small></div>
        </el-col>
        <el-col :span="6" class="metric-item">
          <div class="metric-label">身高</div>
          <div class="metric-value">{{ latestRecord.height || '-' }} <small>cm</small></div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 无数据提示 -->
    <el-empty v-else description="暂无身体数据记录" :image-size="120" class="empty-data" />

    <!-- 标签页：录入 + 历史 -->
    <el-tabs v-model="activeTab" class="data-tabs">
      <!-- 录入数据 -->
      <el-tab-pane label="录入数据" name="create">
        <el-card class="create-card">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="体重" prop="weight">
                  <el-input-number
                    v-model="form.weight"
                    :min="20"
                    :max="300"
                    :precision="1"
                    :step="0.1"
                    style="width: 100%"
                    placeholder="kg"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身高" prop="height">
                  <el-input-number
                    v-model="form.height"
                    :min="100"
                    :max="250"
                    :precision="1"
                    :step="0.5"
                    style="width: 100%"
                    placeholder="cm"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="体脂率" prop="bodyFat">
                  <el-input-number
                    v-model="form.bodyFat"
                    :min="3"
                    :max="60"
                    :precision="1"
                    :step="0.1"
                    style="width: 100%"
                    placeholder="%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="胸围" prop="chest">
                  <el-input-number
                    v-model="form.chest"
                    :min="50"
                    :max="200"
                    :precision="1"
                    :step="0.5"
                    style="width: 100%"
                    placeholder="cm"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="腰围" prop="waist">
                  <el-input-number
                    v-model="form.waist"
                    :min="40"
                    :max="200"
                    :precision="1"
                    :step="0.5"
                    style="width: 100%"
                    placeholder="cm"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="臀围" prop="hip">
                  <el-input-number
                    v-model="form.hip"
                    :min="50"
                    :max="200"
                    :precision="1"
                    :step="0.5"
                    style="width: 100%"
                    placeholder="cm"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="记录日期" prop="recordDate">
                  <el-date-picker
                    v-model="form.recordDate"
                    type="date"
                    placeholder="选择日期"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                    :disabled-date="disableFutureDate"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                    :rows="2"
                    placeholder="例如：早晨空腹测量"
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" @click="handleCreate" :loading="createLoading">
                保存记录
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 历史记录 -->
      <el-tab-pane label="历史记录" name="history">
        <el-card class="history-card">
          <template #header>
            <div class="card-header">
              <span>历史记录</span>
              <div class="date-filter">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  @change="handleDateChange"
                  clearable
                />
              </div>
            </div>
          </template>

          <el-table
            v-if="historyList.length > 0"
            :data="historyList"
            stripe
            style="width: 100%"
            :default-sort="{ prop: 'recordDate', order: 'descending' }"
          >
            <el-table-column prop="recordDate" label="日期" width="120" sortable />
            <el-table-column prop="weight" label="体重(kg)" width="100" />
            <el-table-column prop="bmi" label="BMI" width="80" />
            <el-table-column prop="bodyFat" label="体脂率(%)" width="100" />
            <el-table-column prop="height" label="身高(cm)" width="100" />
            <el-table-column prop="chest" label="胸围(cm)" width="100" />
            <el-table-column prop="waist" label="腰围(cm)" width="100" />
            <el-table-column prop="hip" label="臀围(cm)" width="100" />
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          </el-table>

          <el-empty v-else description="暂无历史记录" :image-size="100" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { bodyRecordApi } from '@/api/bodyRecord'
import { ElMessage } from 'element-plus'

// 标签页
const activeTab = ref('create')

// 表单引用
const formRef = ref()

// 加载状态
const createLoading = ref(false)

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

// 表单验证规则
const rules = {
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' }
  ],
  recordDate: [
    { required: true, message: '请选择记录日期', trigger: 'change' }
  ]
}

// 禁止选择未来日期
const disableFutureDate = (time) => {
  return time.getTime() > Date.now()
}

// 重置表单
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

// 录入身体数据
const handleCreate = async () => {
  try {
    await formRef.value.validate()
    createLoading.value = true

    // 构建提交数据，只提交有值的字段
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

    // 重置表单
    resetForm()

    // 刷新最新数据
    latestRecord.value = result

    // 刷新历史记录
    await fetchHistory()
  } catch (error) {
    console.error('保存身体数据失败:', error)
    ElMessage.error(error.response?.data?.message || '保存失败，请稍后重试')
  } finally {
    createLoading.value = false
  }
}

// 日期筛选变化
const handleDateChange = () => {
  fetchHistory()
}

// 获取历史记录
const fetchHistory = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    historyList.value = await bodyRecordApi.list(params)
  } catch (error) {
    console.error('获取历史记录失败:', error)
  }
}

// 获取最新数据
const fetchLatest = async () => {
  try {
    latestRecord.value = await bodyRecordApi.getLatest()
  } catch (error) {
    console.error('获取最新身体数据失败:', error)
  }
}

// 页面加载时初始化
onMounted(async () => {
  await fetchLatest()
  await fetchHistory()
})
</script>

<style scoped>
.body-data-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.body-data-view h1 {
  margin-bottom: 20px;
  font-size: 24px;
  color: var(--text-primary);
}

.latest-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-date {
  font-size: 14px;
  color: var(--text-secondary);
}

.metric-item {
  text-align: center;
  padding: 10px 0;
}

.metric-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color);
}

.metric-value small {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-secondary);
}

.data-tabs {
  margin-top: 10px;
}

.create-card,
.history-card {
  margin-top: 10px;
}

.date-filter {
  width: 280px;
}

.empty-data {
  margin: 40px 0;
}

@media (max-width: 768px) {
  .body-data-view {
    padding: 12px;
  }

  .metric-item {
    padding: 8px 0;
  }

  .metric-value {
    font-size: 20px;
  }

  .date-filter {
    width: 200px;
  }
}
</style>
