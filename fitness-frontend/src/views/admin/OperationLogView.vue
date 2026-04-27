<template>
  <div class="operation-log-view">
    <div class="page-header">
      <h2>操作日志</h2>
    </div>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" size="small">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/URL" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="模块">
          <el-select v-model="searchForm.module" placeholder="全部" clearable @change="handleSearch" style="width: 120px">
            <el-option label="认证" value="auth" />
            <el-option label="课程" value="courses" />
            <el-option label="排课" value="course-schedules" />
            <el-option label="预约" value="course-bookings" />
            <el-option label="会籍" value="memberships" />
            <el-option label="卡种" value="membership-cards" />
            <el-option label="签到" value="check-ins" />
            <el-option label="运动库" value="exercises" />
            <el-option label="用户" value="users" />
            <el-option label="公告" value="announcements" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作">
          <el-select v-model="searchForm.operation" placeholder="全部" clearable @change="handleSearch" style="width: 110px">
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading" shadow="hover" class="table-card">
      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="module" label="模块" width="90">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作" width="60" align="center" />
        <el-table-column prop="method" label="方法" width="60" align="center" />
        <el-table-column prop="url" label="请求路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="duration" label="耗时(ms)" width="80" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.duration > 1000 ? '#F56C6C' : '#67C23A' }">{{ row.duration }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="操作时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="60" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]" :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="700px" :close-on-click-modal="false">
      <el-descriptions v-if="currentLog" :column="2" border>
        <el-descriptions-item label="ID" :span="2">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ currentLog.url }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <div class="log-params">{{ currentLog.params || '无' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentLog.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ formatDate(currentLog.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentLog = ref(null)
const searchForm = reactive({ keyword: '', module: '', operation: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const formatDate = (d) => d ? new Date(d).toLocaleString('zh-CN') : '-'

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    Object.keys(params).forEach(k => { if (params[k] === '' || params[k] === null || params[k] === undefined) delete params[k] })
    const res = await adminApi.listOperationLogs(params)
    tableData.value = res.list || []
    pagination.total = res.total || 0
  } catch (e) {
    console.error('获取操作日志失败:', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; fetchData() }
const resetSearch = () => { searchForm.keyword = ''; searchForm.module = ''; searchForm.operation = ''; pagination.pageNum = 1; fetchData() }

const showDetail = (row) => {
  currentLog.value = row
  detailVisible.value = true
}

const handleSizeChange = (s) => { pagination.pageSize = s; pagination.pageNum = 1; fetchData() }
const handleCurrentChange = (p) => { pagination.pageNum = p; fetchData() }

onMounted(() => fetchData())
</script>

<style scoped>
.operation-log-view { max-width: 1200px; }
.page-header { margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.search-card { border-radius: 12px; margin-bottom: 16px; }
.table-card { border-radius: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.log-params {
  max-height: 120px;
  overflow-y: auto;
  background: var(--el-fill-color-lighter);
  padding: 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
