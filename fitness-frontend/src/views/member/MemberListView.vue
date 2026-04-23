<template>
  <div class="member-list-view">
    <h1>会员管理</h1>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称/手机号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable>
            <el-option label="超级管理员" value="SUPER_ADMIN" />
            <el-option label="教练" value="COACH" />
            <el-option label="会员" value="MEMBER" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)">
              {{ roleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="0"
              :inactive-value="1"
              active-text="正常"
              inactive-text="禁用"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="currentUser" :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="roleTagType(currentUser.role)">
            {{ roleText(currentUser.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ genderText(currentUser.gender) }}
        </el-descriptions-item>
        <el-descriptions-item label="生日">{{ currentUser.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 0 ? 'success' : 'danger'">
            {{ currentUser.status === 0 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(currentUser.updatedAt) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

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
    case 'SUPER_ADMIN':
      return 'danger'
    case 'COACH':
      return 'warning'
    case 'MEMBER':
      return 'success'
    default:
      return 'info'
  }
}

// 角色文本
const roleText = (role) => {
  switch (role) {
    case 'SUPER_ADMIN':
      return '超级管理员'
    case 'COACH':
      return '教练'
    case 'MEMBER':
      return '会员'
    default:
      return role
  }
}

// 性别文本
const genderText = (gender) => {
  switch (gender) {
    case 1:
      return '男'
    case 2:
      return '女'
    default:
      return '未知'
  }
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
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
    ElMessage.error(error.response?.data?.message || '获取用户列表失败')
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
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await userApi.toggleStatus(row.id)
    ElMessage.success('状态更新成功')
    fetchUserList()
  } catch (error) {
    // 如果用户取消，恢复原来的状态
    row.status = row.status === 0 ? 1 : 0
    if (error !== 'cancel') {
      console.error('切换状态失败:', error)
      ElMessage.error(error.response?.data?.message || '状态更新失败')
    }
  }
}

// 查看详情
const handleViewDetail = async (row) => {
  try {
    const user = await userApi.getDetail(row.id)
    currentUser.value = user
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error(error.response?.data?.message || '获取用户详情失败')
  }
}

// 分页大小改变
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
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>