<template>
  <div class="member-detail-view" v-loading="loading">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h2>会员详情</h2>
    </div>

    <div v-if="member">
      <!-- 基本信息卡片 -->
      <el-card shadow="hover" class="info-card">
        <div class="member-header">
          <el-avatar :size="64" :src="member.avatar" icon="UserFilled" />
          <div class="member-meta">
            <div class="member-name">{{ member.nickname || member.username }}</div>
            <div class="member-role">
              <el-tag size="small" type="success">会员</el-tag>
              <el-tag v-if="member.status === 0" size="small" type="success" effect="plain">正常</el-tag>
              <el-tag v-else size="small" type="danger" effect="plain">禁用</el-tag>
            </div>
          </div>
          <div class="member-contact">
            <div>用户名: {{ member.username }}</div>
            <div>手机: {{ member.phone || '-' }}</div>
            <div>邮箱: {{ member.email || '-' }}</div>
          </div>
        </div>
      </el-card>

      <!-- 数据概览 -->
      <el-row :gutter="16" class="stats-row">
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">会籍</div>
            <div class="stat-value">{{ currentMembership?.cardName || '无' }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">本月签到</div>
            <div class="stat-value">{{ checkInStats?.monthCount || 0 }} 次</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-label">连续签到</div>
            <div class="stat-value">{{ checkInStats?.streakDays || 0 }} 天</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- Tabs -->
      <el-card shadow="hover" class="tabs-card">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="会籍信息" name="membership">
            <div class="section-toolbar">
              <el-button size="small" type="primary" @click="showAssignDialog">办理会籍</el-button>
            </div>
            <el-table v-if="memberships.length" :data="memberships" stripe size="small">
              <el-table-column prop="cardName" label="卡种" width="100" />
              <el-table-column prop="cardType" label="类型" width="60">
                <template #default="{ row }">{{
                  { MONTH: '月卡', QUARTER: '季卡', YEAR: '年卡', TIMES: '次卡' }[row.cardType] || row.cardType
                }}</template>
              </el-table-column>
              <el-table-column label="有效期" min-width="160">
                <template #default="{ row }">
                  {{ formatDate(row.startDate) }} ~ {{ row.endDate ? formatDate(row.endDate) : '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="remainingTimes" label="剩余次数" width="80" align="center" />
              <el-table-column label="状态" width="80" align="center">
                <template #default="{ row }">
                  <el-tag :type="{ ACTIVE: 'success', FROZEN: 'warning', EXPIRED: 'info', CANCELLED: 'danger' }[row.status] || 'info'" size="small">
                    {{ { ACTIVE: '活跃', FROZEN: '冻结', EXPIRED: '已过期', CANCELLED: '已取消' }[row.status] || row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" v-if="row.status === 'ACTIVE'" @click="handleFreeze(row)">冻结</el-button>
                  <el-button size="small" v-if="row.status === 'FROZEN'" @click="handleUnfreeze(row)">解冻</el-button>
                  <el-button size="small" v-if="row.status === 'ACTIVE'" @click="showRenewDialog(row)">续费</el-button>
                  <el-popconfirm title="确定退卡？" @confirm="handleCancel(row)">
                    <template #reference>
                      <el-button size="small" type="danger" plain v-if="row.status === 'ACTIVE' || row.status === 'FROZEN'">退卡</el-button>
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无会籍记录" :image-size="60" />
          </el-tab-pane>

          <el-tab-pane label="签到记录" name="checkin">
            <el-table v-if="checkIns.length" :data="checkIns" stripe size="small">
              <el-table-column prop="checkInDate" label="签到日期" width="120" />
              <el-table-column prop="checkInTime" label="签到时间" width="170" />
              <el-table-column prop="checkInType" label="签到方式" width="100">
                <template #default="{ row }">{{ row.checkInType === 'MANUAL' ? '手动签到' : '二维码' }}</template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无签到记录" :image-size="60" />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 办理会籍弹窗 -->
    <el-dialog v-model="assignVisible" title="办理会籍" width="400px" destroy-on-close>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignFormRules" label-width="90px">
        <el-form-item label="选择卡种" prop="cardId">
          <el-select v-model="assignForm.cardId" placeholder="选择卡种" filterable style="width: 100%">
            <el-option v-for="c in availableCards" :key="c.id" :label="`${c.cardName} ¥${c.price}`" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignSaving" @click="handleAssign">确认办理</el-button>
      </template>
    </el-dialog>

    <!-- 续费弹窗 -->
    <el-dialog v-model="renewVisible" title="续费会籍" width="400px" destroy-on-close>
      <el-form ref="renewFormRef" :model="renewForm" :rules="renewFormRules" label-width="90px">
        <el-form-item label="选择卡种" prop="cardId">
          <el-select v-model="renewForm.cardId" placeholder="选择新卡种" filterable style="width: 100%">
            <el-option v-for="c in availableCards" :key="c.id" :label="`${c.cardName} ¥${c.price}`" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" :loading="renewSaving" @click="handleRenew">确认续费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import { membershipApi } from '@/api/membership'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const memberId = Number(route.params.id)

const loading = ref(false)
const member = ref(null)
const memberships = ref([])
const checkIns = ref([])
const checkInStats = ref({})
const activeTab = ref('membership')
const cards = ref([])

// Dialog states
const assignVisible = ref(false)
const assignSaving = ref(false)
const assignFormRef = ref(null)
const renewVisible = ref(false)
const renewSaving = ref(false)
const renewFormRef = ref(null)
const renewingMembershipId = ref(null)
const assignForm = ref({ userId: memberId, cardId: null })
const renewForm = ref({ cardId: null })
const assignFormRules = { cardId: [{ required: true, message: '请选择卡种', trigger: 'change' }] }
const renewFormRules = { cardId: [{ required: true, message: '请选择卡种', trigger: 'change' }] }

const availableCards = computed(() => cards.value.filter(c => c.status === 1))
const currentMembership = computed(() => memberships.value.find(m => m.status === 'ACTIVE'))

const formatDate = (d) => d ? d.substring(0, 10) : '--'

const fetchData = async () => {
  loading.value = true
  try {
    const [user, membershipList, checkInData, cardList] = await Promise.all([
      userApi.getDetail(memberId),
      membershipApi.listByUser(memberId).catch(() => []),
      adminApi.listCheckIns(memberId).catch(() => []),
      membershipApi.listCards().catch(() => [])
    ])
    member.value = user
    memberships.value = membershipList
    checkIns.value = checkInData
    checkInStats.value = { monthCount: checkInData.length || 0, streakDays: 0 }
    cards.value = cardList
  } catch (e) {
    ElMessage.error('获取会员详情失败')
  } finally {
    loading.value = false
  }
}

const handleAssign = async () => {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return
  assignSaving.value = true
  try {
    await membershipApi.assign({ userId: memberId, cardId: assignForm.value.cardId })
    ElMessage.success('会籍办理成功')
    assignVisible.value = false
    await fetchData()
  } catch (e) {
    console.error('办理会籍失败:', e)
  } finally {
    assignSaving.value = false
  }
}

const showRenewDialog = (row) => {
  renewingMembershipId.value = row.id
  renewForm.value.cardId = null
  renewVisible.value = true
}

const handleRenew = async () => {
  const valid = await renewFormRef.value.validate().catch(() => false)
  if (!valid) return
  renewSaving.value = true
  try {
    await membershipApi.renew({ membershipId: renewingMembershipId.value, cardId: renewForm.value.cardId })
    ElMessage.success('续费成功')
    renewVisible.value = false
    await fetchData()
  } catch (e) {
    console.error('续费失败:', e)
  } finally {
    renewSaving.value = false
  }
}

const handleFreeze = async (row) => {
  try {
    await membershipApi.freeze(row.id)
    ElMessage.success('会籍已冻结')
    await fetchData()
  } catch (e) { console.error('冻结失败:', e) }
}

const handleUnfreeze = async (row) => {
  try {
    await membershipApi.unfreeze(row.id)
    ElMessage.success('会籍已解冻')
    await fetchData()
  } catch (e) { console.error('解冻失败:', e) }
}

const handleCancel = async (row) => {
  try {
    await membershipApi.cancel(row.id)
    ElMessage.success('会籍已取消')
    await fetchData()
  } catch (e) { console.error('退卡失败:', e) }
}

const showAssignDialog = () => {
  assignForm.value = { userId: memberId, cardId: null }
  assignVisible.value = true
}

onMounted(() => fetchData())
</script>

<style scoped>
.member-detail-view { max-width: 1000px; }
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; }
.info-card { border-radius: 12px; margin-bottom: 16px; }
.member-header { display: flex; align-items: center; gap: 20px; }
.member-meta { flex: 1; }
.member-name { font-size: 18px; font-weight: 600; }
.member-role { display: flex; gap: 8px; margin-top: 4px; }
.member-contact { font-size: 13px; color: var(--el-text-color-secondary); line-height: 1.8; }
.stats-row { margin-bottom: 16px; }
.stat-card { border-radius: 12px; text-align: center; }
.stat-label { font-size: 13px; color: var(--el-text-color-secondary); }
.stat-value { font-size: 24px; font-weight: 700; margin-top: 4px; }
.tabs-card { border-radius: 12px; }
.section-toolbar { margin-bottom: 12px; }
</style>
