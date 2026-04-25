<template>
  <div class="exercise-list">
    <!-- 分类 Tab -->
    <div class="category-tabs">
      <el-tabs v-model="query.categoryId" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部" :name="undefined"></el-tab-pane>
        <el-tab-pane
          v-for="cat in categories"
          :key="cat.id"
          :label="cat.name"
          :name="cat.id"
        >
          <template #label>
            <span class="tab-label">
              {{ cat.name }}
              <el-tag size="small" type="info" effect="plain">{{ cat.exerciseCount }}</el-tag>
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="肌群">
          <el-select v-model="query.muscleGroup" placeholder="目标肌群" clearable @change="handleSearch">
            <el-option label="全部" value=""></el-option>
            <el-option v-for="mg in muscleGroups" :key="mg" :label="mg" :value="mg" />
          </el-select>
        </el-form-item>
        <el-form-item label="器械">
          <el-select v-model="query.equipment" placeholder="所需器械" clearable @change="handleSearch">
            <el-option label="全部" value=""></el-option>
            <el-option v-for="eq in equipmentList" :key="eq" :label="eq" :value="eq" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="query.difficulty" placeholder="难度等级" clearable @change="handleSearch">
            <el-option label="全部" value=""></el-option>
            <el-option label="初级" value="BEGINNER"></el-option>
            <el-option label="中级" value="INTERMEDIATE"></el-option>
            <el-option label="高级" value="ADVANCED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="query.keyword"
            placeholder="搜索动作名称"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>

    <!-- 动作卡片列表 -->
    <div v-loading="loading" class="exercise-grid">
      <el-empty v-if="!loading && exercises.length === 0" description="暂无运动动作" />

      <div v-else class="card-grid">
        <el-card
          v-for="item in exercises"
          :key="item.id"
          class="exercise-card"
          shadow="hover"
          @click="showDetail(item)"
        >
          <div class="card-body">
            <div class="card-image">
              <el-image
                :src="item.imageUrl || defaultImage"
                fit="cover"
                style="width: 100%; height: 140px"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="32"><Basketball /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="card-info">
              <h3 class="card-title">{{ item.name }}</h3>
              <div class="card-tags">
                <el-tag size="small" :type="difficultyType(item.difficulty)">
                  {{ difficultyLabel(item.difficulty) }}
                </el-tag>
                <el-tag size="small" v-if="item.muscleGroup" type="success" effect="plain">
                  {{ item.muscleGroup }}
                </el-tag>
              </div>
              <p class="card-desc">{{ item.description || '暂无描述' }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > 0">
        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchExercises"
        />
      </div>
    </div>

    <!-- 动作详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentExercise?.name"
      width="600px"
      top="5vh"
      destroy-on-close
    >
      <template v-if="currentExercise">
        <div class="detail-header">
          <el-image
            :src="currentExercise.imageUrl || defaultImage"
            fit="cover"
            style="width: 100%; height: 200px; border-radius: 8px"
          >
            <template #error>
              <div class="image-placeholder" style="height: 200px">
                <el-icon :size="48"><Basketball /></el-icon>
              </div>
            </template>
          </el-image>
        </div>

        <el-descriptions :column="2" border class="detail-body">
          <el-descriptions-item label="分类" :span="2">
            {{ currentExercise.categoryName }}
          </el-descriptions-item>
          <el-descriptions-item label="难度">
            <el-tag :type="difficultyType(currentExercise.difficulty)" size="small">
              {{ difficultyLabel(currentExercise.difficulty) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="目标肌群">
            {{ currentExercise.muscleGroup || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="所需器械" :span="2">
            {{ currentExercise.equipment || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="动作描述" :span="2">
            {{ currentExercise.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-video" v-if="currentExercise.videoUrl">
          <el-button type="primary" @click="openVideo(currentExercise.videoUrl)">
            <el-icon><VideoPlay /></el-icon>
            观看教学视频
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { exerciseApi } from '@/api/exercise'
import { Search, Basketball, VideoPlay } from '@element-plus/icons-vue'

const defaultImage = ''

// 数据
const categories = ref([])
const exercises = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const currentExercise = ref(null)

// 预设筛选选项
const muscleGroups = ref(['胸部', '背部', '肩部', '肱二头肌', '肱三头肌', '腿部', '腹部', '臀部'])
const equipmentList = ref(['杠铃', '哑铃', '壶铃', '弹力带', '健身椅', '龙门架', '引体向上杆', '瑜伽垫', '泡沫轴'])

// 查询参数
const query = reactive({
  categoryId: undefined,
  muscleGroup: '',
  equipment: '',
  difficulty: '',
  keyword: '',
  pageNum: 1,
  pageSize: 20
})

// 方法
const difficultyLabel = (val) => {
  const map = { BEGINNER: '初级', INTERMEDIATE: '中级', ADVANCED: '高级' }
  return map[val] || val
}

const difficultyType = (val) => {
  const map = { BEGINNER: 'success', INTERMEDIATE: 'warning', ADVANCED: 'danger' }
  return map[val] || 'info'
}

const fetchCategories = async () => {
  try {
    categories.value = await exerciseApi.listCategories()
  } catch (e) {
    console.error('获取运动分类失败:', e)
  }
}

const fetchExercises = async () => {
  loading.value = true
  try {
    const res = await exerciseApi.list({ ...query })
    exercises.value = res.list
    total.value = res.total
  } catch (e) {
    console.error('获取运动动作失败:', e)
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  query.pageNum = 1
  fetchExercises()
}

const handleSearch = () => {
  query.pageNum = 1
  fetchExercises()
}

const showDetail = (item) => {
  currentExercise.value = item
  detailVisible.value = true
}

const openVideo = (url) => {
  window.open(url, '_blank')
}

// 初始化
onMounted(() => {
  fetchCategories()
  fetchExercises()
})
</script>

<style scoped>
.exercise-list {
  max-width: 1100px;
  margin: 0 auto;
}

.category-tabs {
  background: white;
  border-radius: 12px;
  padding: 16px 20px 0;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-bar {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.filter-bar .el-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-bar .el-form-item {
  margin-bottom: 0;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.exercise-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.exercise-card:hover {
  transform: translateY(-2px);
}

.card-body {
  display: flex;
  flex-direction: column;
}

.card-image {
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.image-placeholder {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea22 0%, #764ba222 100%);
  color: var(--el-color-primary-light-3);
}

.card-info {
  padding: 12px 0 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--el-text-color-primary);
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 8px;
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

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px 0;
}

/* 详情弹窗 */
.detail-header {
  margin-bottom: 20px;
}

.detail-body {
  margin-bottom: 16px;
}

.detail-video {
  text-align: center;
  margin-top: 16px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }

  .filter-bar .el-form {
    flex-direction: column;
  }

  .category-tabs {
    padding: 12px 12px 0;
  }
}
</style>
