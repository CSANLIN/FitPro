<template>
  <div class="coach-list-view">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">教练资源管理</h1>
        <p class="page-subtitle">管理健身房教练档案，配置教学权限</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" color="#3b82f6" @click="showCreateDialog" round>
          <el-icon><Plus /></el-icon> 入驻新教练
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card class="premium-panel search-card">
      <el-form :inline="true" class="premium-form">
        <el-form-item label="检索教练" class="form-item">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索姓名或手机" 
            clearable 
            @keyup.enter="handleSearch"
            prefix-icon="Search"
            class="premium-input"
          />
        </el-form-item>
        <el-form-item label="在职状态" class="form-item">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="premium-select">
            <el-option label="正常在职" :value="0" />
            <el-option label="离职/休假" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button type="primary" color="#3b82f6" @click="handleSearch" round>检索</el-button>
          <el-button @click="resetSearch" round plain>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="premium-panel table-card">
      <el-table :data="tableData" style="width: 100%" class="premium-table" :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
        <el-table-column prop="id" label="工号" width="80" align="center" />
        <el-table-column label="教练信息" min-width="180">
          <template #default="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="36" class="cell-avatar">{{ row.nickname?.charAt(0) || row.username.charAt(0) }}</el-avatar>
              <div class="cell-text">
                <div class="cell-name">{{ row.nickname || row.username }}</div>
                <div class="cell-sub">@{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" width="140">
          <template #default="{ row }">
            <span class="contact-text">{{ row.phone || '未绑定' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="执教状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch 
              v-model="row.status" 
              :active-value="0" 
              :inactive-value="1" 
              style="--el-switch-on-color: #10b981; --el-switch-off-color: #ef4444"
              inline-prompt
              active-text="在职"
              inactive-text="离职"
              @change="handleToggleStatus(row)" 
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="入驻时间" width="160">
          <template #default="{ row }">
            <span class="date-text">{{ formatDate(row.createdAt) }}</span>
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

    <!-- 新增教练弹窗 -->
    <el-dialog v-model="dialogVisible" title="教练入驻配置" width="480px" destroy-on-close class="premium-dialog">
      <div class="dialog-desc">为新入驻教练创建专属系统管理账号</div>
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top">
        <el-form-item label="登录账号 (Username)" prop="username" v-if="!isEditing">
          <el-input v-model="form.username" placeholder="建议使用拼音首字母缩写" class="premium-input" />
        </el-form-item>
        <el-form-item label="初始密码" prop="password" v-if="!isEditing">
          <el-input v-model="form.password" type="password" placeholder="教练初始登录密码" show-password class="premium-input" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEditing">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password class="premium-input" />
        </el-form-item>
        <el-form-item label="教练花名 (Nickname)" prop="nickname">
          <el-input v-model="form.nickname" placeholder="对外展示的称呼" class="premium-input" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="手机号" class="premium-input" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" placeholder="工作邮箱" class="premium-input" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" round>取消创建</el-button>
        <el-button type="primary" color="#3b82f6" :loading="saving" @click="handleSave" round>确认创建账号</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import { authApi } from '@/api/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEditing = ref(false)

const searchForm = reactive({ keyword: '', status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '', phone: '', email: '' })

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入教练花名', trigger: 'blur' }]
}

const formatDate = (d) => d ? d.substring(0, 10).replace(/-/g, '/') : '-'

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize, role: 'COACH' }
    Object.keys(params).forEach(k => { if (params[k] === '' || params[k] === null) delete params[k] })
    const res = await userApi.list(params)
    tableData.value = res.list || []
    pagination.total = res.total || 0
  } catch (e) {
    console.error('获取教练列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; fetchData() }
const resetSearch = () => { searchForm.keyword = ''; searchForm.status = null; pagination.pageNum = 1; fetchData() }

const showCreateDialog = () => {
  isEditing.value = false
  form.username = ''; form.password = ''; form.confirmPassword = ''; form.nickname = ''; form.phone = ''; form.email = ''
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await authApi.register({ username: form.username, password: form.password, confirmPassword: form.confirmPassword, nickname: form.nickname, phone: form.phone, email: form.email, role: 'COACH' })
    ElMessage.success('教练账号下发成功')
    dialogVisible.value = false
    await fetchData()
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要在系统中${row.status === 0 ? '重新启用' : '彻底禁用'}该教练吗？`, '人事变动确认', { type: 'warning', confirmButtonText: '确认变更' })
    await userApi.toggleStatus(row.id)
    ElMessage.success('在职状态已更新')
  } catch (e) {
    row.status = row.status === 0 ? 1 : 0
  }
}

const handleSizeChange = (s) => { pagination.pageSize = s; pagination.pageNum = 1; fetchData() }
const handleCurrentChange = (p) => { pagination.pageNum = p; fetchData() }

onMounted(() => fetchData())
</script>

<style scoped>
.coach-list-view { max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 800; margin: 0 0 4px; color: #0f172a; }
.page-subtitle { font-size: 14px; color: #64748b; margin: 0; }

.premium-panel { border: none !important; margin-bottom: 20px; }
.search-card { padding: 8px 4px 0; }
.premium-form { display: flex; flex-wrap: wrap; gap: 16px; }
.form-item { margin-bottom: 16px !important; margin-right: 0 !important; }
.form-actions { margin-left: auto; margin-bottom: 16px !important; }

.table-card { padding: 0; }
.premium-table { border-radius: 12px; overflow: hidden; }

.user-info-cell { display: flex; align-items: center; gap: 12px; }
.cell-avatar { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; font-weight: 600; }
.cell-text { display: flex; flex-direction: column; }
.cell-name { font-weight: 600; color: #0f172a; font-size: 14px; }
.cell-sub { font-size: 12px; color: #64748b; }

.contact-text, .date-text { font-family: monospace; font-size: 13px; color: #475569; }

.action-btn { font-weight: 600; }
.pagination-wrapper { margin-top: 24px; padding-bottom: 8px; display: flex; justify-content: flex-end; }

.dialog-desc { color: #64748b; font-size: 14px; margin-top: -10px; margin-bottom: 24px; }
</style>
