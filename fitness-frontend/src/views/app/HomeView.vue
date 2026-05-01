<template>
  <div class="home-view">
    <!-- 骨架屏 -->
    <div v-if="pageLoading" class="skeleton-wrap">
      <el-skeleton animated class="sk-hero" style="height:180px;border-radius:28px;margin-bottom:24px" />
      <div style="display:flex;gap:16px;margin-bottom:16px">
        <el-skeleton animated class="sk-card" style="flex:1;height:100px;border-radius:20px" />
        <el-skeleton animated class="sk-card" style="flex:1;height:100px;border-radius:20px" />
      </div>
      <el-skeleton animated style="height:80px;border-radius:20px;margin-bottom:12px" />
      <el-skeleton animated style="height:80px;border-radius:20px" />
    </div>

    <template v-else>
    <!-- 头部欢迎区域 -->
    <div class="welcome-header">
      <div class="greeting">
        <h2>{{ greetingText }}，{{ userInfo?.nickname || '运动达人' }}！</h2>
        <p class="subtitle">来看看你今天的运动数据吧</p>
      </div>
      <div class="header-action">
        <el-button class="scan-btn" @click="handleCheckin">
          <el-icon><FullScreen /></el-icon> 签到
        </el-button>
      </div>
    </div>

    <!-- 运动数据摘要大卡片 (Blob Aesthetic) -->
    <div class="hero-overview-card">
      <div class="blob-yellow"></div>
      <div class="blob-red"></div>
      
      <div class="hero-content">
        <h3 class="hero-title">本周你的<br>运动成果</h3>
        
        <div class="hero-metrics">
          <div class="metric-circle dark-circle">
            <span class="m-value">{{ weeklyStats?.weeklyCount || 0 }}</span>
            <span class="m-label">天</span>
          </div>
          <div class="metric-circle yellow-circle">
            <span class="m-value">{{ weeklyStats?.weeklyVolume || 0 }}</span>
            <span class="m-label">kg 容量</span>
          </div>
        </div>
        
        <div class="hero-legend">
          <div class="legend-item"><span class="dot c-yellow"></span> 训练总容量</div>
          <div class="legend-item"><span class="dot c-dark"></span> 活跃天数</div>
        </div>
      </div>
    </div>

    <!-- 快捷入口卡片组 (White Pill Cards) -->
    <div class="quick-actions">
      <div class="action-card" @click="router.push('/app/plan')">
        <div class="ac-header">
          <h4 class="ac-title">我的计划</h4>
          <span class="ac-val">正在进行</span>
        </div>
        <div class="ac-icon">
          <el-icon><Document /></el-icon>
        </div>
      </div>

      <div class="action-card" @click="router.push('/app/body-data')">
        <div class="ac-header">
          <h4 class="ac-title">身体数据</h4>
          <span class="ac-val">去记录</span>
        </div>
        <div class="ac-icon">
          <el-icon><DataLine /></el-icon>
        </div>
      </div>

      <div class="action-card" @click="router.push('/app/record')">
        <div class="ac-header">
          <h4 class="ac-title">运动记录</h4>
          <span class="ac-val">历史数据</span>
        </div>
        <div class="ac-icon">
          <el-icon><Edit /></el-icon>
        </div>
      </div>
    </div>

    <!-- 即将开始的课程 (Dark Card) -->
    <div class="dark-schedule-card">
      <div class="card-header">
        <h3 class="card-title">即将开始的课程</h3>
        <span class="card-action" @click="router.push('/app/course')">新增预约 <el-icon class="add-icon"><Plus /></el-icon></span>
      </div>
      
      <div v-if="!nextCourse" class="empty-text">
        暂无预约的课程，快去预约一个吧！
      </div>
      <div v-else class="course-list">
        <div class="course-item" @click="goCourseDetail(nextCourse)">
          <div class="c-avatar"><el-icon><Avatar /></el-icon></div>
          <div class="c-info">
            <div class="c-name">{{ nextCourse.courseName }}</div>
            <div class="c-coach">教练: {{ nextCourse.coachName }}</div>
          </div>
          <div class="c-meta">
            <span class="c-time">{{ nextCourse.scheduleTime || '10:00' }}</span>
          </div>
        </div>
      </div>
    </div>
    </template>
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
  FullScreen, Document, DataLine, Edit, Plus, Avatar
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const userInfo = computed(() => authStore.userInfo || {})

const greetingText = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 12) return '早上好，活力满满'
  if (hour < 18) return '下午好，继续加油'
  return '晚上好，练起来吧'
})

const pageLoading = ref(true)
const weeklyStats = ref({ weeklyCount: 0, weeklyVolume: 0 })
const nextCourse = ref(null)

