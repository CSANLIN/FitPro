<template>
  <div class="template-manage-view">
    <div class="page-header">
      <h2>训练模板管理</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>新建模板
      </el-button>
    </div>

    <!-- 模板列表 -->
    <el-card v-loading="loading" class="template-card">
      <el-empty v-if="!loading && templates.length === 0" description="暂无训练模板" />

      <div v-else class="template-list">
        <div v-for="tmpl in templates" :key="tmpl.id" class="template-item">
          <div class="template-info" @click="showTemplateDetail(tmpl.id)">
            <h3 class="template-name">{{ tmpl.name }}</h3>
            <p class="template-desc">{{ tmpl.description || '暂无描述' }}</p>
            <div class="template-meta">
              <el-tag size="small" :type="difficultyType(tmpl.difficulty)">
                {{ difficultyLabel(tmpl.difficulty) }}
              </el-tag>
              <el-tag size="small" type="info" effect="plain" v-if="tmpl.targetType">
                {{ targetLabel(tmpl.targetType) }}
              </el-tag>
              <span class="item-count">{{ tmpl.itemCount }}个动作</span>
            </div>
          </div>
          <div class="template-actions">
            <el-button size="small" @click="showTemplateDetail(tmpl.id)">查看</el-button>
            <el-button size="small" @click="editTemplate(tmpl.id)">编辑</el-button>
            <el-popconfirm title="确定删除该模板？" @confirm="handleDeleteTemplate(tmpl.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 模板详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentTemplate?.name" width="650px" top="5vh" destroy-on-close>
      <template v-if="currentTemplate">
        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="模板名称" :span="2">{{ currentTemplate.name }}</el-descriptions-item>
          <el-descriptions-item label="难度">
            <el-tag :type="difficultyType(currentTemplate.difficulty)" size="small">
              {{ difficultyLabel(currentTemplate.difficulty) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="目标">
            {{ targetLabel(currentTemplate.targetType) || '不限' }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2" v-if="currentTemplate.description">
            {{ currentTemplate.description }}
          </el-descriptions-item>
        </el-descriptions>

        <h4 class="items-title">动作列表</h4>
        <el-table :data="currentTemplate.items" stripe size="small" style="width: 100%">
          <el-table-column prop="exerciseName" label="动作名称" min-width="120" />
          <el-table-column prop="sets" label="组数" width="60" align="center" />
          <el-table-column prop="reps" label="次数" width="60" align="center" />
          <el-table-column prop="restSeconds" label="休息(s)" width="80" align="center" />
          <el-table-column prop="sortOrder" label="排序" width="60" align="center" />
        </el-table>
      </template>
    </el-dialog>

    <!-- 新建/编辑模板弹窗 -->
    <el-dialog
      v-model="createVisible"
      :title="editingId ? '编辑模板' : '新建模板'"
      width="700px"
      top="5vh"
      destroy-on-close
    >
      <el-form ref="formRef" :model="templateForm" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="templateForm.name" placeholder="例如：新手全身训练" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="templateForm.difficulty" placeholder="选择" style="width: 100%">
                <el-option label="初级" value="BEGINNER" />
                <el-option label="中级" value="INTERMEDIATE" />
                <el-option label="高级" value="ADVANCED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="目标" prop="targetType">
              <el-select v-model="templateForm.targetType" placeholder="选择" clearable style="width: 100%">
                <el-option label="减脂" value="FAT_LOSS" />
                <el-option label="增肌" value="MUSCLE_GAIN" />
                <el-option label="塑形" value="SHAPE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="templateForm.description" type="textarea" :rows="2" placeholder="模板描述(可选)" />
        </el-form-item>

        <el-divider>动作列表</el-divider>

        <div v-for="(item, index) in templateForm.items" :key="index" class="item-row">
          <el-row :gutter="8" align="middle">
            <el-col :span="8">
              <el-form-item
                :label="`#${index + 1}`"
                :prop="`items.${index}.exerciseId`"
                :rules="formRules.exerciseId"
                label-width="35px"
              >
                <el-select v-model="item.exerciseId" placeholder="选择动作" filterable style="width: 100%">
                  <el-option v-for="e in exercises" :key="e.id" :label="e.name" :value="e.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-input-number v-model="item.sets" :min="1" :max="20" size="small" placeholder="组数" style="width: 100%" controls-position="right" />
            </el-col>
            <el-col :span="4">
              <el-input-number v-model="item.reps" :min="1" :max="999" size="small" placeholder="次数" style="width: 100%" controls-position="right" />
            </el-col>
            <el-col :span="4">
              <el-input-number v-model="item.restSeconds" :min="0" :max="600" :step="15" size="small" placeholder="休息(s)" style="width: 100%" controls-position="right" />
            </el-col>
            <el-col :span="2">
              <el-button type="danger" size="small" text @click="removeItem(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-col>
          </el-row>
        </div>

        <el-button type="primary" plain @click="addItem" class="add-item-btn">
          <el-icon><Plus /></el-icon>添加动作
        </el-button>
      </el-form>

      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveTemplate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { workoutApi } from '@/api/workout'
import { exerciseApi } from '@/api/exercise'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

// 数据
const templates = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const createVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const currentTemplate = ref(null)
const exercises = ref([])
const formRef = ref(null)

const templateForm = reactive({
  name: '',
  description: '',
  targetType: '',
  difficulty: 'BEGINNER',
  items: []
})

const formRules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  exerciseId: [{ required: true, message: '请选择动作', trigger: 'change' }]
}

// 标签辅助
const difficultyLabel = (val) => ({ BEGINNER: '初级', INTERMEDIATE: '中级', ADVANCED: '高级' }[val] || val)
const difficultyType = (val) => ({ BEGINNER: 'success', INTERMEDIATE: 'warning', ADVANCED: 'danger' }[val] || 'info')
const targetLabel = (val) => ({ FAT_LOSS: '减脂', MUSCLE_GAIN: '增肌', SHAPE: '塑形' }[val] || val)

// 加载
const fetchTemplates = async () => {
  loading.value = true
  try {
    templates.value = await workoutApi.listTemplates()
  } catch (e) {
    console.error('获取模板失败:', e)
  } finally {
    loading.value = false
  }
}

const fetchExercises = async () => {
  try {
    const res = await exerciseApi.list({ pageNum: 1, pageSize: 999 })
    exercises.value = res.list || []
  } catch (e) {
    console.error('获取动作失败:', e)
  }
}

// 查看详情
const showTemplateDetail = async (id) => {
  try {
    currentTemplate.value = await workoutApi.getTemplateDetail(id)
    detailVisible.value = true
  } catch (e) {
    console.error('获取模板详情失败:', e)
  }
}

// 编辑模板
const editTemplate = async (id) => {
  try {
    const detail = await workoutApi.getTemplateDetail(id)
    editingId.value = id
    templateForm.name = detail.name
    templateForm.description = detail.description || ''
    templateForm.targetType = detail.targetType || ''
    templateForm.difficulty = detail.difficulty
    templateForm.items = detail.items.map(item => ({
      exerciseId: item.exerciseId,
      sets: item.sets,
      reps: item.reps,
      restSeconds: item.restSeconds
    }))
    createVisible.value = true
  } catch (e) {
    console.error('获取模板详情失败:', e)
  }
}

// 新建
const showCreateDialog = () => {
  editingId.value = null
  templateForm.name = ''
  templateForm.description = ''
  templateForm.targetType = ''
  templateForm.difficulty = 'BEGINNER'
  templateForm.items = []
  createVisible.value = true
}

const addItem = () => {
  templateForm.items.push({
    exerciseId: null,
    sets: 3,
    reps: 12,
    restSeconds: 60
  })
}

const removeItem = (index) => {
  templateForm.items.splice(index, 1)
}

// 保存
const handleSaveTemplate = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    const data = {
      name: templateForm.name,
      description: templateForm.description || null,
      targetType: templateForm.targetType || null,
      difficulty: templateForm.difficulty,
      items: templateForm.items.map((item, i) => ({
        exerciseId: item.exerciseId,
        sets: item.sets,
        reps: item.reps,
        restSeconds: item.restSeconds,
        sortOrder: i
      }))
    }

    if (editingId.value) {
      await workoutApi.updateTemplate({ id: editingId.value, ...data })
      ElMessage.success('模板更新成功')
    } else {
      await workoutApi.createTemplate(data)
      ElMessage.success('模板创建成功')
    }

    createVisible.value = false
    await fetchTemplates()
  } catch (e) {
    console.error('保存模板失败:', e)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDeleteTemplate = async (id) => {
  try {
    await workoutApi.deleteTemplate(id)
    ElMessage.success('模板已删除')
    await fetchTemplates()
  } catch (e) {
    console.error('删除模板失败:', e)
  }
}

// 初始化
onMounted(() => {
  fetchTemplates()
  fetchExercises()
})
</script>

<style scoped>
.template-manage-view {
  padding: 0;
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

.template-card {
  border-radius: 12px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  transition: background-color 0.2s;
}

.template-item:hover {
  background-color: var(--el-fill-color-light);
}

.template-info {
  flex: 1;
  cursor: pointer;
}

.template-name {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
}

.template-desc {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.template-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.item-count {
  color: var(--el-text-color-secondary);
}

.template-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  margin-left: 16px;
}

/* 详情 */
.detail-desc {
  margin-bottom: 20px;
}

.items-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 8px;
}

/* 新建弹窗 */
.item-row {
  margin-bottom: 8px;
  padding: 8px;
  background: var(--el-fill-color-lighter);
  border-radius: 6px;
}

.add-item-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
