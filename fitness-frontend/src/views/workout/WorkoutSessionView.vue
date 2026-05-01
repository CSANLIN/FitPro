<template>
  <div class="workout-session-view">
    <!-- 顶部导航 -->
    <div class="session-header">
      <el-button text @click="confirmExit" :disabled="saving">
        <el-icon><ArrowLeft /></el-icon> 退出训练
      </el-button>
      <h2 class="session-title">{{ plan?.name || '训练中' }}</h2>
      <div class="header-right">
        <el-tag v-if="timerRunning" type="warning" effect="dark" round>
          <el-icon class="rotate-anim"><Clock /></el-icon> 休息中
        </el-tag>
        <el-tag v-else-if="!allDone" type="success" effect="dark" round>训练中</el-tag>
        <el-tag v-else type="primary" effect="dark" round>已完成</el-tag>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap">
      <el-skeleton animated style="height:400px;border-radius:24px" />
    </div>

    <template v-else-if="error">
      <el-empty :description="error" />
    </template>

    <template v-else-if="!todayWorkout">
      <el-empty description="今天没有训练安排，休息一天吧！">
        <el-button type="primary" round @click="goBack">返回计划</el-button>
      </el-empty>
    </template>

    <template v-else>
      <!-- 训练进度 -->
      <div class="progress-bar-wrap">
        <div class="progress-info">
          <span class="progress-text">完成进度</span>
          <span class="progress-percent">{{ Math.round(progressPercent) }}%</span>
        </div>
        <el-progress :percentage="Math.round(progressPercent)" :stroke-width="10" :color="progressColor" />
      </div>

      <!-- 当前动作卡片 -->
      <div class="current-exercise-card">
        <div class="exercise-header">
          <span class="exercise-name">{{ currentItem?.exerciseName }}</span>
          <span class="exercise-set">第 {{ currentSetDisplay }}/{{ totalSetsForExercise }} 组</span>
        </div>

        <div v-if="currentItem" class="exercise-detail">
          <div class="detail-row">
            <div class="detail-box">
              <span class="detail-label">重量</span>
              <span class="detail-value">{{ currentItem.weight || 0 }} kg</span>
            </div>
            <div class="detail-box">
              <span class="detail-label">次数</span>
              <span class="detail-value">{{ currentItem.reps }} 次</span>
            </div>
            <div class="detail-box" v-if="currentItem.restSeconds">
              <span class="detail-label">组间休息</span>
              <span class="detail-value">{{ currentItem.restSeconds }}s</span>
            </div>
          </div>
        </div>

        <!-- 倒计时 -->
        <div v-if="timerRunning" class="timer-section">
          <div class="timer-circle" :class="{ 'timer-warning': restCountdown <= 5 }">
            <span class="timer-value">{{ formatTimer(restCountdown) }}</span>
          </div>
          <p class="timer-label">休息中，准备下一组</p>
          <el-button type="primary" round @click="skipRest" class="skip-btn">跳过休息</el-button>
        </div>

        <!-- 动作列表（当前动作的所有组） -->
        <div class="sets-grid">
          <div
            v-for="(set, idx) in currentExerciseSets"
            :key="idx"
            class="set-card"
            :class="{ 'set-active': setIndex === idx && !timerRunning, 'set-done': set.done }"
            @click="!set.done && !timerRunning && completeSet(idx)"
          >
            <div class="set-num">第{{ idx + 1 }}组</div>
            <div class="set-info">{{ set.weight || 0 }}kg × {{ set.reps }}次</div>
            <div class="set-status">
              <el-icon v-if="set.done" color="#10b981" :size="24"><CircleCheckFilled /></el-icon>
              <el-icon v-else :size="24"><Circle /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 动作导航（缩略图列表） -->
      <div class="exercise-nav">
        <div
          v-for="(ex, idx) in todayWorkout.items"
          :key="idx"
          class="nav-dot-group"
        >
          <div class="nav-label">{{ ex.exerciseName }}</div>
          <div class="nav-dots">
            <div
              v-for="(s, si) in ex.sets"
              :key="si"
              class="nav-dot"
              :class="{ done: s.done, active: idx === exerciseIndex && si === setIndex }"
            />
          </div>
        </div>
      </div>

      <!-- 完成所有动作后 -->
      <div v-if="allDone" class="complete-section">
        <div class="complete-icon"><el-icon :size="64" color="#10b981"><CircleCheckFilled /></el-icon></div>
        <h3>训练完成！</h3>
        <p>你已完成今日全部训练内容</p>
        <div class="complete-stats">
          <div class="c-stat">
            <span class="c-val">{{ todayWorkout.items.length }}</span>
            <span class="c-lbl">动作数</span>
          </div>
          <div class="c-stat">
            <span class="c-val">{{ totalSetsDone }}</span>
            <span class="c-lbl">总组数</span>
          </div>
          <div class="c-stat">
            <span class="c-val">{{ totalVolume }}</span>
            <span class="c-lbl">总容量(kg)</span>
          </div>
        </div>
        <el-button type="primary" size="large" round :loading="saving" @click="submitSession" class="submit-btn">
          <el-icon><Select /></el-icon> 提交训练记录
        </el-button>
      </div>

      <!-- 训练备注 -->
      <div v-if="!allDone" class="note-section">
        <el-input
          v-model="sessionNote"
          type="textarea"
          :rows="2"
          placeholder="记录训练感受（选填）"
          maxlength="200"
          show-word-limit
        />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { workoutApi } from '@/api/workout'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Clock, CircleCheckFilled, Circle, Select } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const planId = Number(route.query.planId)
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const plan = ref(null)
const todayWorkout = ref(null)
const exerciseIndex = ref(0)
const setIndex = ref(0)

