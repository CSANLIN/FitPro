<template>
  <div class="announcement-manage-view">
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>发布公告
      </el-button>
    </div>

    <el-card v-loading="loading" shadow="hover" class="table-card">
      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column label="置顶" width="60" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.isTop === 1" color="#E6A23C"><StarFilled /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="{ NOTICE: 'success', ACTIVITY: 'warning', MAINTENANCE: 'danger' }[row.type] || 'info'" size="small">
              {{ { NOTICE: '公告', ACTIVITY: '活动', MAINTENANCE: '维护' }[row.type] || row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" plain @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button size="small" @click="handleToggleTop(row)">
              {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
            </el-button>
            <el-popconfirm title="确定删除该公告？" @confirm="handleDelete(row)">
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
    <el-dialog v-model="dialogVisible" :title="isEditing ? '编辑公告' : '发布公告'" width="650px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="系统公告" value="NOTICE" />
            <el-option label="活动通知" value="ACTIVITY" />
            <el-option label="维护通知" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="公告内容（支持 Markdown 格式）" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
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
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Plus, StarFilled } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const form = reactive({ title: '', type: 'NOTICE', content: '', isTop: 0 })
const formRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const formatDate = (d) => d ? new Date(d).toLocaleString('zh-CN') : '-'

const fetchData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    const res = await adminApi.listAnnouncements(params)
    tableData.value = res.list || []
    pagination.total = res.total || 0
  } catch (e) {
    console.error('获取公告列表失败:', e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEditing.value = false; editingId.value = null
  form.title = ''; form.type = 'NOTICE'; form.content = ''; form.isTop = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEditing.value = true; editingId.value = row.id
  form.title = row.title; form.type = row.type; form.content = row.content; form.isTop = row.isTop
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEditing.value) {
      await adminApi.updateAnnouncement(editingId.value, { title: form.title, type: form.type, content: form.content, isTop: form.isTop })
      ElMessage.success('公告更新成功')
    } else {
      await adminApi.createAnnouncement({ title: form.title, type: form.type, content: form.content, isTop: form.isTop })
      ElMessage.success('公告创建成功')
    }
    dialogVisible.value = false
    await fetchData()
  } catch (e) {
    console.error('保存公告失败:', e)
  } finally {
    saving.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    await adminApi.toggleAnnouncementStatus(row.id)
    ElMessage.success(row.status === 1 ? '公告已下架' : '公告已发布')
    await fetchData()
  } catch (e) { console.error('操作失败:', e) }
}

const handleToggleTop = async (row) => {
  try {
    await adminApi.toggleAnnouncementTop(row.id)
    ElMessage.success(row.isTop === 1 ? '已取消置顶' : '已置顶')
    await fetchData()
  } catch (e) { console.error('操作失败:', e) }
}

const handleDelete = async (row) => {
  try {
    await adminApi.deleteAnnouncement(row.id)
    ElMessage.success('公告已删除')
    await fetchData()
  } catch (e) { console.error('删除失败:', e) }
}

const handleSizeChange = (s) => { pagination.pageSize = s; pagination.pageNum = 1; fetchData() }
const handleCurrentChange = (p) => { pagination.pageNum = p; fetchData() }

onMounted(() => fetchData())
</script>

<style scoped>
.announcement-manage-view { max-width: 1100px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.table-card { border-radius: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
