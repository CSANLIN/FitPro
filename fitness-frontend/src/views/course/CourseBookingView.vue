<template>
  <div class="course-booking-view">
    <!-- 高级课程头部 -->
    <div class="premium-hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <div class="hero-text">
          <h2 class="hero-title">{{ courseName || '课程排期预约' }}</h2>
          <p class="hero-subtitle">找到最适合你的时间，开启运动之旅</p>
        </div>
        <div class="hero-action">
          <el-button color="#fff" class="my-booking-btn" round @click="showMyBookings">
            <el-icon><Ticket /></el-icon>我的预约
          </el-button>
        </div>
      </div>
    </div>

    <!-- 悬浮筛选器 -->
    <div class="filter-wrapper">
      <div class="glass-filter-card">
        <el-form :inline="true" size="large" class="filter-form">
          <el-form-item label="课程选择" class="filter-item">
            <el-select v-model="query.courseId" placeholder="选择你想上的课程" filterable clearable @change="fetchSchedules" style="width: 220px">
              <el-option v-for="c in courseList" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围" class="filter-item">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 280px"
              @change="fetchSchedules"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 排课列表 -->
    <div v-loading="loading" class="schedule-section">
      <el-empty v-if="!loading && schedules.length === 0" description="未找到匹配的课程排期" />

      <div v-else class="schedule-list">
        <div
          v-for="item in schedules"
          :key="item.id"
          class="schedule-card premium-shadow"
          :class="{ 'is-booked-card': item.booked }"
        >
          <!-- 左侧日期 -->
          <div class="schedule-date-box" :class="{'booked-bg': item.booked}">
            <span class="date-day">{{ dayOfMonth(item.scheduleDate) }}</span>
            <span class="date-month">{{ monthStr(item.scheduleDate) }}</span>
            <span class="date-week">{{ dayOfWeek(item.scheduleDate) }}</span>
          </div>
          
          <!-- 中间信息 -->
          <div class="schedule-info-box">
            <div class="course-main">
              <span class="course-name">{{ item.courseName }}</span>
              <el-tag size="small" :effect="item.booked ? 'dark' : 'light'" :type="item.booked ? 'success' : 'primary'" round>
                {{ typeLabel(item.courseType) }}
              </el-tag>
            </div>
            
            <div class="course-meta">
              <div class="meta-item time">
                <el-icon><Clock /></el-icon>
                <span>{{ item.startTime }} - {{ item.endTime }}</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ item.coachName || '待定教练' }}</span>
              </div>
              <div class="meta-item" v-if="item.location">
                <el-icon><Location /></el-icon>
                <span>{{ item.location }}</span>
              </div>
            </div>
            <div class="course-price" v-if="item.price && parseFloat(item.price) > 0">
              <span class="price-tag">¥{{ item.price }}</span>
            </div>
            <div class="course-price" v-else>
              <span class="free-tag">免费</span>
            </div>
          </div>
          
          <!-- 右侧操作 -->
          <div class="schedule-action-box">
            <div class="capacity-indicator">
              <div class="capacity-text" :class="capacityClass(item)">
                <strong>{{ item.currentCount }}</strong> / {{ item.maxCapacity }}
              </div>
              <div class="capacity-progress">
                <div class="progress-bar" :style="{ width: `${Math.min(100, (item.currentCount / item.maxCapacity) * 100)}%`, backgroundColor: capacityColor(item) }"></div>
              </div>
              <span class="capacity-label">已预约人数</span>
            </div>
            
            <div class="btn-wrapper">
              <el-button
                v-if="item.booked"
                type="danger"
                plain
                round
                @click="handleCancelBooking(item)"
                class="action-btn"
              >取消预约</el-button>
              <el-button
                v-else
                type="primary"
                round
                :disabled="item.currentCount >= item.maxCapacity"
                @click="handleBook(item)"
                class="action-btn"
              >
                {{ item.currentCount >= item.maxCapacity ? '名额已满' : '立即预约' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 我的预约弹窗 -->
    <el-dialog
      v-model="myBookingVisible"
      title="我的预约"
      width="600px"
      top="10vh"
      destroy-on-close
      class="custom-dialog"
    >
      <el-empty v-if="myBookings.length === 0" description="暂无预约记录" />
      <div v-else class="my-booking-list">
        <div v-for="item in myBookings" :key="item.id" class="booking-item">
          <div class="booking-left">
            <el-tag size="small" :type="bookingStatusType(item.status)" effect="dark" round>
              {{ bookingStatusLabel(item.status) }}
            </el-tag>
            <span class="booking-course">{{ item.courseName }}</span>
          </div>
          <div class="booking-meta">
            <div class="meta-time"><el-icon><Clock/></el-icon> {{ item.scheduleDate }} {{ item.startTime }}-{{ item.endTime }}</div>
            <div class="meta-coach" v-if="item.coachName"><el-icon><User/></el-icon> {{ item.coachName }}</div>
          </div>
          <div class="booking-action" v-if="item.status === 'BOOKED'">
            <el-button size="small" type="danger" plain round @click="handleCancelMyBooking(item)">
              取消预约
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { courseApi } from '@/api/course'
import { paymentApi } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, User, Location, Ticket } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

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

const typeLabel = (val) => val || '其他'

const dayOfMonth = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).getDate()
}

const monthStr = (dateStr) => {
  if (!dateStr) return ''
  const m = new Date(dateStr).getMonth() + 1
  return `${m}月`
}

const dayOfWeek = (dateStr) => {
  if (!dateStr) return ''
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return days[new Date(dateStr).getDay()]
}

