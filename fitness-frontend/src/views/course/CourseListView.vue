<template>
  <div class="course-list-view">
    <div class="page-header">
      <h2 class="page-title">探索课程</h2>
      <p class="page-subtitle">找到最适合你的训练节奏</p>
    </div>

    <!-- 类型筛选 Tabs (Premium Design) -->
    <div class="type-tabs-wrapper">
      <el-tabs v-model="courseType" @tab-change="handleTypeChange" class="premium-tabs">
        <el-tab-pane label="全部课程" name=""></el-tab-pane>
        <el-tab-pane label="瑜伽" name="瑜伽"></el-tab-pane>
        <el-tab-pane label="力量训练" name="力量训练"></el-tab-pane>
        <el-tab-pane label="有氧运动" name="有氧运动"></el-tab-pane>
        <el-tab-pane label="舞蹈" name="舞蹈"></el-tab-pane>
        <el-tab-pane label="格斗" name="格斗"></el-tab-pane>
        <el-tab-pane label="综合体能" name="综合"></el-tab-pane>
        <el-tab-pane label="其他" name="其他"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 课程卡片列表 -->
    <div v-loading="loading" class="course-grid">
      <el-empty v-if="!loading && courses.length === 0" description="暂无课程" />

      <div v-else class="card-grid">
        <div
          v-for="course in courses"
          :key="course.id"
          class="premium-course-card"
          @click="viewSchedules(course)"
        >
          <div class="card-image">
            <el-image
              :src="course.coverImage || defaultImage"
              fit="cover"
              class="image-el"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="48"><Notebook /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="card-overlay"></div>
            <div class="card-type-tag">
              <span class="glass-tag">{{ typeLabel(course.courseType) }}</span>
            </div>
          </div>
          
          <div class="card-content">
            <h3 class="card-title">{{ course.name }}</h3>
            <p class="card-desc">{{ course.description || '解锁身体潜能，感受每一次心跳的律动。' }}</p>
            
            <div class="card-footer">
              <div class="meta-item">
                <div class="meta-icon"><el-icon><Clock /></el-icon></div>
                <span>{{ course.durationMinutes }} Min</span>
              </div>
              <div class="meta-divider"></div>
              <div class="meta-item">
                <div class="meta-icon"><el-icon><User /></el-icon></div>
                <span>Max {{ course.maxCapacity }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi } from '@/api/course'
import { Notebook, Clock, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const courses = ref([])
const loading = ref(false)
const courseType = ref('')

// Use a better default image for the premium look
const defaultImage = 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=800&q=80'

const typeLabel = (val) => val || '其他'

const fetchCourses = async () => {
  loading.value = true
  try {
    courses.value = await courseApi.list({ courseType: courseType.value || undefined })
  } catch (e) {
    console.error('获取课程列表失败:', e)
    ElMessage.error('获取课程列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleTypeChange = () => {
  fetchCourses()
}

const viewSchedules = (course) => {
  router.push({ name: 'AppCourseBooking', query: { courseId: course.id, courseName: course.name } })
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.course-list-view {
  max-width: 1100px;
  margin: 0 auto;
  padding-bottom: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  margin-top: 8px;
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  margin: 0 0 4px;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 标签页优化 */
.type-tabs-wrapper {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 12px 20px 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

:deep(.premium-tabs .el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f1f5f9;
}

:deep(.premium-tabs .el-tabs__item) {
  font-size: 15px;
  font-weight: 600;
  color: #64748b;
  transition: all 0.3s ease;
}

:deep(.premium-tabs .el-tabs__item.is-active) {
  color: var(--primary-color);
  font-size: 16px;
}

:deep(.premium-tabs .el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, var(--primary-color), #8b5cf6);
}

/* 卡片网格 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

/* 高级卡片设计 */
.premium-course-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  flex-direction: column;
}

.premium-course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.image-el {
  width: 100%;
  height: 100%;
  transition: transform 0.6s ease;
}

.premium-course-card:hover .image-el {
  transform: scale(1.05);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(15, 23, 42, 0.6) 0%, transparent 60%);
  pointer-events: none;
}

.image-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  color: #94a3b8;
}

.card-type-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.glass-tag {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.card-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
}

.card-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.6;
  flex: 1;
}

.card-footer {
  display: flex;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.meta-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: #f1f5f9;
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.meta-divider {
  width: 1px;
  height: 16px;
  background: #e2e8f0;
  margin: 0 16px;
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
