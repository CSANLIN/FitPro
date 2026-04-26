<template>
  <div class="course-booking-view">
    <!-- 课程信息头 -->
    <div class="page-header">
      <div>
        <h2>{{ courseName || '课程预约' }}</h2>
        <p class="header-desc" v-if="courseName">选择日期和时间进行预约</p>
      </div>
      <el-button @click="showMyBookings">
        <el-icon><Ticket /></el-icon>我的预约
      </el-button>
    </div>

    <!-- 课程筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" size="small">
        <el-form-item label="课程">
          <el-select v-model="query.courseId" placeholder="选择课程" filterable clearable @change="fetchSchedules" style="width: 200px">
            <el-option v-for="c in courseList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
            @change="fetchSchedules"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 排课列表 -->
    <div v-loading="loading" class="schedule-section">
      <el-empty v-if="!loading && schedules.length === 0" description="暂无可用排课" />

      <div v-else class="schedule-list">
        <el-card
          v-for="item in schedules"
          :key="item.id"
          class="schedule-card"
          :class="{ 'is-booked': item.booked }"
        >
          <div class="schedule-left">
            <div class="schedule-date">
              <span class="date-day">{{ dayOfMonth(item.scheduleDate) }}</span>
              <span class="date-week">{{ dayOfWeek(item.scheduleDate) }}</span>
            </div>
            <div class="schedule-time">
              <el-icon><Clock /></el-icon>
              <span>{{ item.startTime }} - {{ item.endTime }}</span>
            </div>
          </div>
          <div class="schedule-middle">
            <div class="schedule-course">
              <el-tag size="small" effect="plain">{{ typeLabel(item.courseType) }}</el-tag>
              <span class="course-name">{{ item.courseName }}</span>
            </div>
            <div class="schedule-meta">
              <span><el-icon><User /></el-icon> {{ item.coachName || '待定教练' }}</span>
              <span v-if="item.location"><el-icon><Location /></el-icon> {{ item.location }}</span>
            </div>
          </div>
          <div class="schedule-right">
            <div class="capacity-info">
              <span :class="capacityClass(item)">{{ item.currentCount }}/{{ item.maxCapacity }}</span>
              <span class="capacity-label">已预约</span>
            </div>
            <el-button
              v-if="item.booked"
              type="danger"
              size="small"
              plain
              @click="handleCancelBooking(item)"
            >取消预约</el-button>
            <el-button
              v-else
              type="primary"
              size="small"
              :disabled="item.currentCount >= item.maxCapacity"
              @click="handleBook(item)"
            >
              {{ item.currentCount >= item.maxCapacity ? '已满' : '预约' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 我的预约弹窗 -->
    <el-dialog
      v-model="myBookingVisible"
      title="我的预约"
      width="600px"
      top="5vh"
      destroy-on-close
    >
      <el-empty v-if="myBookings.length === 0" description="暂无预约记录" />
      <div v-else class="my-booking-list">
        <div v-for="item in myBookings" :key="item.id" class="booking-item">
          <div class="booking-left">
            <el-tag size="small" :type="bookingStatusType(item.status)">
              {{ bookingStatusLabel(item.status) }}
            </el-tag>
            <span class="booking-course">{{ item.courseName }}</span>
          </div>
          <div class="booking-meta">
            <span>{{ item.scheduleDate }} {{ item.startTime }}-{{ item.endTime }}</span>
            <span v-if="item.coachName">教练: {{ item.coachName }}</span>
          </div>
          <div class="booking-action" v-if="item.status === 'BOOKED'">
            <el-button size="small" type="danger" plain @click="handleCancelMyBooking(item)">
              取消
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { courseApi } from '@/api/course'
import { ElMessage } from 'element-plus'
import { Clock, User, Location, Ticket } from '@element-plus/icons-vue'

const route = useRoute()

const courseList = ref([])
const schedules = ref([])
const loading = ref(false)
const myBookings = ref([])
const myBookingVisible = ref(false)
const courseName = ref(route.query.courseName || '')
const dateRange = ref(null)

const query = reactive({
  courseId: route.query.courseId || null,
  startDate: '',
  endDate: ''
})

const typeLabel = (val) => ({
  YOGA: '瑜伽',
  BOXING: '搏击',
  SPINNING: '动感单车',
  HIIT: 'HIIT',
  OTHER: '其他'
}[val] || val)

const dayOfMonth = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).getDate()
}

