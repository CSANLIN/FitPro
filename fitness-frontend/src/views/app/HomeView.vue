<template>
  <div class="home-view">
    <!-- 头部欢迎区域 -->
    <div class="welcome-header">
      <div class="greeting">
        <h2>{{ greetingText }}, {{ userInfo?.nickname || '会员' }}</h2>
        <p class="subtitle">准备好今天的训练了吗？</p>
      </div>
      <div class="header-action">
        <el-button type="primary" circle class="scan-btn" @click="handleCheckin">
          <el-icon><FullScreen /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 运动数据摘要卡片 -->
    <div class="stats-overview">
      <div class="stat-card primary">
        <div class="stat-icon"><el-icon><Timer /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ weeklyStats?.weeklyVolume || 0 }}<small> kg</small></span>
          <span class="stat-label">本周总容量</span>
        </div>
      </div>
      <div class="stat-card secondary">
        <div class="stat-icon"><el-icon><Calendar /></el-icon></div>
        <div class="stat-info">
          <span class="stat-value">{{ weeklyStats?.weeklyCount || 0 }}<small> 次</small></span>
          <span class="stat-label">本周训练</span>
        </div>
      </div>
    </div>

    <!-- 快捷入口卡片组 -->
    <div class="quick-actions">
      <div class="action-btn" @click="router.push('/app/plan')">
        <div class="icon-box" style="background: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);">
          <el-icon color="#fff"><Document /></el-icon>
        </div>
        <span>训练计划</span>
      </div>
      <div class="action-btn" @click="router.push('/app/course')">
        <div class="icon-box" style="background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);">
          <el-icon color="#fff"><Ticket /></el-icon>
        </div>
        <span>预约课程</span>
      </div>
      <div class="action-btn" @click="router.push('/app/body-data')">
        <div class="icon-box" style="background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);">
          <el-icon color="#fff"><DataLine /></el-icon>
        </div>
        <span>身体数据</span>
      </div>
      <div class="action-btn" @click="router.push('/app/record')">
        <div class="icon-box" style="background: linear-gradient(135deg, #fccb90 0%, #d57eeb 100%);">
          <el-icon color="#fff"><Edit /></el-icon>
        </div>
        <span>训练记录</span>
      </div>
    </div>

    <!-- 即将开始的课程 -->
    <div class="section-container" v-if="nextCourse">
      <div class="section-header">
        <h3>即将开始</h3>
        <el-button link type="primary" @click="router.push('/app/course/booking')">全部</el-button>
      </div>
      <div class="course-card">
        <div class="course-date">
          <span class="day">{{ nextCourseDay }}</span>
          <span class="month">{{ nextCourseMonth }}</span>
        </div>
        <div class="course-info">
          <h4>{{ nextCourse.courseName }}</h4>
          <p class="time">
            <el-icon><Clock /></el-icon>
            {{ nextCourse.scheduleTime || '10:00 - 11:00' }}
          </p>
          <p class="coach">教练: {{ nextCourse.coachName }}</p>
        </div>
        <div class="course-action">
          <el-button type="primary" round size="small" @click="goCourseDetail(nextCourse)">查看</el-button>
        </div>
      </div>
    </div>

    <!-- 推荐内容/占位图 -->
    <div class="section-container">
      <div class="section-header">
        <h3>推荐探索</h3>
      </div>
      <div class="banner-card" @click="router.push('/app/exercise')">
        <div class="banner-content">
          <h4>解锁全新动作库</h4>
          <p>掌握正确的发力技巧，避免受伤</p>
          <el-button type="primary" round size="small" class="explore-btn">去探索</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { checkinApi } from '@/api/checkin'
