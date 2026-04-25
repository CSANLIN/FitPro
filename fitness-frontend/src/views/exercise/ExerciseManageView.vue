<template>
  <div class="exercise-manage">
    <!-- 操作提示 -->
    <el-alert
      title="管理提示：删除分类前请确保该分类下没有动作"
      type="info"
      :closable="false"
      show-icon
      class="alert-bar"
    />

    <!-- 分类管理 & 动作管理 Tab -->
    <el-tabs v-model="activeTab" class="manage-tabs">
      <!-- ==================== 分类管理 ==================== -->
      <el-tab-pane label="分类管理" name="category">
        <div class="section-header">
          <span class="section-title">运动分类</span>
          <el-button type="primary" size="small" @click="openCategoryDialog()">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>

        <el-table :data="categories" stripe v-loading="categoryLoading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="120" />
          <el-table-column prop="name" label="分类名称" min-width="150" />
          <el-table-column prop="icon" label="图标" width="120" />
          <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
          <el-table-column prop="exerciseCount" label="动作数量" width="100" align="center" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openCategoryDialog(row)">编辑</el-button>
              <el-popconfirm
                title="确定删除该分类？"
                @confirm="handleDeleteCategory(row.id)"
              >
                <template #reference>
                  <el-button size="small" type="danger" :disabled="row.exerciseCount > 0">
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ==================== 动作管理 ==================== -->
      <el-tab-pane label="动作管理" name="exercise">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-form :inline="true" :model="exerciseQuery" size="small">
            <el-form-item label="分类">
              <el-select
                v-model="exerciseQuery.categoryId"
                placeholder="选择分类"
                clearable
                style="width: 140px"
              >
                <el-option
                  v-for="cat in categories"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="难度">
              <el-select
                v-model="exerciseQuery.difficulty"
                placeholder="难度等级"
                clearable
                style="width: 120px"
              >
                <el-option label="初级" value="BEGINNER" />
                <el-option label="中级" value="INTERMEDIATE" />
                <el-option label="高级" value="ADVANCED" />
              </el-select>
            </el-form-item>
            <el-form-item label="肌群">
              <el-select
                v-model="exerciseQuery.muscleGroup"
                placeholder="目标肌群"
                clearable
                style="width: 130px"
              >
                <el-option v-for="mg in muscleGroups" :key="mg" :label="mg" :value="mg" />
              </el-select>
            </el-form-item>
            <el-form-item label="器械">
              <el-select
                v-model="exerciseQuery.equipment"
                placeholder="所需器械"
                clearable
                style="width: 130px"
              >
                <el-option v-for="eq in equipmentList" :key="eq" :label="eq" :value="eq" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="exerciseQuery.keyword"
                placeholder="搜索动作名称"
                clearable
                style="width: 180px"
                @keyup.enter="handleExerciseSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleExerciseSearch">搜索</el-button>
              <el-button @click="resetExerciseSearch">重置</el-button>
            </el-form-item>
          </el-form>

          <el-button type="primary" size="small" @click="openExerciseDialog()">
            <el-icon><Plus /></el-icon>新增动作
          </el-button>
        </div>

        <!-- 动作表格 -->
        <el-table :data="exercises" v-loading="exerciseLoading" stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="100" />
          <el-table-column prop="name" label="动作名称" min-width="140" />
          <el-table-column prop="categoryName" label="所属分类" width="120" />
          <el-table-column prop="muscleGroup" label="目标肌群" min-width="160" show-overflow-tooltip />
          <el-table-column prop="equipment" label="所需器械" min-width="140" show-overflow-tooltip />
          <el-table-column prop="difficulty" label="难度" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="difficultyType(row.difficulty)" size="small">
                {{ difficultyLabel(row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openExerciseDialog(row)">编辑</el-button>
              <el-popconfirm title="确定删除该动作？" @confirm="handleDeleteExercise(row.id)">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrap" v-if="exerciseTotal > 0">
          <el-pagination
            v-model:current-page="exerciseQuery.pageNum"
            :page-size="exerciseQuery.pageSize"
            :total="exerciseTotal"
            layout="total, prev, pager, next"
            @current-change="fetchExercises"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 分类编辑弹窗 -->
    <el-dialog
      v-model="categoryDialogVisible"
      :title="editingCategoryId ? '编辑分类' : '新增分类'"
      width="420px"
      destroy-on-close
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="图标标识(可选)" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="categorySaving" @click="handleSaveCategory">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 动作编辑弹窗 -->
    <el-dialog
      v-model="exerciseDialogVisible"
      :title="editingExerciseId ? '编辑动作' : '新增动作'"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="exerciseFormRef"
        :model="exerciseForm"
        :rules="exerciseRules"
        label-width="90px"
      >
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="exerciseForm.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="动作名称" prop="name">
          <el-input v-model="exerciseForm.name" placeholder="请输入动作名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="exerciseForm.difficulty" placeholder="选择难度" style="width: 100%">
            <el-option label="初级 (BEGINNER)" value="BEGINNER" />
            <el-option label="中级 (INTERMEDIATE)" value="INTERMEDIATE" />
            <el-option label="高级 (ADVANCED)" value="ADVANCED" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标肌群" prop="muscleGroup">
          <el-input v-model="exerciseForm.muscleGroup" placeholder="多个肌群用逗号分隔" />
        </el-form-item>
        <el-form-item label="所需器械" prop="equipment">
          <el-input v-model="exerciseForm.equipment" placeholder="多个器械用逗号分隔" />
        </el-form-item>
        <el-form-item label="动作描述" prop="description">
          <el-input
            v-model="exerciseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入动作描述"
          />
        </el-form-item>
        <el-form-item label="示意图URL" prop="imageUrl">
          <el-input v-model="exerciseForm.imageUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="教学视频URL" prop="videoUrl">
          <el-input v-model="exerciseForm.videoUrl" placeholder="https://..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exerciseDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="exerciseSaving" @click="handleSaveExercise">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { exerciseApi } from '@/api/exercise'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const route = useRoute()

// ---------- 公共数据 ----------
const muscleGroups = ['胸部', '背部', '肩部', '肱二头肌', '肱三头肌', '腿部', '腹部', '臀部']
const equipmentList = ['杠铃', '哑铃', '壶铃', '弹力带', '健身椅', '龙门架', '引体向上杆', '瑜伽垫', '泡沫轴']

const difficultyLabel = (val) => ({ BEGINNER: '初级', INTERMEDIATE: '中级', ADVANCED: '高级' }[val] || val)
const difficultyType = (val) => ({ BEGINNER: 'success', INTERMEDIATE: 'warning', ADVANCED: 'danger' }[val] || 'info')

// ---------- Tab ----------
const activeTab = ref(route.path.includes('/list') ? 'exercise' : 'category')

// 根据路由切换 Tab
watch(() => route.path, (path) => {
  activeTab.value = path.includes('/list') ? 'exercise' : 'category'
})

// ---------- 分类管理 ----------
const categories = ref([])
const categoryLoading = ref(false)
const categoryDialogVisible = ref(false)
const categorySaving = ref(false)
const editingCategoryId = ref(null)

const categoryForm = reactive({
  name: '',
  icon: '',
  sortOrder: 0
})

const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const categoryFormRef = ref(null)

const fetchCategories = async () => {
  categoryLoading.value = true
  try {
    categories.value = await exerciseApi.listCategories()
  } catch (e) {
    console.error('获取分类列表失败:', e)
  } finally {
    categoryLoading.value = false
  }
}

const openCategoryDialog = (row) => {
  if (row) {
    editingCategoryId.value = row.id
    categoryForm.name = row.name
    categoryForm.icon = row.icon || ''
    categoryForm.sortOrder = row.sortOrder
  } else {
    editingCategoryId.value = null
    categoryForm.name = ''
    categoryForm.icon = ''
    categoryForm.sortOrder = 0
  }
  categoryDialogVisible.value = true
}

const handleSaveCategory = async () => {
  const valid = await categoryFormRef.value.validate().catch(() => false)
  if (!valid) return

  categorySaving.value = true
  try {
    if (editingCategoryId.value) {
      await exerciseApi.updateCategory({ id: editingCategoryId.value, ...categoryForm })
      ElMessage.success('分类更新成功')
    } else {
      await exerciseApi.createCategory(categoryForm)
      ElMessage.success('分类创建成功')
    }
    categoryDialogVisible.value = false
    await fetchCategories()
  } catch (e) {
    console.error('保存分类失败:', e)
  } finally {
    categorySaving.value = false
  }
}

const handleDeleteCategory = async (id) => {
  try {
    await exerciseApi.deleteCategory(id)
    ElMessage.success('分类已删除')
    await fetchCategories()
  } catch (e) {
    console.error('删除分类失败:', e)
  }
}

// ---------- 动作管理 ----------
const exercises = ref([])
const exerciseTotal = ref(0)
const exerciseLoading = ref(false)
const exerciseDialogVisible = ref(false)
const exerciseSaving = ref(false)
const editingExerciseId = ref(null)

const exerciseQuery = reactive({
  categoryId: undefined,
  difficulty: '',
  muscleGroup: '',
  equipment: '',
  keyword: '',
  pageNum: 1,
  pageSize: 20
})

const exerciseForm = reactive({
  categoryId: null,
  name: '',
  difficulty: 'BEGINNER',
  muscleGroup: '',
  equipment: '',
  description: '',
  imageUrl: '',
  videoUrl: ''
})

const exerciseRules = {
  categoryId: [{ required: true, message: '请选择所属分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入动作名称', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }]
}

const exerciseFormRef = ref(null)

const fetchExercises = async () => {
  exerciseLoading.value = true
  try {
    const res = await exerciseApi.list({ ...exerciseQuery })
    exercises.value = res.list
    exerciseTotal.value = res.total
  } catch (e) {
    console.error('获取动作列表失败:', e)
  } finally {
    exerciseLoading.value = false
  }
}

const handleExerciseSearch = () => {
  exerciseQuery.pageNum = 1
  fetchExercises()
}

const resetExerciseSearch = () => {
  exerciseQuery.categoryId = undefined
  exerciseQuery.difficulty = ''
  exerciseQuery.muscleGroup = ''
  exerciseQuery.equipment = ''
  exerciseQuery.keyword = ''
  exerciseQuery.pageNum = 1
  fetchExercises()
}

const openExerciseDialog = (row) => {
  if (row) {
    editingExerciseId.value = row.id
    exerciseForm.categoryId = row.categoryId
    exerciseForm.name = row.name
    exerciseForm.difficulty = row.difficulty
    exerciseForm.muscleGroup = row.muscleGroup || ''
    exerciseForm.equipment = row.equipment || ''
    exerciseForm.description = row.description || ''
    exerciseForm.imageUrl = row.imageUrl || ''
    exerciseForm.videoUrl = row.videoUrl || ''
  } else {
    editingExerciseId.value = null
    exerciseForm.categoryId = null
    exerciseForm.name = ''
    exerciseForm.difficulty = 'BEGINNER'
    exerciseForm.muscleGroup = ''
    exerciseForm.equipment = ''
    exerciseForm.description = ''
    exerciseForm.imageUrl = ''
    exerciseForm.videoUrl = ''
  }
  exerciseDialogVisible.value = true
}

const handleSaveExercise = async () => {
  const valid = await exerciseFormRef.value.validate().catch(() => false)
  if (!valid) return

  exerciseSaving.value = true
  try {
    const data = { ...exerciseForm }
    if (editingExerciseId.value) {
      await exerciseApi.update({ id: editingExerciseId.value, ...data })
      ElMessage.success('动作更新成功')
    } else {
      await exerciseApi.create(data)
      ElMessage.success('动作创建成功')
    }
    exerciseDialogVisible.value = false
    await fetchExercises()
  } catch (e) {
    console.error('保存动作失败:', e)
  } finally {
    exerciseSaving.value = false
  }
}

const handleDeleteExercise = async (id) => {
  try {
    await exerciseApi.delete(id)
    ElMessage.success('动作已删除')
    await fetchExercises()
  } catch (e) {
    console.error('删除动作失败:', e)
  }
}

// 初始化
onMounted(() => {
  fetchCategories()
  fetchExercises()
})
</script>

<style scoped>
.exercise-manage {
  padding: 0;
}

.alert-bar {
  margin-bottom: 16px;
}

.manage-tabs {
  background: white;
  border-radius: 8px;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.search-bar .el-form {
  flex: 1;
}

.search-bar .el-form-item {
  margin-bottom: 8px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding: 12px 0;
}
</style>