const dayOfWeek = (dateStr) => {
  if (!dateStr) return ''
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return days[new Date(dateStr).getDay()]
}

const capacityClass = (item) => {
  const ratio = item.currentCount / item.maxCapacity
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
    courseList.value = await courseApi.list()
  } catch (e) {
    console.error('获取课程列表失败:', e)
  }
}

const fetchSchedules = async () => {
  loading.value = true
  try {
    const params = { courseId: query.courseId || undefined }
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    } else {
      // 默认显示未来两周
      const start = new Date()
      const end = new Date()
      end.setDate(end.getDate() + 14)
      params.startDate = start.toISOString().split('T')[0]
      params.endDate = end.toISOString().split('T')[0]
    }
    schedules.value = await courseApi.listSchedules(params)
  } catch (e) {
    console.error('获取排课失败:', e)
  } finally {
    loading.value = false
  }
}

const handleBook = async (item) => {
  try {
    await courseApi.book(item.id)
    ElMessage.success('预约成功')
    await fetchSchedules()
  } catch (e) {
    console.error('预约失败:', e)
  }
}

const handleCancelBooking = async (item) => {
  try {
    // 需要先找到我的预约ID
    const bookings = await courseApi.myBookings()
    const booking = bookings.find(b => b.scheduleId === item.id && b.status === 'BOOKED')
    if (booking) {
      await courseApi.cancelBooking(booking.id)
      ElMessage.success('已取消预约')
      await fetchSchedules()
    }
  } catch (e) {
    console.error('取消预约失败:', e)
  }
}

const handleCancelMyBooking = async (item) => {
  try {
    await courseApi.cancelBooking(item.id)
    ElMessage.success('已取消预约')
    await fetchMyBookings()
    await fetchSchedules()
  } catch (e) {
    console.error('取消预约失败:', e)
  }
}

const fetchMyBookings = async () => {
  try {
    myBookings.value = await courseApi.myBookings()
  } catch (e) {
    console.error('获取我的预约失败:', e)
  }
}

const showMyBookings = async () => {
  await fetchMyBookings()
  myBookingVisible.value = true
}

onMounted(() => {
  fetchCourseList()
  fetchSchedules()
})
</script>

<style scoped>
.course-booking-view {
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

.header-desc {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.filter-card {
  border-radius: 12px;
  margin-bottom: 16px;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.schedule-card {
  border-radius: 12px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.schedule-card.is-booked {
  border-left: 4px solid var(--el-color-success);
}

.schedule-card:not(.is-booked) {
  border-left: 4px solid var(--el-border-color-light);
}

.schedule-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;
  padding: 8px 16px 8px 0;
  border-right: 1px solid var(--el-border-color-light);
  margin-right: 16px;
}

.date-day {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-color-primary);
  line-height: 1.2;
}

.date-week {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.schedule-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--el-text-color-regular);
  margin-top: 4px;
}

.schedule-middle {
  flex: 1;
}

.schedule-course {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.course-name {
  font-size: 15px;
  font-weight: 600;
}

.schedule-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.schedule-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.schedule-right {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 80px;
  margin-left: 16px;
}

.capacity-info {
  text-align: center;
}

.capacity-info span:first-child {
  font-size: 18px;
  font-weight: 600;
  display: block;
}

.capacity-label {
  font-size: 11px;
  color: var(--el-text-color-secondary);
}

.capacity-low { color: var(--el-color-success); }
.capacity-high { color: var(--el-color-warning); }
.capacity-full { color: var(--el-color-danger); }

/* 我的预约弹窗 */
.my-booking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.booking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
}

.booking-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
}

.booking-course {
  font-weight: 600;
  font-size: 14px;
}

.booking-meta {
  flex: 1;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.booking-action {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .schedule-card {
    flex-direction: column;
    align-items: stretch;
  }

  .schedule-left {
    flex-direction: row;
    border-right: none;
    border-bottom: 1px solid var(--el-border-color-light);
    margin-right: 0;
    padding: 0 0 8px;
    margin-bottom: 8px;
    gap: 16px;
  }

  .schedule-right {
    flex-direction: row;
    margin-left: 0;
    margin-top: 8px;
    justify-content: space-between;
  }
}
</style>
