<template>
  <div class="exercise-list-view">
    <div class="page-header">
      <h2 class="page-title">动作探索</h2>
      <p class="page-subtitle">构建你的专属训练库，解锁无限可能</p>
    </div>

    <!-- 筛选区域 (Premium Glassmorphism) -->
    <div class="premium-filter-container">
      <div class="filter-main">
        <el-input
          v-model="query.keyword"
          placeholder="搜索你想练的动作..."
          clearable
          class="search-input"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable @change="handleSearch" class="filter-select">
          <el-option label="全部分类" value=""></el-option>
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        
        <el-select v-model="query.muscleGroup" placeholder="目标肌群" clearable @change="handleSearch" class="filter-select">
          <el-option label="所有肌群" value=""></el-option>
          <el-option v-for="mg in muscleGroups" :key="mg" :label="mg" :value="mg" />
        </el-select>

        <el-select v-model="query.equipment" placeholder="所需器械" clearable @change="handleSearch" class="filter-select">
          <el-option label="所有器械" value=""></el-option>
          <el-option v-for="eq in equipmentList" :key="eq" :label="eq" :value="eq" />
        </el-select>

        <el-select v-model="query.difficulty" placeholder="难度等级" clearable @change="handleSearch" class="filter-select">
          <el-option label="所有难度" value=""></el-option>
          <el-option label="初级 (Beginner)" value="BEGINNER"></el-option>
          <el-option label="中级 (Intermediate)" value="INTERMEDIATE"></el-option>
          <el-option label="高级 (Advanced)" value="ADVANCED"></el-option>
        </el-select>
      </div>
    </div>

    <!-- 动作卡片列表 -->
    <div v-loading="loading" class="exercise-grid-section">
      <el-empty v-if="!loading && exercises.length === 0" description="未能找到匹配的动作，尝试更换搜索词" />

      <div v-else class="card-masonry">
        <div
          v-for="item in exercises"
          :key="item.id"
          class="premium-exercise-card"
          @click="showDetail(item)"
        >
          <div class="card-img-wrapper">
            <el-image
              :src="item.imageUrl || defaultImage"
              fit="cover"
              class="card-img"
            >
              <template #error>
                <div class="img-placeholder">
                  <el-icon :size="40"><Basketball /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="img-overlay">
              <span class="view-detail-text">查看详解</span>
            </div>
          </div>
          
          <div class="card-info">
            <div class="info-header">
              <h3 class="exercise-title">{{ item.name }}</h3>
              <el-tag size="small" :type="difficultyType(item.difficulty)" effect="dark" round class="diff-tag">
                {{ difficultyLabel(item.difficulty) }}
              </el-tag>
            </div>
            
            <p class="exercise-desc">{{ item.description || '解锁这个动作，感受肌肉的燃烧与力量的爆发。' }}</p>
            
            <div class="info-footer">
              <span class="meta-badge" v-if="item.muscleGroup">
                <span class="dot primary"></span> {{ item.muscleGroup }}
              </span>
              <span class="meta-badge" v-if="item.equipment">
                <span class="dot warning"></span> {{ item.equipment }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          background
          layout="prev, pager, next"
          @current-change="fetchExercises"
        />
      </div>
    </div>

    <!-- 高级动作详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="动作解析"
      width="90%"
      max-width="650px"
      top="8vh"
      destroy-on-close
      class="premium-dialog"
    >
      <template v-if="currentExercise">
        <div class="detail-hero">
          <el-image
            :src="currentExercise.imageUrl || defaultImage"
            fit="cover"
            class="detail-hero-img"
          >
            <template #error>
              <div class="img-placeholder hero">
                <el-icon :size="64"><Basketball /></el-icon>
              </div>
            </template>
          </el-image>
          
          <div class="hero-overlay">
            <h2 class="detail-title">{{ currentExercise.name }}</h2>
            <div class="detail-tags">
              <el-tag effect="dark" :type="difficultyType(currentExercise.difficulty)" round>
                {{ difficultyLabel(currentExercise.difficulty) }}
              </el-tag>
              <el-tag effect="dark" type="info" round v-if="currentExercise.categoryName">
                {{ currentExercise.categoryName }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="detail-content">
          <div class="spec-grid">
            <div class="spec-item">
              <div class="spec-icon"><el-icon><CopyDocument /></el-icon></div>
              <div class="spec-text">
                <span class="label">目标肌群</span>
                <span class="value">{{ currentExercise.muscleGroup || '综合' }}</span>
              </div>
            </div>
            <div class="spec-item">
              <div class="spec-icon"><el-icon><Tools /></el-icon></div>
              <div class="spec-text">
                <span class="label">所需器械</span>
                <span class="value">{{ currentExercise.equipment || '自重' }}</span>
              </div>
            </div>
          </div>

          <div class="instruction-block">
            <h3>动作要领</h3>
            <p>{{ currentExercise.description || '暂无详细描述，建议参考视频教学或咨询教练。' }}</p>
          </div>
        </div>

        <div class="detail-action" v-if="currentExercise.videoUrl">
          <el-button color="#10b981" class="video-btn" round size="large" @click="openVideo(currentExercise.videoUrl)">
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
import { Search, Basketball, VideoPlay, CopyDocument, Tools } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const defaultImage = 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?auto=format&fit=crop&w=800&q=80'

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
.exercise-list-view {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
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

/* 筛选区 */
.premium-filter-container {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.6);
  position: sticky;
  top: 70px; /* 留出 header 高度 */
  z-index: 10;
}

.filter-main {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

:deep(.search-input .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02) !important;
}

.filter-select {
  width: 140px;
}

:deep(.filter-select .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02) !important;
}

/* 卡片网格 */
.card-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

/* 高级动作卡片 */
.premium-exercise-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
}

