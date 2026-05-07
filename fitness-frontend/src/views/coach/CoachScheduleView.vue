<template>
  <div class="coach-schedule">
    <!-- 顶部操作栏 -->
    <div class="schedule-header">
      <div class="date-filter">
        <el-radio-group v-model="dateRange" size="default" @change="onDateRangeChange">
          <el-radio-button value="today">今天</el-radio-button>
          <el-radio-button value="week">本周</el-radio-button>
          <el-radio-button value="month">本月</el-radio-button>
          <el-radio-button value="custom">自定义</el-radio-button>
        </el-radio-group>
        <el-date-picker
          v-if="dateRange === 'custom'"
          v-model="customRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="custom-date-picker"
          @change="fetchSchedules"
        />
      </div>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>创建排课
      </el-button>
    </div>

    <!-- 排课列表 -->
    <div v-loading="loading" class="schedule-list">
      <el-empty v-if="!loading && schedules.length === 0" description="暂无排课" />
      <el-row v-else :gutter="20">
        <el-col v-for="item in schedules" :key="item.id" :xs="24" :sm="12" :lg="8">
          <el-card class="schedule-card" shadow="hover">
            <div class="card-header">
              <div class="course-info">
                <span class="course-name">{{ item.courseName }}</span>
                <el-tag :type="statusTag(item.status)" size="small">
                  {{ statusLabel(item.status) }}
                </el-tag>
              </div>
            </div>
            <div class="card-body">
              <div class="info-row">
                <el-icon><Calendar /></el-icon>
                <span>{{ item.scheduleDate }} {{ item.startTime }} - {{ item.endTime }}</span>
              </div>
              <div class="info-row">
                <el-icon><Location /></el-icon>
                <span>{{ item.location || '未指定地点' }}</span>
              </div>
              <div class="info-row">
                <el-icon><User /></el-icon>
                <span>{{ item.courseType }}</span>
              </div>
              <div class="capacity-bar">
                <span class="capacity-label">预约人数</span>
                <el-progress
                  :percentage="capacityPercent(item)"
                  :status="item.currentCount >= item.maxCapacity ? 'exception' : 'success'"
                />
                <span class="capacity-text">{{ item.currentCount }}/{{ item.maxCapacity }}</span>
              </div>
            </div>
            <div class="card-actions">
              <el-button size="small" @click="viewBookings(item)">查看预约</el-button>
              <el-button
                v-if="item.status === 'UPCOMING'"
                size="small"
                type="danger"
                plain
                @click="cancelSchedule(item)"
              >取消排课</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 创建排课弹窗 -->
    <el-dialog v-model="showCreateDialog" title="创建排课" width="520px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="选择课程" prop="courseId">
          <el-select v-model="createForm.courseId" placeholder="请选择课程" filterable style="width: 100%">
            <el-option
              v-for="c in courseList"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="scheduleDate">
          <el-date-picker
            v-model="createForm.scheduleDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="(t) => t < new Date()"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="createForm.startTime"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="createForm.endTime"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地点" prop="location">
          <el-input v-model="createForm.location" placeholder="上课地点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确认创建</el-button>
      </template>
    </el-dialog>

    <!-- 查看预约学员弹窗 -->
    <el-dialog v-model="showBookingDialog" :title="'预约学员 - ' + (currentSchedule?.courseName || '')" width="700px">
      <el-table v-loading="bookingLoading" :data="bookingList" stripe>
        <el-table-column label="学员" min-width="160">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" :src="row.userAvatar" />
              <span>{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="bookedAt" label="预约时间" min-width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'BOOKED' ? 'warning' : row.status === 'ATTENDED' ? 'success' : 'info'" size="small">
              {{ row.status === 'BOOKED' ? '已预约' : row.status === 'ATTENDED' ? '已出席' : row.status === 'ABSENT' ? '缺席' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showBookingDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { coachApi } from '@/api/coach'
import { courseApi } from '@/api/course'
import { Plus, Calendar, Location, User } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const schedules = ref([])
const courseList = ref([])
const showCreateDialog = ref(false)
const showBookingDialog = ref(false)
const bookingList = ref([])
const bookingLoading = ref(false)
const currentSchedule = ref(null)
const createFormRef = ref(null)

const dateRange = ref('week')
const customRange = ref(null)

// 计算日期范围
const dateParams = computed(() => {
  const now = new Date()
  const fmt = (d) => d.toISOString().slice(0, 10)

  if (dateRange.value === 'today') {
    return { startDate: fmt(now), endDate: fmt(now) }
  }
  if (dateRange.value === 'week') {
    const start = new Date(now)
    start.setDate(start.getDate() - start.getDay() + 1)
    const end = new Date(now)
    end.setDate(end.getDate() + (7 - end.getDay()))
    return { startDate: fmt(start), endDate: fmt(end) }
  }
  if (dateRange.value === 'month') {
    const start = new Date(now.getFullYear(), now.getMonth(), 1)
    const end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
    return { startDate: fmt(start), endDate: fmt(end) }
  }
  if (customRange.value) {
    return { startDate: customRange.value[0], endDate: customRange.value[1] }
  }
  return {}
})

const createForm = ref({
  courseId: null,
  scheduleDate: '',
  startTime: '',
  endTime: '',
  location: ''
})

const createRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 状态标签
const statusTag = (status) => {
  const map = { UPCOMING: 'warning', ONGOING: 'success', FINISHED: '', CANCELLED: 'danger' }
  return map[status] || 'info'
}

const statusLabel = (status) => {
  const map = { UPCOMING: '待开始', ONGOING: '进行中', FINISHED: '已结束', CANCELLED: '已取消' }
  return map[status] || status
}

const capacityPercent = (item) => {
  return item.maxCapacity > 0 ? Math.round((item.currentCount / item.maxCapacity) * 100) : 0
}

const onDateRangeChange = () => {
  if (dateRange.value !== 'custom') {
    fetchSchedules()
  }
}

// 获取排课列表
const fetchSchedules = async () => {
  loading.value = true
  try {
    const params = {}
    if (dateParams.value.startDate) params.startDate = dateParams.value.startDate
    if (dateParams.value.endDate) params.endDate = dateParams.value.endDate
    schedules.value = await coachApi.listSchedules(params)
  } catch (e) {
    console.error('获取排课列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 获取课程列表
const fetchCourses = async () => {
  try {
    courseList.value = await courseApi.list({ all: true })
  } catch (e) {
    console.error('获取课程列表失败:', e)
  }
}

// 创建排课
const handleCreate = async () => {
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = { ...createForm.value }
    await coachApi.createSchedule(data)
    ElMessage.success('排课创建成功')
    showCreateDialog.value = false
    createFormRef.value.resetFields()
    fetchSchedules()
  } catch (e) {
    console.error('创建排课失败:', e)
  } finally {
    submitting.value = false
  }
}

// 取消排课
const cancelSchedule = async (item) => {
  try {
    await ElMessageBox.confirm(`确定取消 ${item.scheduleDate} ${item.startTime} 的「${item.courseName}」排课吗？`, '确认取消', {
      type: 'warning',
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想'
    })
    await coachApi.cancelSchedule(item.id)
    ElMessage.success('排课已取消')
    fetchSchedules()
  } catch (e) {
    if (e !== 'cancel') console.error('取消排课失败:', e)
  }
}

// 查看预约
const viewBookings = async (item) => {
  currentSchedule.value = item
  showBookingDialog.value = true
  bookingLoading.value = true
  try {
    bookingList.value = await coachApi.listBookings(item.id)
  } catch (e) {
    console.error('获取预约列表失败:', e)
  } finally {
    bookingLoading.value = false
  }
}

onMounted(() => {
  fetchSchedules()
  fetchCourses()
})
</script>

<style scoped lang="scss">
.coach-schedule {
  padding: 0;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.date-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.custom-date-picker {
  width: 240px;
}

.schedule-card {
  margin-bottom: 20px;

  .card-header {
    margin-bottom: 16px;
  }

  .course-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .course-name {
    font-size: 16px;
    font-weight: 600;
  }

  .card-body {
    .info-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      font-size: 14px;
      color: var(--text-secondary);
    }
  }

  .capacity-bar {
    margin-top: 12px;

    .capacity-label {
      font-size: 12px;
      color: var(--text-secondary);
      margin-bottom: 4px;
      display: block;
    }

    .capacity-text {
      font-size: 12px;
      color: var(--text-secondary);
      text-align: right;
      display: block;
      margin-top: 2px;
    }
  }

  .card-actions {
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px solid var(--border-color);
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