// 休息定时器
const timerRunning = ref(false)
const restCountdown = ref(0)
let timerInterval = null

const sessionNote = ref('')

// 获取计划的今日训练
const fetchPlanDetail = async () => {
  if (!planId) {
    error.value = '未指定训练计划'
    loading.value = false
    return
  }
  try {
    const detail = await workoutApi.getPlanDetail(planId)
    plan.value = detail

    // 判断今天星期几
    const today = new Date().getDay() // 0=周日, 1=周一, ..., 6=周六
    const todayDayOfWeek = today === 0 ? 7 : today // 转为 1-7

    // 查找今天的训练日
    const day = (detail.days || []).find(d => d.dayOfWeek === todayDayOfWeek)
    if (!day) {
      todayWorkout.value = null
      loading.value = false
      return
    }

    // 构建训练数据，为每个动作创建组
    const items = (day.items || []).map(item => ({
      exerciseId: item.exerciseId,
      exerciseName: item.exerciseName,
      sets: Array.from({ length: item.sets || 1 }, (_, i) => ({
        weight: item.weight || 0,
        reps: item.reps || 12,
        restSeconds: item.restSeconds || 60,
        done: false,
        // 为每组存储原始ID和序号（用于提交）
        originalItemId: item.id,
        setNumber: i + 1
      }))
    }))

    todayWorkout.value = { dayName: day.name || '训练日', items }
  } catch (e) {
    console.error('获取计划详情失败:', e)
    error.value = '获取训练计划失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 当前动作的所有组
const currentExerciseSets = computed(() => {
  if (!todayWorkout.value) return []
  const ex = todayWorkout.value.items[exerciseIndex.value]
  return ex ? ex.sets : []
})

// 当前组的数据
const currentItem = computed(() => {
  const sets = currentExerciseSets.value
  return sets[setIndex.value] || null
})

// 当前动作的总组数
const totalSetsForExercise = computed(() => currentExerciseSets.value.length)

// 当前组显示文本
const currentSetDisplay = computed(() => setIndex.value + 1)

// 总组数
const totalSetsCount = computed(() => {
  if (!todayWorkout.value) return 0
  return todayWorkout.value.items.reduce((sum, ex) => sum + ex.sets.length, 0)
})

// 已完成组数
const totalSetsDone = computed(() => {
  if (!todayWorkout.value) return 0
  let done = 0
  for (const ex of todayWorkout.value.items) {
    for (const s of ex.sets) {
      if (s.done) done++
    }
  }
  return done
})

// 总训练容量
const totalVolume = computed(() => {
  if (!todayWorkout.value) return 0
  let vol = 0
  for (const ex of todayWorkout.value.items) {
    for (const s of ex.sets) {
      if (s.done) vol += (s.weight || 0) * s.reps
    }
  }
  return vol
})

// 进度百分比
const progressPercent = computed(() => {
  const total = totalSetsCount.value
  if (total === 0) return 0
  return (totalSetsDone.value / total) * 100
})

// 是否全部完成
const allDone = computed(() => {
  return totalSetsCount.value > 0 && totalSetsDone.value === totalSetsCount.value
})

// 进度条颜色
const progressColor = computed(() => {
  if (progressPercent.value < 30) return '#f56c6c'
  if (progressPercent.value < 70) return '#e6a23c'
  return '#10b981'
})

// 完成一组
const completeSet = (idx) => {
  const ex = todayWorkout.value.items[exerciseIndex.value]
  if (!ex || ex.sets[idx].done || timerRunning.value) return

  ex.sets[idx].done = true

  // 检查是否还有下一组
  const nextSetIdx = idx + 1
  if (nextSetIdx < ex.sets.length) {
    // 还有下一组 -> 启动休息定时器
    setIndex.value = nextSetIdx
    startRest(ex.sets[idx].restSeconds || 60)
  } else {
    // 当前动作完成 -> 看下一个动作
    const nextExIdx = exerciseIndex.value + 1
    if (nextExIdx < todayWorkout.value.items.length) {
      exerciseIndex.value = nextExIdx
      setIndex.value = 0
      startRest(ex.sets[idx].restSeconds || 60)
    } else {
      // 全部完成！
      setIndex.value = 0
    }
  }
}

// 启动休息
const startRest = (seconds) => {
  timerRunning.value = true
  restCountdown.value = seconds
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    if (restCountdown.value <= 1) {
      clearInterval(timerInterval)
      timerInterval = null
      timerRunning.value = false
    } else {
      restCountdown.value--
    }
  }, 1000)
}

