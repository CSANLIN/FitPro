<template>
  <div class="member-list-view">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户与会员管理</h1>
        <p class="page-subtitle">管理全站用户信息，掌控系统访问权限</p>
      </div>
      <div class="header-actions">
        <!-- 预留操作按钮，例如导出等 -->
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card class="premium-panel search-card">
      <el-form :model="searchForm" inline class="premium-form">
        <el-form-item label="关键词" class="form-item">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名/昵称/手机号"
            clearable
            @keyup.enter="handleSearch"
            prefix-icon="Search"
            class="premium-input"
          />
        </el-form-item>

        <el-form-item label="角色筛查" class="form-item">
          <el-select v-model="searchForm.role" placeholder="全部角色" clearable class="premium-select">
            <el-option label="超级管理员" value="SUPER_ADMIN" />
            <el-option label="教练" value="COACH" />
            <el-option label="会员" value="MEMBER" />
          </el-select>
        </el-form-item>

        <el-form-item label="账号状态" class="form-item">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="premium-select">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" color="#3b82f6" @click="handleSearch" :loading="loading" round>
            检索用户
          </el-button>
          <el-button @click="resetSearch" round plain>重置条件</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="premium-panel table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        class="premium-table"
        :header-cell-style="{ background: '#f8fafc', color: '#64748b', fontWeight: '600' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="36" class="cell-avatar" :src="row.avatar">{{ row.nickname?.charAt(0) || row.username.charAt(0) }}</el-avatar>
              <div class="cell-text">
                <div class="cell-name">{{ row.nickname || row.username }}</div>
                <div class="cell-sub">@{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系方式" width="160">
          <template #default="{ row }">
            <div class="contact-cell">
              <span v-if="row.phone">{{ row.phone }}</span>
              <span v-else class="text-placeholder">未绑定手机</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="系统角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" effect="light" round class="role-tag">
              {{ roleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="0"
              :inactive-value="1"
              style="--el-switch-on-color: #10b981; --el-switch-off-color: #ef4444"
              inline-prompt
              active-text="正常"
              inactive-text="禁用"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            <span class="date-text">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)" class="action-btn">
              档案详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户综合档案"
      width="650px"
      :close-on-click-modal="false"
      class="premium-dialog"
    >
      <div class="profile-header" v-if="currentUser">
        <el-avatar :size="64" class="profile-avatar">{{ currentUser.nickname?.charAt(0) || currentUser.username.charAt(0) }}</el-avatar>
        <div class="profile-title-area">
          <h2 class="profile-name">{{ currentUser.nickname || currentUser.username }}</h2>
          <el-tag :type="roleTagType(currentUser.role)" effect="dark" round size="small">{{ roleText(currentUser.role) }}</el-tag>
        </div>
      </div>
      
      <el-descriptions v-if="currentUser" :column="2" border class="premium-descriptions mt-md">
        <el-descriptions-item label="系统 ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="登录账号">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="电子邮箱">{{ currentUser.email || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ genderText(currentUser.gender) }}
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ currentUser.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="账号状态" :span="2">
          <el-tag :type="currentUser.status === 0 ? 'success' : 'danger'" effect="plain" round size="small">
            {{ currentUser.status === 0 ? '正常活跃' : '已被禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="最后更新">{{ formatDate(currentUser.updatedAt) }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false" round>关闭档案</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'


// 搜索表单
const searchForm = reactive({
  keyword: '',
  role: '',
  status: null
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 详情弹窗
const detailDialogVisible = ref(false)
const currentUser = ref(null)

// 角色标签类型
const roleTagType = (role) => {
  switch (role) {
    case 'SUPER_ADMIN': return 'danger'
    case 'COACH': return 'warning'
    case 'MEMBER': return 'success'
    default: return 'info'
  }
}

// 角色文本
const roleText = (role) => {
  switch (role) {
    case 'SUPER_ADMIN': return '系统管理员'
    case 'COACH': return '专业教练'
    case 'MEMBER': return '注册会员'
    default: return role
  }
}

// 性别文本
const genderText = (gender) => {
  switch (gender) {
    case 1: return '男'
    case 2: return '女'
    default: return '未知'
  }
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    const res = await userApi.list(params)
    tableData.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.role = ''
  searchForm.status = null
  pagination.pageNum = 1
  fetchUserList()
}

// 切换状态
const handleStatusChange = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 0 ? '启用' : '禁用'}用户 "${row.nickname || row.username}" 吗？`,
      '风险提示',
      {
        confirmButtonText: '确定执行',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'premium-message-box'
      }
    )

    await userApi.toggleStatus(row.id)
    ElMessage.success('状态更新成功')
    fetchUserList()
  } catch (error) {
    // 恢复原来的状态
    row.status = row.status === 0 ? 1 : 0
    if (error !== 'cancel') {
      console.error('切换状态失败:', error)
    }
  }
}

// 查看详情弹窗
const handleViewDetail = async (row) => {
  try {
    const user = await userApi.getDetail(row.id)
    currentUser.value = user
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
  }
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchUserList()
}

// 页码改变
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchUserList()
}

// 初始化
onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.member-list-view {
  max-width: 1400px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 800;
  margin: 0 0 4px;
  color: #0f172a;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.premium-panel {
  border: none !important;
  margin-bottom: 20px;
}

.search-card {
  padding: 8px 4px 0;
}

.premium-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.form-item {
  margin-bottom: 16px !important;
  margin-right: 0 !important;
}

.form-actions {
  margin-left: auto;
  margin-bottom: 16px !important;
}

/* 表格定制化 */
.table-card {
  padding: 0;
}

.premium-table {
  border-radius: 12px;
  overflow: hidden;
}

.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cell-avatar {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  font-weight: 600;
}

.cell-text {
  display: flex;
  flex-direction: column;
}

.cell-name {
  font-weight: 600;
  color: #0f172a;
  font-size: 14px;
}

.cell-sub {
  font-size: 12px;
  color: #64748b;
}

.contact-cell {
  color: #334155;
  font-family: monospace;
  font-size: 13px;
}

.role-tag {
  border-radius: 6px;
  font-weight: 600;
  letter-spacing: 1px;
}

.date-text {
  font-size: 13px;
  color: #64748b;
}

.action-btn {
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 24px;
  padding-bottom: 8px;
  display: flex;
  justify-content: flex-end;
}

/* 档案详情弹窗美化 */
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0 24px;
  border-bottom: 1px dashed #e2e8f0;
}

.profile-avatar {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  font-size: 24px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.profile-title-area {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.profile-name {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}

.premium-descriptions {
  --el-descriptions-table-border: 1px solid #f1f5f9;
  --el-descriptions-item-bordered-label-background: #f8fafc;
}

:deep(.premium-descriptions .el-descriptions__label) {
  font-weight: 600;
  color: #64748b;
  width: 100px;
}

:deep(.premium-descriptions .el-descriptions__content) {
  color: #0f172a;
  font-weight: 500;
}
</style>