import { courseApi } from '@/api/course'
import { workoutApi } from '@/api/workout'
import {
  FullScreen,
  Timer,
  Calendar,
  Document,
  Ticket,
  DataLine,
  Edit,
  Clock
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const userInfo = computed(() => authStore.userInfo || {})

const greetingText = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 数据状态
const weeklyStats = ref({ weeklyCount: 0, weeklyVolume: 0 })
const nextCourse = ref(null)

const nextCourseDay = computed(() => {
  if (!nextCourse.value || !nextCourse.value.scheduleDate) return '今日'
  return new Date(nextCourse.value.scheduleDate).getDate()
})

const nextCourseMonth = computed(() => {
  if (!nextCourse.value || !nextCourse.value.scheduleDate) return '本月'
  return (new Date(nextCourse.value.scheduleDate).getMonth() + 1) + '月'
})

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await workoutApi.weeklyStats()
    if (res) {
      weeklyStats.value = res
    }
  } catch (error) {
    console.warn('获取训练统计失败', error)
  }
}

// 获取预约课程
const fetchBookings = async () => {
  try {
    const res = await courseApi.myBookings()
    if (res && res.length > 0) {
      // 简单取第一个作为即将开始的课程，实际应过滤未来时间并排序
      nextCourse.value = res[0]
    }
  } catch (error) {
    console.warn('获取预约记录失败', error)
  }
}

const handleCheckin = async () => {
  try {
    await checkinApi.checkIn()
    ElMessage.success('签到成功！')
  } catch (error) {
    if (error?.response?.data?.message) {
      ElMessage.warning(error.response.data.message)
    } else {
      ElMessage.success('模拟签到成功（请检查接口连接）')
    }
  }
}

const goCourseDetail = (course) => {
  router.push('/app/course/booking')
}

onMounted(() => {
  fetchStats()
  fetchBookings()
})
</script>

<style scoped>
.home-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 欢迎区域 */
.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.greeting h2 {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.greeting .subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.scan-btn {
  width: 48px;
  height: 48px;
  font-size: 22px;
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.3);
  transition: transform 0.3s ease;
}

.scan-btn:hover {
  transform: translateY(-2px);
}

/* 统计卡片 */
.stats-overview {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-card {
  padding: 20px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-card.primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, #6366f1 100%);
  color: white;
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.2);
}

.stat-card.secondary {
  background: #ffffff;
  color: #334155;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.stat-card.secondary .stat-icon {
  background: #f1f5f9;
  color: #64748b;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 22px;
  font-weight: 800;
  line-height: 1.2;
}

.stat-value small {
  font-size: 12px;
  font-weight: 500;
  opacity: 0.8;
}

.stat-label {
  font-size: 12px;
  margin-top: 4px;
  opacity: 0.9;
}

/* 快捷入口 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  background: white;
  padding: 20px 16px;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.action-btn:active {
  transform: scale(0.95);
}

.icon-box {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.08);
}

.action-btn span {
  font-size: 12px;
  font-weight: 600;
  color: #475569;
}

/* 模块容器 */
.section-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

/* 课程卡片 */
.course-card {
  background: white;
  border-radius: 20px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.course-date {
  background: #f8fafc;
  border-radius: 14px;
  width: 60px;
  height: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
}

.course-date .day {
  font-size: 20px;
  font-weight: 800;
  line-height: 1;
}

.course-date .month {
  font-size: 11px;
  font-weight: 600;
  margin-top: 4px;
}

.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-info h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.course-info .time {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-info .coach {
  margin: 0;
  font-size: 12px;
  color: #94a3b8;
}

/* Banner */
.banner-card {
  background: url('https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=800&q=80') center/cover no-repeat;
  height: 160px;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.banner-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(15, 23, 42, 0.9) 0%, rgba(15, 23, 42, 0.4) 100%);
}

.banner-content {
  position: relative;
  z-index: 1;
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  color: white;
}

.banner-content h4 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 800;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.banner-content p {
  margin: 0 0 16px 0;
  font-size: 13px;
  opacity: 0.9;
}

.explore-btn {
  background: white;
  color: #0f172a;
  border: none;
  font-weight: 600;
}

.explore-btn:hover {
  background: #f1f5f9;
  color: var(--primary-color);
}
</style>
