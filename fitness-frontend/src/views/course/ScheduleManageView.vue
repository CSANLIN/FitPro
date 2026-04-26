<template>
  <div class="schedule-manage-view">
    <div class="page-header">
      <h2>排课管理</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>新增排课
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" size="small">
        <el-form-item label="课程">
          <el-select v-model="query.courseId" placeholder="全部课程" clearable filterable @change="fetchSchedules" style="width: 180px">
            <el-option v-for="c in courseList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教练">
          <el-select v-model="query.coachId" placeholder="全部教练" clearable filterable @change="fetchSchedules" style="width: 150px">
            <el-option v-for="c in coachList" :key="c.id" :label="c.nickname" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
            @change="fetchSchedules"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 排课表格 -->
    <el-card v-loading="loading" class="table-card">
      <el-empty v-if="!loading && schedules.length === 0" description="暂无排课" />

      <el-table v-else :data="schedules" stripe style="width: 100%">
        <el-table-column prop="scheduleDate" label="日期" width="100" />
        <el-table-column label="时间" width="120">
          <template #default="{ row }">
            {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" min-width="120" />
        <el-table-column prop="coachName" label="教练" width="100" />
        <el-table-column prop="location" label="地点" width="100" />
        <el-table-column label="容量" width="80" align="center">
          <template #default="{ row }">
            <span :class="capacityClass(row)">{{ row.currentCount }}/{{ row.maxCapacity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewBookings(row)">预约列表</el-button>
            <el-button
              v-if="row.status === 'UPCOMING'"
              size="small"
              type="danger"
              plain
              @click="handleCancel(row)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增排课弹窗 -->
    <el-dialog
      v-model="createVisible"
      title="新增排课"
      width="500px"
      top="10vh"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="选择课程" filterable style="width: 100%">
            <el-option v-for="c in courseList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="form.coachId" placeholder="选择教练" filterable style="width: 100%">
            <el-option v-for="c in coachList" :key="c.id" :label="c.nickname" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="scheduleDate">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :picker-options="dateOptions"
            style="width: 100%"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="form.startTime"
                placeholder="开始时间"
                format="HH:mm"
                value-format="HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="form.endTime"
                placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" placeholder="如: 3楼大操房" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleCreate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 预约列表弹窗 -->
    <el-dialog
      v-model="bookingVisible"
      :title="`预约列表 - ${currentSchedule?.courseName || ''}`"
      width="700px"
      top="5vh"
      destroy-on-close
    >
      <template v-if="currentSchedule">
        <div class="booking-summary">
          <span>日期: {{ currentSchedule.scheduleDate }} {{ currentSchedule.startTime }}-{{ currentSchedule.endTime }}</span>
          <span>教练: {{ currentSchedule.coachName }}</span>
          <span>预约: {{ currentSchedule.currentCount }}/{{ currentSchedule.maxCapacity }}</span>
        </div>
      </template>
      <el-empty v-if="bookings.length === 0" description="暂无预约" />
      <el-table v-else :data="bookings" stripe style="width: 100%">
        <el-table-column prop="userName" label="会员姓名" width="120" />
        <el-table-column label="预约状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="bookingStatusType(row.status)" size="small">
              {{ bookingStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bookedAt" label="预约时间" width="160" />
        <el-table-column prop="cancelledAt" label="取消时间" width="160" v-if="hasCancelled" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { courseApi } from '@/api/course'
import { userApi } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const courseList = ref([])
const coachList = ref([])
const schedules = ref([])
const bookings = ref([])
const loading = ref(false)
const saving = ref(false)
const createVisible = ref(false)
const bookingVisible = ref(false)
const currentSchedule = ref(null)
const formRef = ref(null)
const dateRange = ref(null)

const query = reactive({
  courseId: null,
  coachId: null,
  startDate: '',
  endDate: ''
})

const form = reactive({
  courseId: null,
  coachId: null,
  scheduleDate: '',
  startTime: '',
  endTime: '',
  location: ''
})

const formRules = {
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const dateOptions = {
  disabledDate: (time) => time.getTime() < Date.now() - 86400000
}

const hasCancelled = computed(() => bookings.value.some(b => b.status === 'CANCELLED'))

const statusLabel = (val) => ({
  UPCOMING: '待开始',
  ONGOING: '进行中',
  FINISHED: '已结束',
  CANCELLED: '已取消'
}[val] || val)

const statusType = (val) => ({
  UPCOMING: 'success',
  ONGOING: 'warning',
  FINISHED: 'info',
  CANCELLED: 'danger'
}[val] || 'info')

const capacityClass = (row) => {
  const ratio = row.currentCount / row.maxCapacity
  if (ratio >= 1) return 'capacity-full'
  if (ratio >= 0.8) return 'capacity-high'
  return 'capacity-low'
}

const bookingStatusLabel = (val) => ({
  BOOKED: '已预约',
  CANCELLED: '已取消',
  ATTENDED: '已上课',
  ABSENT: '未出席'
}[val] || val)

const bookingStatusType = (val) => ({
  BOOKED: 'success',
  CANCELLED: 'info',
  ATTENDED: 'primary',
  ABSENT: 'danger'
}[val] || 'info')

const fetchCourseList = async () => {
  try {
    courseList.value = await courseApi.list({ all: true })
  } catch (e) {
    console.error('获取课程列表失败:', e)
  }
}

const fetchCoachList = async () => {
  try {
    // 获取教练列表
    const res = await userApi.list({ role: 'COACH', pageNum: 1, pageSize: 999 })
    coachList.value = res.list || []
  } catch (e) {
    console.error('获取教练列表失败:', e)
  }
}

const fetchSchedules = async () => {
  loading.value = true
  try {
    const params = {
      courseId: query.courseId || undefined,
      coachId: query.coachId || undefined
    }
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    schedules.value = await courseApi.listSchedules(params)
  } catch (e) {
    console.error('获取排课失败:', e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  form.courseId = null
  form.coachId = null
  form.scheduleDate = ''
  form.startTime = ''
  form.endTime = ''
  form.location = ''
  createVisible.value = true
}

const handleCreate = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await courseApi.createSchedule({
      courseId: form.courseId,
      coachId: form.coachId,
      scheduleDate: form.scheduleDate,
      startTime: form.startTime,
      endTime: form.endTime,
      location: form.location || null
    })
    ElMessage.success('排课创建成功')
    createVisible.value = false
    await fetchSchedules()
  } catch (e) {
    console.error('创建排课失败:', e)
  } finally {
    saving.value = false
  }
}

const handleCancel = async (schedule) => {
  try {
    await ElMessageBox.confirm('确定取消该排课？取消后不可恢复。', '确认', {
      type: 'warning'
    })
    await courseApi.cancelSchedule(schedule.id)
    ElMessage.success('排课已取消')
    await fetchSchedules()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('取消排课失败:', e)
    }
  }
}

const viewBookings = async (schedule) => {
  currentSchedule.value = schedule
  try {
    bookings.value = await courseApi.listBookingsBySchedule(schedule.id)
    bookingVisible.value = true
  } catch (e) {
    console.error('获取预约列表失败:', e)
  }
}

onMounted(() => {
  fetchCourseList()
  fetchCoachList()
  fetchSchedules()
})
</script>

<style scoped>
.schedule-manage-view {
  max-width: 1100px;
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

.filter-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.table-card {
  border-radius: 12px;
}

.capacity-low { color: var(--el-color-success); font-weight: 600; }
.capacity-high { color: var(--el-color-warning); font-weight: 600; }
.capacity-full { color: var(--el-color-danger); font-weight: 600; }

.booking-summary {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 16px;
  padding: 12px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
}
</style>
