<template>
  <div class="membership-manage-view">
    <div class="page-header">
      <h2>会籍管理</h2>
      <el-button type="primary" @click="showCreateCardDialog">
        <el-icon><Plus /></el-icon>新增卡种
      </el-button>
    </div>

    <!-- Tabs: 卡种管理 / 会籍管理 -->
    <el-tabs v-model="activeTab" class="main-tabs">
      <!-- 卡种管理 -->
      <el-tab-pane label="卡种管理" name="cards">
        <el-card v-loading="cardLoading" class="table-card">
          <el-empty v-if="!cardLoading && cards.length === 0" description="暂无卡种" />
          <el-table v-else :data="cards" stripe style="width: 100%">
            <el-table-column prop="cardName" label="卡种名称" width="120" />
            <el-table-column label="类型" width="80" align="center">
              <template #default="{ row }">
                {{ typeLabel(row.cardType) }}
              </template>
            </el-table-column>
            <el-table-column prop="durationDays" label="天数" width="70" align="center">
              <template #default="{ row }">
                {{ row.cardType === 'TIMES' ? '--' : row.durationDays }}
              </template>
            </el-table-column>
            <el-table-column prop="totalTimes" label="次数" width="70" align="center">
              <template #default="{ row }">
                {{ row.cardType === 'TIMES' ? row.totalTimes : '--' }}
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="100" align="right">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-switch
                  :model-value="row.status === 1"
                  @change="handleToggleCardStatus(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="showEditCardDialog(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 会籍管理 -->
      <el-tab-pane label="会籍管理" name="memberships">
        <el-card class="toolbar-card">
          <el-form :inline="true" size="small">
            <el-form-item label="会员">
              <el-select v-model="memberUserId" placeholder="选择会员" filterable clearable @change="fetchMemberships" style="width: 200px">
                <el-option v-for="m in memberList" :key="m.id" :label="m.nickname" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="showAssignDialog">办理会籍</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card v-loading="membershipLoading" class="table-card">
          <el-empty v-if="!membershipLoading && filteredMemberships.length === 0" description="暂无会籍记录" />
          <el-table v-else :data="filteredMemberships" stripe style="width: 100%">
            <el-table-column prop="userName" label="会员" width="100" />
            <el-table-column prop="cardName" label="卡种" width="100" />
            <el-table-column label="类型" width="60" align="center">
              <template #default="{ row }"> {{ typeLabel(row.cardType) }} </template>
            </el-table-column>
            <el-table-column label="有效期" min-width="160">
              <template #default="{ row }">
                {{ formatDate(row.startDate) }} ~ {{ row.endDate ? formatDate(row.endDate) : '--' }}
              </template>
            </el-table-column>
            <el-table-column prop="remainingTimes" label="剩余次数" width="80" align="center" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">
                  {{ statusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center" fixed="right">
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
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑卡种弹窗 -->
    <el-dialog
      v-model="cardDialogVisible"
      :title="isEditingCard ? '编辑卡种' : '新增卡种'"
      width="450px"
      top="10vh"
      destroy-on-close
    >
      <el-form ref="cardFormRef" :model="cardForm" :rules="cardFormRules" label-width="100px">
        <el-form-item label="卡种名称" prop="cardName">
          <el-input v-model="cardForm.cardName" placeholder="如: 月卡、季卡" />
        </el-form-item>
        <el-form-item label="卡种类型" prop="cardType">
          <el-select v-model="cardForm.cardType" placeholder="选择类型" style="width: 100%">
            <el-option label="月卡" value="MONTH" />
            <el-option label="季卡" value="QUARTER" />
            <el-option label="年卡" value="YEAR" />
            <el-option label="次卡" value="TIMES" />
          </el-select>
        </el-form-item>
        <el-form-item label="有效天数" prop="durationDays" v-if="cardForm.cardType !== 'TIMES'">
          <el-input-number v-model="cardForm.durationDays" :min="1" :max="3650" style="width: 100%" />
        </el-form-item>
        <el-form-item label="总次数" prop="totalTimes" v-if="cardForm.cardType === 'TIMES'">
          <el-input-number v-model="cardForm.totalTimes" :min="1" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="cardForm.price" :min="0" :step="50" :precision="2" style="width: 100%">
            <template #prefix>¥</template>
          </el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cardDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="cardSaving" @click="handleSaveCard">保存</el-button>
      </template>
    </el-dialog>

    <!-- 办理会籍弹窗 -->
    <el-dialog
      v-model="assignVisible"
      title="办理会籍"
      width="400px"
      top="15vh"
      destroy-on-close
    >
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignFormRules" label-width="100px">
        <el-form-item label="选择会员" prop="userId">
          <el-select v-model="assignForm.userId" placeholder="搜索会员" filterable style="width: 100%">
            <el-option v-for="m in memberList" :key="m.id" :label="m.nickname" :value="m.id" />
          </el-select>
        </el-form-item>
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
    <el-dialog
      v-model="renewVisible"
      title="续费会籍"
      width="400px"
      top="15vh"
      destroy-on-close
    >
      <el-form ref="renewFormRef" :model="renewForm" :rules="renewFormRules" label-width="100px">
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
import { ref, computed, onMounted, reactive } from 'vue'
import { membershipApi } from '@/api/membership'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('cards')

// ========== 卡种管理 ==========
const cards = ref([])
const cardLoading = ref(false)
const cardDialogVisible = ref(false)
const isEditingCard = ref(false)
const editingCardId = ref(null)
const cardSaving = ref(false)
const cardFormRef = ref(null)

const cardForm = reactive({
  cardName: '',
  cardType: '',
  durationDays: 30,
  totalTimes: 30,
  price: 0
})

const cardFormRules = {
  cardName: [{ required: true, message: '请输入卡种名称', trigger: 'blur' }],
  cardType: [{ required: true, message: '请选择卡种类型', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const typeLabel = (val) => ({
  MONTH: '月卡', QUARTER: '季卡', YEAR: '年卡', TIMES: '次卡'
}[val] || val)

const fetchCards = async () => {
  cardLoading.value = true
  try {
    cards.value = await membershipApi.listCards()
  } catch (e) {
    console.error('获取卡种失败:', e)
  } finally {
    cardLoading.value = false
  }
}

const showCreateCardDialog = () => {
  isEditingCard.value = false
  editingCardId.value = null
  cardForm.cardName = ''
  cardForm.cardType = ''
  cardForm.durationDays = 30
  cardForm.totalTimes = 30
  cardForm.price = 0
  cardDialogVisible.value = true
}

const showEditCardDialog = (row) => {
  isEditingCard.value = true
  editingCardId.value = row.id
  cardForm.cardName = row.cardName
  cardForm.cardType = row.cardType
  cardForm.durationDays = row.durationDays || 30
  cardForm.totalTimes = row.totalTimes || 30
  cardForm.price = row.price
  cardDialogVisible.value = true
}

const handleSaveCard = async () => {
  const valid = await cardFormRef.value.validate().catch(() => false)
  if (!valid) return

  cardSaving.value = true
  try {
    const data = {
      cardName: cardForm.cardName,
      cardType: cardForm.cardType,
      durationDays: cardForm.cardType !== 'TIMES' ? cardForm.durationDays : null,
      totalTimes: cardForm.cardType === 'TIMES' ? cardForm.totalTimes : null,
      price: cardForm.price
    }
    if (isEditingCard.value) {
      await membershipApi.updateCard(editingCardId.value, data)
      ElMessage.success('卡种更新成功')
    } else {
      await membershipApi.createCard(data)
      ElMessage.success('卡种创建成功')
    }
    cardDialogVisible.value = false
    await fetchCards()
  } catch (e) {
    console.error('保存卡种失败:', e)
  } finally {
    cardSaving.value = false
  }
}

const handleToggleCardStatus = async (row) => {
  try {
    await membershipApi.toggleCardStatus(row.id)
    ElMessage.success('状态已切换')
    await fetchCards()
  } catch (e) {
    console.error('切换状态失败:', e)
  }
}

// ========== 会籍管理 ==========
const memberList = ref([])
const memberships = ref([])
const membershipLoading = ref(false)
const memberUserId = ref(null)
const assignVisible = ref(false)
const assignSaving = ref(false)
const assignFormRef = ref(null)
const renewVisible = ref(false)
const renewSaving = ref(false)
const renewFormRef = ref(null)
const renewingMembershipId = ref(null)

const assignForm = reactive({ userId: null, cardId: null })
const renewForm = reactive({ cardId: null })

const assignFormRules = {
  userId: [{ required: true, message: '请选择会员', trigger: 'change' }],
  cardId: [{ required: true, message: '请选择卡种', trigger: 'change' }]
}

const renewFormRules = {
  cardId: [{ required: true, message: '请选择卡种', trigger: 'change' }]
}

const availableCards = computed(() => cards.value.filter(c => c.status === 1))

const filteredMemberships = computed(() => {
  if (!memberUserId.value) return memberships.value
  return memberships.value.filter(m => m.userId === memberUserId.value)
})

const statusLabel = (val) => ({
  ACTIVE: '活跃', FROZEN: '冻结', EXPIRED: '已过期', CANCELLED: '已取消'
}[val] || val)

const statusType = (val) => ({
  ACTIVE: 'success', FROZEN: 'warning', EXPIRED: 'info', CANCELLED: 'danger'
}[val] || 'info')

const formatDate = (dateStr) => {
  if (!dateStr) return '--'
  return dateStr.substring(0, 10)
}

const fetchMembers = async () => {
  try {
    const res = await userApi.list({ role: 'MEMBER', pageNum: 1, pageSize: 999 })
    memberList.value = res.list || []
  } catch (e) {
    console.error('获取会员列表失败:', e)
  }
}

const fetchMemberships = async () => {
  membershipLoading.value = true
  try {
    memberships.value = await membershipApi.listAllMemberships()
  } catch (e) {
    console.error('获取会籍列表失败:', e)
  } finally {
    membershipLoading.value = false
  }
}

const showAssignDialog = () => {
  assignForm.userId = null
  assignForm.cardId = null
  assignVisible.value = true
}

const handleAssign = async () => {
  const valid = await assignFormRef.value.validate().catch(() => false)
  if (!valid) return

  assignSaving.value = true
  try {
    await membershipApi.assign(assignForm)
    ElMessage.success('会籍办理成功')
    assignVisible.value = false
    await fetchMemberships()
  } catch (e) {
    console.error('办理会籍失败:', e)
  } finally {
    assignSaving.value = false
  }
}

const showRenewDialog = (row) => {
  renewingMembershipId.value = row.id
  renewForm.cardId = null
  renewVisible.value = true
}

const handleRenew = async () => {
  const valid = await renewFormRef.value.validate().catch(() => false)
  if (!valid) return

  renewSaving.value = true
  try {
    await membershipApi.renew({ membershipId: renewingMembershipId.value, cardId: renewForm.cardId })
    ElMessage.success('续费成功')
    renewVisible.value = false
    await fetchMemberships()
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
    await fetchMemberships()
  } catch (e) {
    console.error('冻结失败:', e)
  }
}

const handleUnfreeze = async (row) => {
  try {
    await membershipApi.unfreeze(row.id)
    ElMessage.success('会籍已解冻')
    await fetchMemberships()
  } catch (e) {
    console.error('解冻失败:', e)
  }
}

const handleCancel = async (row) => {
  try {
    await membershipApi.cancel(row.id)
    ElMessage.success('会籍已取消')
    await fetchMemberships()
  } catch (e) {
    console.error('退卡失败:', e)
  }
}

onMounted(() => {
  fetchCards()
  fetchMembers()
  fetchMemberships()
})
</script>

<style scoped>
.membership-manage-view {
  max-width: 1100px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.table-card {
  border-radius: 12px;
  margin-top: 16px;
}

.toolbar-card {
  border-radius: 12px;
}
</style>
