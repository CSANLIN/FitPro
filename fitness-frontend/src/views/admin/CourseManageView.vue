<template>
  <div class="course-manage-view">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">课程图鉴中心</h1>
        <p class="page-subtitle">设计并管理全站课程体系与资产</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" color="#3b82f6" @click="showCreateDialog" round>
          <el-icon><Plus /></el-icon> 研发新课程
        </el-button>
      </div>
    </div>

    <el-card v-loading="loading" class="premium-panel table-card">
      <el-table :data="tableData" style="width: 100%" class="premium-table" :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
        <el-table-column prop="name" label="课程体系名称" min-width="160">
          <template #default="{ row }">
            <div class="course-name-cell">
              <div class="course-icon theme-blue">
                <el-icon><Notebook /></el-icon>
              </div>
              <span class="font-bold">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="类型归属" width="120">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" round class="type-tag">{{ row.courseType || '通用核心' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="durationMinutes" label="标准时长(分钟)" width="140" align="center">
          <template #default="{ row }">
            <span class="meta-number">{{ row.durationMinutes }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxCapacity" label="容量上限" width="100" align="center">
          <template #default="{ row }">
            <span class="meta-number">{{ row.maxCapacity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发布状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              :active-value="1" 
              :inactive-value="0"
              style="--el-switch-on-color: #3b82f6; --el-switch-off-color: #cbd5e1"
              active-text="已上架" 
              inactive-text="已下架" 
              inline-prompt
              @change="handleToggleStatus(row)" 
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="研发时间" width="160">
          <template #default="{ row }">
            <span class="date-text">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="管理维护" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-group">
              <el-button link type="primary" class="action-btn" @click="handleEdit(row)">设计编辑</el-button>
              <el-popconfirm title="确定要永久销毁该课程体系吗？" @confirm="handleDelete(row)" width="220">
                <template #reference>
                  <el-button link type="danger" class="action-btn">销毁</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination 
          v-model:current-page="pagination.pageNum" 
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]" 
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="handleSizeChange" 
          @current-change="handleCurrentChange" 
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEditing ? '修订课程档案' : '研发新课程体系'" width="550px" destroy-on-close class="premium-dialog">
      <div class="dialog-desc">{{ isEditing ? '优化现有的课程配置及表现' : '填写课程基本资料进行备案与上架配置' }}</div>
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top">
        <el-form-item label="课程官方名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：燃脂动感单车基础级" class="premium-input" />
        </el-form-item>
        
        <el-form-item label="课程类型归属" prop="courseType">
          <el-select v-model="form.courseType" placeholder="选择核心运动类型" class="full-width premium-select">
            <el-option label="瑜伽身心" value="瑜伽" />
            <el-option label="力量增肌" value="力量训练" />
            <el-option label="有氧减脂" value="有氧运动" />
            <el-option label="舞蹈节奏" value="舞蹈" />
            <el-option label="格斗防身" value="格斗" />
            <el-option label="综合体能" value="综合" />
            <el-option label="其他类别" value="其他" />
          </el-select>
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="标准排期时长 (分钟)" prop="durationMinutes">
              <el-input-number v-model="form.durationMinutes" :min="15" :step="15" class="full-width" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单场最大容量 (人)" prop="maxCapacity">
              <el-input-number v-model="form.maxCapacity" :min="1" :max="200" class="full-width" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="课程推广描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="向会员展示的课程特色简介..." class="premium-textarea" />
        </el-form-item>
        
        <el-form-item label="宣传海报 (URL)" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="https://..." class="premium-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" round>暂不提交</el-button>
        <el-button type="primary" color="#3b82f6" :loading="saving" @click="handleSave" round>
          {{ isEditing ? '保存修订' : '发布上线' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { courseApi } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Notebook } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const form = reactive({ name: '', description: '', courseType: '', durationMinutes: 60, maxCapacity: 30, coverImage: '' })

const formRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  durationMinutes: [{ required: true, message: '请输入课程时长', trigger: 'blur' }],
  maxCapacity: [{ required: true, message: '请输入最大容量', trigger: 'blur' }]
}

const formatDate = (d) => d ? d.substring(0, 10).replace(/-/g, '/') : '-'

const fetchData = async () => {
  loading.value = true
  try {
    const data = await courseApi.list({ all: true })
    tableData.value = data || []
    pagination.total = (data && data.length) || 0
  } catch (e) {
    console.error('获取课程列表失败:', e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEditing.value = false
  editingId.value = null
  form.name = ''; form.description = ''; form.courseType = ''
  form.durationMinutes = 60; form.maxCapacity = 30; form.coverImage = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEditing.value = true
  editingId.value = row.id
  form.name = row.name
  form.description = row.description || ''
  form.courseType = row.courseType || ''
  form.durationMinutes = row.durationMinutes || 60
  form.maxCapacity = row.maxCapacity || 30
  form.coverImage = row.coverImage || ''
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEditing.value) {
      await courseApi.update({ ...form, id: editingId.value })
      ElMessage.success('课程档案更新成功')
    } else {
      await courseApi.create(form)
      ElMessage.success('全新课程体系已发布')
    }
    dialogVisible.value = false
    await fetchData()
  } catch (e) {
    console.error('保存课程失败:', e)
  } finally {
    saving.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要${row.status === 1 ? '上架' : '下架'}该课程体系吗？`, '状态变更操作', { type: 'warning', confirmButtonText: '确认执行' })
    await courseApi.toggleStatus(row.id)
    ElMessage.success('发布状态已更新')
    await fetchData()
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleDelete = async (row) => {
  try {
    await courseApi.delete(row.id)
    ElMessage.success('课程档案已彻底销毁')
    await fetchData()
  } catch (e) {
    console.error('删除失败:', e)
  }
}

const handleSizeChange = (s) => { pagination.pageSize = s; pagination.pageNum = 1; fetchData() }
const handleCurrentChange = (p) => { pagination.pageNum = p; fetchData() }

onMounted(() => fetchData())
</script>

<style scoped>
.course-manage-view { max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 800; margin: 0 0 4px; color: #0f172a; }
.page-subtitle { font-size: 14px; color: #64748b; margin: 0; }

.premium-panel { border: none !important; margin-bottom: 20px; }
.table-card { padding: 0; }
.premium-table { border-radius: 12px; overflow: hidden; }

.course-name-cell { display: flex; align-items: center; gap: 12px; }
.course-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 18px; }
.theme-blue { background: #eff6ff; color: #3b82f6; }
.font-bold { font-weight: 600; color: #0f172a; }

.type-tag { font-weight: 600; letter-spacing: 1px; }
.meta-number { font-family: monospace; font-size: 14px; font-weight: 600; color: #475569; }
.date-text { font-family: monospace; font-size: 13px; color: #64748b; }

.action-group { display: flex; gap: 8px; justify-content: center; }
.action-btn { font-weight: 600; }

.pagination-wrapper { margin-top: 24px; padding-bottom: 8px; display: flex; justify-content: flex-end; }
.dialog-desc { color: #64748b; font-size: 14px; margin-top: -10px; margin-bottom: 24px; }
.full-width { width: 100%; }
</style>