// 跳过休息
const skipRest = () => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
  timerRunning.value = false
  restCountdown.value = 0
}

// 格式化定时器
const formatTimer = (seconds) => {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

// 提交训练记录
const submitSession = async () => {
  if (!allDone.value) return
  saving.value = true
  try {
    // 构建提交数据
    const items = []
    for (const ex of todayWorkout.value.items) {
      ex.sets.forEach((set, i) => {
        items.push({
          exerciseId: ex.exerciseId,
          setNumber: i + 1,
          reps: set.reps,
          weight: set.weight > 0 ? set.weight : null,
          durationSeconds: null,
          completed: 1
        })
      })
    }

    const now = new Date()
    const startTime = now.toISOString().slice(0, 19).replace('T', ' ')
    const endTime = startTime // 近似

    const data = {
      name: plan.value?.name ? `${plan.value.name} - ${todayWorkout.value.dayName}` : '今日训练',
      planDayId: null,
      startTime,
      endTime,
      note: sessionNote.value || null,
      items
    }

    await workoutApi.createRecord(data)
    ElMessage.success('训练记录已保存！')
    router.push({ name: 'AppWorkoutRecord' })
  } catch (e) {
    console.error('提交训练失败:', e)
    ElMessage.error(e?.response?.data?.message || '提交失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

// 确认退出
const confirmExit = async () => {
  if (totalSetsDone.value > 0 && !allDone.value) {
    try {
      await ElMessageBox.confirm('训练尚未完成，确定要退出吗？进度将丢失', '提示', { type: 'warning' })
    } catch {
      return
    }
  }
  router.back()
}

// 返回计划
const goBack = () => {
  router.push({ name: 'AppWorkoutPlan' })
}

onMounted(() => {
  fetchPlanDetail()
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
})
</script>

<style scoped>
.workout-session-view {
  max-width: 700px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.session-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0 20px;
  position: sticky;
  top: 0;
  z-index: 10;
  background: var(--bg-base);
}

.session-title {
  flex: 1;
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-right {
  flex-shrink: 0;
}

.rotate-anim {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-wrap {
  padding-top: 40px;
}

/* 进度条 */
.progress-bar-wrap {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04);
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
}

.progress-percent {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
}

/* 当前动作卡片 */
.current-exercise-card {
  background: white;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04);
}

.exercise-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.exercise-name {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}

.exercise-set {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 20px;
}

.detail-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.detail-box {
  flex: 1;
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px;
  text-align: center;
}

.detail-label {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.detail-value {
  display: block;
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

/* 倒计时 */
.timer-section {
  text-align: center;
  padding: 20px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 16px;
}

.timer-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #1e293b;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}

.timer-circle.timer-warning {
  background: #ef4444;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.timer-value {
  font-size: 28px;
  font-weight: 800;
  color: white;
  font-family: monospace;
}

.timer-label {
  font-size: 14px;
  color: #92400e;
  font-weight: 600;
  margin: 0 0 12px;
}

.skip-btn {
  font-weight: 600;
}

/* 组卡片网格 */
.sets-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.set-card {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.set-card.set-active {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 4px 12px rgba(59,130,246,0.15);
}

.set-card.set-done {
  border-color: #10b981;
  background: #ecfdf5;
  opacity: 0.7;
  cursor: default;
}

.set-card:not(.set-done):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.set-num {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
}

.set-info {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}

.set-status {
  margin-top: 4px;
}

/* 导航点 */
.exercise-nav {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding: 16px 0;
  margin-bottom: 20px;
}

.nav-dot-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 60px;
}

.nav-label {
  font-size: 11px;
  color: #64748b;
  font-weight: 600;
  text-align: center;
}

.nav-dots {
  display: flex;
  gap: 4px;
}

.nav-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e2e8f0;
  transition: all 0.2s;
}

.nav-dot.active {
  background: #3b82f6;
  width: 10px;
  height: 10px;
}

.nav-dot.done {
  background: #10b981;
}

/* 完成区 */
.complete-section {
  text-align: center;
  padding: 40px 20px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04);
}

.complete-section h3 {
  font-size: 24px;
  font-weight: 800;
  margin: 16px 0 4px;
  color: #0f172a;
}

.complete-section p {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 24px;
}

.complete-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 32px;
}

.c-stat {
  text-align: center;
}

.c-val {
  display: block;
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
}

.c-lbl {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  font-weight: 600;
  margin-top: 4px;
}

.submit-btn {
  width: 220px;
  font-weight: 700;
  font-size: 16px;
  padding: 16px !important;
}

/* 备注 */
.note-section {
  margin-top: 12px;
}

/* 空状态 */
.loading-wrap {
  padding: 40px 0;
}
</style>
