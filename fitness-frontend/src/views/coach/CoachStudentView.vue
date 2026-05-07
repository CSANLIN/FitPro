<template>
  <div class="coach-students">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的学员</span>
          <span class="student-count">共 {{ students.length }} 人</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="students" stripe style="width: 100%">
        <el-table-column label="学员" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" :src="row.userAvatar" />
              <span class="user-name">{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalBookings" label="预约次数" width="100" sortable />
        <el-table-column label="出勤" width="120" sortable>
          <template #default="{ row }">
            {{ row.attendedCount }}/{{ row.totalBookings }}
          </template>
        </el-table-column>
        <el-table-column label="出勤率" width="120" sortable>
          <template #default="{ row }">
            <el-tag :type="row.attendanceRate >= 80 ? 'success' : row.attendanceRate >= 50 ? 'warning' : 'danger'" size="small">
              {{ row.attendanceRate }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastBookingDate" label="最近上课" width="120" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewStudentDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && students.length === 0" description="还没有学员预约过你的课程" />
    </el-card>

    <!-- 学员详情弹窗 -->
    <el-dialog v-model="showDetailDialog" :title="'学员详情 - ' + (currentStudent?.userName || '')" width="600px">
      <div v-if="currentStudent" class="student-detail">
        <div class="detail-summary">
          <el-avatar :size="64" :src="currentStudent.userAvatar" />
          <div class="detail-info">
            <h3>{{ currentStudent.userName }}</h3>
            <p>预约 {{ currentStudent.totalBookings }} 次 · 出勤 {{ currentStudent.attendedCount }} 次 · 出勤率 {{ currentStudent.attendanceRate }}%</p>
            <p>最近上课：{{ currentStudent.lastBookingDate || '暂无' }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { coachApi } from '@/api/coach'

const loading = ref(false)
const students = ref([])
const showDetailDialog = ref(false)
const currentStudent = ref(null)

const fetchStudents = async () => {
  loading.value = true
  try {
    students.value = await coachApi.listStudents()
  } catch (e) {
    console.error('获取学员列表失败:', e)
  } finally {
    loading.value = false
  }
}

const viewStudentDetail = (student) => {
  currentStudent.value = student
  showDetailDialog.value = true
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-weight: 500;
}

.student-detail {
  .detail-summary {
    display: flex;
    gap: 24px;
    align-items: flex-start;
    padding: 16px;
    background: var(--bg-lighter);
    border-radius: 12px;
  }

  .detail-info {
    h3 {
      margin: 0 0 8px;
      font-size: 18px;
    }

    p {
      margin: 4px 0;
      color: var(--text-secondary);
      font-size: 14px;
    }
  }
}
</style>
