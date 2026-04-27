<template>
  <div class="member-detail-view" v-loading="loading">
    <div class="page-header">
      <el-button text @click="router.back()" class="back-btn" round>
        <el-icon><ArrowLeft /></el-icon> 返回上一页
      </el-button>
      <div class="header-text">
        <h2 class="page-title">会员深度档案</h2>
      </div>
    </div>

    <div v-if="member" class="profile-container">
      <!-- 英雄信息卡片 -->
      <div class="hero-card premium-shadow">
        <div class="hero-bg"></div>
        <div class="hero-content">
          <div class="member-header">
            <el-avatar :size="80" :src="member.avatar" class="hero-avatar">
              {{ member.nickname?.charAt(0) || member.username.charAt(0) }}
            </el-avatar>
            <div class="member-meta">
              <div class="member-name">{{ member.nickname || member.username }}</div>
              <div class="member-role">
                <el-tag size="small" type="success" effect="dark" round>注册会员</el-tag>
                <el-tag v-if="member.status === 0" size="small" type="primary" effect="plain" round>状态正常</el-tag>
                <el-tag v-else size="small" type="danger" effect="plain" round>账号禁用</el-tag>
              </div>
            </div>
            <div class="member-contact glass-panel">
              <div class="contact-item"><el-icon><User /></el-icon> {{ member.username }}</div>
              <div class="contact-item"><el-icon><Iphone /></el-icon> {{ member.phone || '未绑定手机' }}</div>
              <div class="contact-item"><el-icon><Message /></el-icon> {{ member.email || '未绑定邮箱' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据概览 (Stats) -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <div class="stat-card premium-panel">
            <div class="stat-icon theme-blue"><el-icon><Ticket /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ currentMembership?.cardName || '无活跃会籍' }}</div>
              <div class="stat-label">当前持有卡种</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card premium-panel">
            <div class="stat-icon theme-green"><el-icon><Calendar /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ checkInStats?.monthCount || 0 }}<small> 次</small></div>
              <div class="stat-label">本月签到次数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card premium-panel">
            <div class="stat-icon theme-orange"><el-icon><Trophy /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ checkInStats?.streakDays || 0 }}<small> 天</small></div>
              <div class="stat-label">当前连续签到</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 核心业务 Tabs -->
      <el-card class="premium-panel tabs-card">
        <el-tabs v-model="activeTab" class="premium-tabs">
          <el-tab-pane label="会籍资产管理" name="membership">
            <div class="section-toolbar">
              <el-button color="#3b82f6" @click="showAssignDialog" round>
                <el-icon><Plus /></el-icon> 为该用户办理新会籍
              </el-button>
            </div>
            
            <el-table v-if="memberships.length" :data="memberships" class="premium-table">
              <el-table-column prop="cardName" label="会籍卡种" min-width="120">
                <template #default="{ row }">
                  <span class="font-bold">{{ row.cardName }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="cardType" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag size="small" effect="plain" round>
                    {{ { MONTH: '月卡', QUARTER: '季卡', YEAR: '年卡', TIMES: '次卡' }[row.cardType] || row.cardType }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="有效期限" min-width="220">
                <template #default="{ row }">
                  <div class="date-range">
                    <span class="date-item">{{ formatDate(row.startDate) }}</span>
                    <span class="separator">至</span>
                    <span class="date-item">{{ row.endDate ? formatDate(row.endDate) : '无限期' }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="remainingTimes" label="剩余次数" width="100" align="center">
                <template #default="{ row }">
                  <span v-if="row.cardType === 'TIMES'" class="count-badge">{{ row.remainingTimes }}</span>
                  <span v-else class="text-placeholder">-</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="{ ACTIVE: 'success', FROZEN: 'warning', EXPIRED: 'info', CANCELLED: 'danger' }[row.status] || 'info'" effect="dark" round size="small">
                    {{ { ACTIVE: '活跃中', FROZEN: '已冻结', EXPIRED: '已过期', CANCELLED: '已取消' }[row.status] || row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="管理操作" width="220" fixed="right" align="center">
                <template #default="{ row }">
                  <div class="action-group">
                    <el-button link type="warning" v-if="row.status === 'ACTIVE'" @click="handleFreeze(row)">冻结</el-button>
                    <el-button link type="success" v-if="row.status === 'FROZEN'" @click="handleUnfreeze(row)">解冻</el-button>
                    <el-button link type="primary" v-if="row.status === 'ACTIVE'" @click="showRenewDialog(row)">续费</el-button>
                    <el-popconfirm title="确定要退卡吗？该操作不可逆" @confirm="handleCancel(row)">
                      <template #reference>
                        <el-button link type="danger" v-if="row.status === 'ACTIVE' || row.status === 'FROZEN'">退卡</el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="该会员暂无会籍记录" class="premium-empty" />
          </el-tab-pane>

          <el-tab-pane label="签到与到访记录" name="checkin">
            <el-table v-if="checkIns.length" :data="checkIns" class="premium-table">
              <el-table-column prop="checkInDate" label="签到日期" width="150" />
              <el-table-column prop="checkInTime" label="准确时间" width="200" />
              <el-table-column prop="checkInType" label="验证方式" width="150">
                <template #default="{ row }">
                  <el-tag :type="row.checkInType === 'MANUAL' ? 'info' : 'success'" round effect="light">
                    {{ row.checkInType === 'MANUAL' ? '前台手动签到' : '设备扫码核验' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="该会员暂无到访记录" class="premium-empty" />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 办理会籍弹窗 -->
    <el-dialog v-model="assignVisible" title="办理新会籍" width="450px" destroy-on-close class="premium-dialog">
      <div class="dialog-desc">为用户配置全新的入场资格</div>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignFormRules" label-position="top">
        <el-form-item label="选择目标卡种" prop="cardId">
          <el-select v-model="assignForm.cardId" placeholder="搜索或选择卡种" filterable class="full-width">
            <el-option v-for="c in availableCards" :key="c.id" :label="`${c.cardName} (¥${c.price})`" :value="c.id">
              <div class="card-option">
                <span>{{ c.cardName }}</span>
                <span class="price-tag">¥{{ c.price }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false" round>取消</el-button>
        <el-button type="primary" color="#10b981" :loading="assignSaving" @click="handleAssign" round>确认授权</el-button>
      </template>
    </el-dialog>

    <!-- 续费弹窗 -->
    <el-dialog v-model="renewVisible" title="会籍续费/升级" width="450px" destroy-on-close class="premium-dialog">
      <div class="dialog-desc">在原有会籍基础上叠加时长或次数</div>
      <el-form ref="renewFormRef" :model="renewForm" :rules="renewFormRules" label-position="top">
        <el-form-item label="选择续费卡种" prop="cardId">
          <el-select v-model="renewForm.cardId" placeholder="选择续费卡种" filterable class="full-width">
            <el-option v-for="c in availableCards" :key="c.id" :label="`${c.cardName} (¥${c.price})`" :value="c.id">
              <div class="card-option">
                <span>{{ c.cardName }}</span>
                <span class="price-tag">¥{{ c.price }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false" round>取消</el-button>
        <el-button type="primary" color="#3b82f6" :loading="renewSaving" @click="handleRenew" round>完成支付与续费</el-button>
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
import { ArrowLeft, Plus, User, Iphone, Message, Ticket, Calendar, Trophy } from '@element-plus/icons-vue'

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

const formatDate = (d) => d ? d.substring(0, 10).replace(/-/g, '/') : '--'

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
    ElMessage.error('获取会员档案失败')
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
    ElMessage.success('会籍开通成功')
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
    ElMessage.success('会籍续费成功')
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
    ElMessage.success('资产已冻结')
    await fetchData()
  } catch (e) { console.error('冻结失败:', e) }
}

const handleUnfreeze = async (row) => {
  try {
    await membershipApi.unfreeze(row.id)
    ElMessage.success('资产已解冻')
    await fetchData()
  } catch (e) { console.error('解冻失败:', e) }
}

const handleCancel = async (row) => {
  try {
    await membershipApi.cancel(row.id)
    ElMessage.success('该会籍已退卡作废')
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
.member-detail-view {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 24px;
}

.back-btn {
  margin-left: -12px;
  margin-bottom: 8px;
  color: #64748b;
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  margin: 0;
  color: #0f172a;
}

/* 英雄卡片 */
.hero-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 24px;
  background: white;
}

.hero-bg {
  height: 100px;
  background: linear-gradient(135deg, #3b82f6 0%, #2dd4bf 100%);
}

.hero-content {
  padding: 0 30px 30px;
  position: relative;
}

.hero-avatar {
  border: 4px solid white;
  margin-top: -40px;
  background: #f1f5f9;
  color: #3b82f6;
  font-size: 32px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.member-header {
  display: flex;
  gap: 24px;
  align-items: flex-end;
}

.member-meta {
  flex: 1;
  padding-bottom: 4px;
}

.member-name {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 8px;
}

.member-role {
  display: flex;
  gap: 8px;
}

.member-contact {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px 24px;
  border-radius: 16px;
  background: #f8fafc;
}

.contact-item {
  font-size: 13px;
  color: #475569;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 数据卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  gap: 20px;
  background: white;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.theme-blue { background: #eff6ff; color: #3b82f6; }
.theme-green { background: #ecfdf5; color: #10b981; }
.theme-orange { background: #fffbeb; color: #f59e0b; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.2;
}

.stat-value small {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}

/* Tabs 区 */
.premium-panel {
  border: none !important;
}

.tabs-card {
  padding: 10px;
}

.section-toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.premium-table {
  border-radius: 12px;
}

.font-bold {
  font-weight: 600;
  color: #0f172a;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-item {
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 13px;
  font-family: monospace;
}

.separator {
  color: #94a3b8;
  font-size: 12px;
}

.count-badge {
  background: #fef2f2;
  color: #ef4444;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 13px;
}

.action-group {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 弹窗优化 */
.dialog-desc {
  color: #64748b;
  font-size: 14px;
  margin-top: -10px;
  margin-bottom: 24px;
}

.full-width {
  width: 100%;
}

.card-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-tag {
  color: #10b981;
  font-weight: 700;
}
</style>