const fetchStats = async () => {
  try {
    const res = await workoutApi.weeklyStats()
    if (res) weeklyStats.value = res
  } catch (error) {
    console.warn('获取训练统计失败', error)
  }
}

const fetchBookings = async () => {
  try {
    const res = await courseApi.myBookings()
    if (res && res.length > 0) {
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
    await fetchBookings()
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message || '签到失败'
    ElMessage.warning(msg)
  }
}

const goCourseDetail = () => {
  router.push('/app/course/booking')
}

onMounted(async () => {
  await Promise.all([fetchStats(), fetchBookings()])
  pageLoading.value = false
})
</script>

<style scoped>
.home-view {
  position: relative;
}
.skeleton-wrap {
  padding: 8px 0;
}

.home-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 欢迎区域 */
.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 8px;
}

.greeting h2 {
  font-size: 24px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.greeting .subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 500;
}

.scan-btn {
  background: var(--accent-dark) !important;
  color: white !important;
  border: none !important;
  border-radius: var(--border-radius-pill) !important;
  padding: 10px 20px !important;
  font-weight: 600;
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

/* Blob Overview Card */
.hero-overview-card {
  background-color: #e5e2da;
  border-radius: var(--border-radius-xl);
  padding: 32px 24px;
  position: relative;
  overflow: hidden;
  min-height: 280px;
  display: flex;
  flex-direction: column;
}

.blob-yellow {
  position: absolute;
  top: 10%;
  right: -5%;
  width: 220px;
  height: 220px;
  background: rgba(255, 204, 46, 0.9);
  filter: blur(40px);
  border-radius: 50%;
  z-index: 0;
}

.blob-red {
  position: absolute;
  bottom: -10%;
  left: 20%;
  width: 180px;
  height: 180px;
  background: rgba(255, 107, 107, 0.85);
  filter: blur(40px);
  border-radius: 50%;
  z-index: 0;
}

.hero-content {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.hero-title {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 32px;
  line-height: 1.3;
}

.hero-metrics {
  display: flex;
  gap: 12px;
  margin-top: auto;
  margin-bottom: 32px;
}

.metric-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
}

.m-value { font-size: 18px; font-weight: 800; line-height: 1; margin-bottom: 2px; }
.m-label { font-size: 10px; font-weight: 600; text-align: center; }

.dark-circle { background: var(--accent-dark); color: white; transform: translateY(10px); }
.dark-circle .m-label { color: #a0a0a0; }

.yellow-circle { background: var(--primary-color); color: var(--text-primary); transform: translateY(-10px); }
.yellow-circle .m-label { color: #8a6a00; }

.hero-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-primary);
}

.dot {
  width: 16px;
  height: 4px;
  border-radius: 2px;
}

.c-yellow { background: var(--primary-color); }
.c-dark { background: var(--accent-dark); }

/* Quick Actions Grid */
.quick-actions {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: none; /* Firefox */
}
.quick-actions::-webkit-scrollbar { display: none; } /* Chrome */

.action-card {
  min-width: 140px;
  background: white;
  border-radius: var(--border-radius-lg);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform 0.2s;
}
.action-card:active { transform: scale(0.95); }

.ac-header {
  display: flex;
  flex-direction: column;
}
.ac-title { font-size: 14px; font-weight: 700; margin: 0; color: var(--text-primary); }
.ac-val { font-size: 11px; color: var(--text-secondary); font-weight: 600; }

.ac-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--bg-base);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
  font-size: 16px;
  align-self: flex-end;
}

/* Dark Schedule Card */
.dark-schedule-card {
  background: var(--accent-dark);
  border-radius: var(--border-radius-xl);
  padding: 24px;
  color: white;
  margin-bottom: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-title { font-size: 18px; font-weight: 700; color: white; margin: 0; }
.card-action {
  font-size: 12px;
  color: #8c8c93;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}
.add-icon {
  background: var(--primary-color);
  color: var(--text-primary);
  border-radius: 50%;
  padding: 2px;
}

.empty-text {
  font-size: 13px;
  color: #6a6a70;
  text-align: center;
  padding: 10px 0;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
  border-radius: var(--border-radius-sm);
  cursor: pointer;
}

.c-avatar {
  width: 36px;
  height: 36px;
  background: #3a3b40;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.c-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.c-name { font-size: 14px; font-weight: 600; color: white; }
.c-coach { font-size: 11px; color: #8c8c93; }

.c-meta {
  display: flex;
  align-items: center;
}
.c-time {
  font-size: 12px;
  font-weight: 700;
  color: var(--primary-color);
}
</style>
