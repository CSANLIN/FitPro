<template>
  <div class="course-list-view">
    <!-- 类型筛选 Tabs -->
    <div class="type-tabs">
      <el-tabs v-model="courseType" @tab-change="handleTypeChange">
        <el-tab-pane label="全部课程" name=""></el-tab-pane>
        <el-tab-pane label="瑜伽" name="YOGA"></el-tab-pane>
        <el-tab-pane label="搏击" name="BOXING"></el-tab-pane>
        <el-tab-pane label="动感单车" name="SPINNING"></el-tab-pane>
        <el-tab-pane label="HIIT" name="HIIT"></el-tab-pane>
        <el-tab-pane label="其他" name="OTHER"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 课程卡片列表 -->
    <div v-loading="loading" class="course-grid">
      <el-empty v-if="!loading && courses.length === 0" description="暂无课程" />

      <div v-else class="card-grid">
        <el-card
          v-for="course in courses"
          :key="course.id"
          class="course-card"
          shadow="hover"
          @click="viewSchedules(course)"
        >
          <div class="card-image">
            <el-image
              :src="course.coverImage || defaultImage"
              fit="cover"
              style="width: 100%; height: 160px"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="40"><Notebook /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="card-type-tag">
              <el-tag size="small" effect="dark">{{ typeLabel(course.courseType) }}</el-tag>
            </div>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ course.name }}</h3>
            <div class="card-meta">
              <span><el-icon><Clock /></el-icon> {{ course.durationMinutes }}分钟</span>
              <span><el-icon><User /></el-icon> 最多{{ course.maxCapacity }}人</span>
            </div>
            <p class="card-desc">{{ course.description || '暂无描述' }}</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi } from '@/api/course'
import { Notebook, Clock, User } from '@element-plus/icons-vue'

const router = useRouter()

const courses = ref([])
const loading = ref(false)
const courseType = ref('')

const defaultImage = ''

const typeLabel = (val) => ({
  YOGA: '瑜伽',
  BOXING: '搏击',
  SPINNING: '动感单车',
  HIIT: 'HIIT',
  OTHER: '其他'
}[val] || val)

const fetchCourses = async () => {
  loading.value = true
  try {
    courses.value = await courseApi.list({ courseType: courseType.value || undefined })
  } catch (e) {
    console.error('获取课程列表失败:', e)
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
}

.type-tabs {
  background: white;
  border-radius: 12px;
  padding: 16px 20px 0;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.course-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.course-card:hover {
  transform: translateY(-2px);
}

.card-image {
  position: relative;
  overflow: hidden;
}

.image-placeholder {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea22 0%, #764ba222 100%);
  color: var(--el-color-primary-light-3);
}

.card-type-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}

.card-body {
  padding: 12px 0 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--el-text-color-primary);
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-desc {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