.premium-exercise-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.08);
}

.card-img-wrapper {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.card-img {
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
}

.premium-exercise-card:hover .card-img {
  transform: scale(1.08);
}

.img-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #cbd5e1;
}

.img-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.premium-exercise-card:hover .img-overlay {
  opacity: 1;
}

.view-detail-text {
  color: white;
  font-weight: 600;
  font-size: 14px;
  border: 1px solid white;
  padding: 6px 16px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
}

.card-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
  gap: 8px;
}

.exercise-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.3;
}

.diff-tag {
  flex-shrink: 0;
}

.exercise-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 16px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.info-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.meta-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #475569;
  font-weight: 500;
  background: #f8fafc;
  padding: 4px 10px;
  border-radius: 6px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.dot.primary { background: var(--primary-color); }
.dot.warning { background: #f59e0b; }

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 详情弹窗增强 */
.detail-hero {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
}

.detail-hero-img {
  width: 100%;
  height: 240px;
  display: block;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(15, 23, 42, 0.8) 0%, transparent 80%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 24px;
}

.detail-title {
  color: white;
  margin: 0 0 12px;
  font-size: 24px;
  font-weight: 800;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.spec-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
}

.spec-icon {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.spec-text {
  display: flex;
  flex-direction: column;
}

.spec-text .label {
  font-size: 12px;
  color: #64748b;
}

.spec-text .value {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.instruction-block {
  background: white;
  border-radius: 12px;
  padding: 0;
}

.instruction-block h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 12px;
}

.instruction-block p {
  color: #475569;
  line-height: 1.7;
  font-size: 14px;
  margin: 0;
  white-space: pre-wrap;
}

.detail-action {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.video-btn {
  width: 100%;
  max-width: 300px;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 8px 20px rgba(16, 185, 129, 0.3);
}

@media (max-width: 768px) {
  .filter-select {
    width: 48%;
    flex: auto;
  }
  
  .card-masonry {
    grid-template-columns: 1fr;
  }
  
  .spec-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-hero-img {
    height: 180px;
  }
}
</style>
