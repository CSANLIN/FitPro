<template>
  <div class="course-manage-view">
    <div class="page-header">
      <h2>课程管理</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>新增课程
      </el-button>
    </div>

    <el-card v-loading="loading" shadow="hover" class="table-card">
      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="name" label="课程名称" min-width="140" />
        <el-table-column prop="courseType" label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.courseType || '通用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="durationMinutes" label="时长(分钟)" width="100" align="center" />
        <el-table-column prop="maxCapacity" label="最大容量" width="90" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
              active-text="上架" inactive-text="下架" @change="handleToggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该课程？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button size="small" type="danger" plain>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]" :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑课程' : '新增课程'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="如: 动感单车" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="课程简介" />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-select v-model="form.courseType" placeholder="选择类型" style="width: 100%">
            <el-option label="瑜伽" value="瑜伽" />
            <el-option label="力量训练" value="力量训练" />
            <el-option label="有氧运动" value="有氧运动" />
            <el-option label="舞蹈" value="舞蹈" />
            <el-option label="格斗" value="格斗" />
            <el-option label="综合" value="综合" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="时长(分钟)" prop="durationMinutes">
              <el-input-number v-model="form.durationMinutes" :min="15" :step="15" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大容量" prop="maxCapacity">
              <el-input-number v-model="form.maxCapacity" :min="1" :max="200" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="图片URL（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { courseApi } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

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

const formatDate = (d) => d ? new Date(d).toLocaleString('zh-CN') : '-'

const fetchData = async () => {
  loading.value = true
  try {
    const data = await courseApi.list({ all: true })
    // 后端返回 List<CourseVO>，前端不分页展示
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
      ElMessage.success('课程更新成功')
    } else {
      await courseApi.create(form)
      ElMessage.success('课程创建成功')
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
    await ElMessageBox.confirm(`确定${row.status === 1 ? '上架' : '下架'}该课程？`, '提示', { type: 'warning' })
    await courseApi.toggleStatus(row.id)
    ElMessage.success('状态已更新')
    await fetchData()
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1
    if (e !== 'cancel') console.error('操作失败:', e)
  }
}

const handleDelete = async (row) => {
  try {
    await courseApi.delete(row.id)
    ElMessage.success('课程已删除')
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
.course-manage-view { max-width: 1100px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.table-card { border-radius: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