const capacityClass = (item) => {
  const ratio = item.currentCount / item.maxCapacity
  if (ratio >= 1) return 'color-danger'
  if (ratio >= 0.8) return 'color-warning'
  return 'color-success'
}

const capacityColor = (item) => {
  const ratio = item.currentCount / item.maxCapacity
  if (ratio >= 1) return '#f56c6c'
  if (ratio >= 0.8) return '#e6a23c'
  return '#67c23a'
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
  // 如果课程有价格，走支付流程
  if (item.price && parseFloat(item.price) > 0) {
    try {
      const order = await paymentApi.createOrder({ scheduleId: item.id })
      // 跳转到支付页面，带上相关信息
      router.push({
        name: 'Payment',
        params: { orderNo: order.orderNo },
        query: {
          scheduleId: item.id,
          amount: item.price,
          courseName: item.courseName,
          scheduleDate: item.scheduleDate,
          startTime: item.startTime,
          endTime: item.endTime,
          coachName: item.coachName
        }
      })
    } catch (e) {
      ElMessage.error(e.response?.data?.message || '创建订单失败')
    }
    return
  }

  // 免费课程直接预约
  try {
    await courseApi.book(item.id)
    ElMessage.success('预约成功')
    await fetchSchedules()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '预约失败，请稍后重试')
  }
}

const handleCancelBooking = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
    const bookings = await courseApi.myBookings()
    const booking = bookings.find(b => b.scheduleId === item.id && b.status === 'BOOKED')
    if (booking) {
      await courseApi.cancelBooking(booking.id)
      ElMessage.success('已取消预约')
      await fetchSchedules()
    } else {
      ElMessage.warning('未找到对应的预约记录')
    }
  } catch (e) {
    // ignore
  }
}

const handleCancelMyBooking = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？', '提示', { type: 'warning' })
    await courseApi.cancelBooking(item.id)
    ElMessage.success('已取消预约')
    await fetchMyBookings()
    await fetchSchedules()
  } catch (e) {
    // ignore
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
  max-width: 1000px;
  margin: 0 auto;
  padding-bottom: 40px;
}

/* 高级头部 */
.premium-hero {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  margin-top: 8px;
  margin-bottom: -30px; /* 让下面的筛选器悬浮上去 */
  padding: 40px 30px 60px;
  background: linear-gradient(135deg, var(--primary-color) 0%, #8b5cf6 100%);
  color: white;
  box-shadow: 0 10px 30px rgba(139, 92, 246, 0.2);
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at top right, rgba(255,255,255,0.2) 0%, transparent 60%);
}

.hero-content {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hero-title {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.hero-subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.my-booking-btn {
  color: var(--primary-color);
  font-weight: 600;
  border: none;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 筛选器 */
.filter-wrapper {
  position: relative;
  z-index: 10;
  padding: 0 20px;
  margin-bottom: 30px;
}

.glass-filter-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.5);
  display: flex;
  justify-content: center;
}

.filter-form {
  margin-bottom: -18px; /* 修正el-form自带的下边距 */
}

/* 排课列表 */
.schedule-section {
  padding: 0 10px;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.schedule-card {
  background: white;
  border-radius: 20px;
  display: flex;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid transparent;
}

.schedule-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
}

.is-booked-card {
  border-color: #67c23a;
  box-shadow: 0 4px 16px rgba(103, 194, 58, 0.1);
}

/* 日期块 */
.schedule-date-box {
  background: #f8fafc;
  width: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  border-right: 1px dashed #e2e8f0;
  transition: all 0.3s;
}

.booked-bg {
  background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
  border-right-color: transparent;
}

.date-day {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
}

.booked-bg .date-day {
  color: #67c23a;
}

.date-month {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  margin-top: 4px;
}

.date-week {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 2px;
}

/* 信息块 */
.schedule-info-box {
  flex: 1;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-main {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.course-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #64748b;
}

.meta-item.time {
  color: var(--primary-color);
  font-weight: 600;
}

/* 操作块 */
.schedule-action-box {
  width: 200px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  background: #fafaf9;
}

.capacity-indicator {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.capacity-text {
  font-size: 14px;
  color: #64748b;
}

.capacity-text strong {
  font-size: 18px;
}

.color-success { color: #67c23a; }
.color-warning { color: #e6a23c; }
.color-danger { color: #f56c6c; }

.capacity-progress {
  width: 80%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.capacity-label {
  font-size: 11px;
  color: #94a3b8;
}

.action-btn {
  width: 120px;
  font-weight: 600;
}

/* 我的预约弹窗 */
.my-booking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.booking-item {
  display: flex;
  align-items: center;
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.booking-left {
  width: 180px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.booking-course {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.booking-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.meta-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.meta-coach {
  display: flex;
  align-items: center;
  gap: 6px;
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .glass-filter-card {
    padding: 16px;
  }
  
  .filter-form {
    display: flex;
    flex-direction: column;
    width: 100%;
  }
  
  .filter-item {
    width: 100%;
    margin-right: 0;
  }
  
  :deep(.el-select), :deep(.el-date-editor) {
    width: 100% !important;
  }
  
  .schedule-card {
    flex-direction: column;
  }
  
  .schedule-date-box {
    width: 100%;
    flex-direction: row;
    padding: 16px;
    border-right: none;
    border-bottom: 1px dashed #e2e8f0;
    gap: 12px;
    justify-content: flex-start;
  }
  
  .schedule-info-box {
    padding: 16px;
  }
  
  .schedule-action-box {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    padding: 16px;
  }
  
  .capacity-indicator {
    width: auto;
    align-items: flex-start;
  }
  
  .booking-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .booking-left {
    width: 100%;
  }
}
</style>
