<template>
  <div class="system-config-view">
    <div class="page-header">
      <h2>系统配置</h2>
    </div>

    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="hover" class="config-card">
          <template #header>
            <div class="card-header">系统设置</div>
          </template>

          <el-form :model="configForm" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="configForm.systemName" placeholder="FitPro" />
            </el-form-item>
            <el-form-item label="会员默认头像">
              <el-input v-model="configForm.defaultAvatar" placeholder="头像URL" />
            </el-form-item>
            <el-form-item label="签到时间范围">
              <el-row :gutter="8">
                <el-col :span="10">
                  <el-time-picker v-model="configForm.checkInStart" placeholder="开始时间" format="HH:mm" value-format="HH:mm" style="width: 100%" />
                </el-col>
                <el-col :span="1" style="text-align: center; line-height: 32px;">至</el-col>
                <el-col :span="10">
                  <el-time-picker v-model="configForm.checkInEnd" placeholder="结束时间" format="HH:mm" value-format="HH:mm" style="width: 100%" />
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="课程提前预约">
              <el-row :gutter="8">
                <el-col :span="10">
                  <el-input-number v-model="configForm.bookingAdvanceDays" :min="1" :max="30" style="width: 100%" />
                </el-col>
                <el-col :span="2" style="line-height: 32px;">天</el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="最大取消预约">
              <el-row :gutter="8">
                <el-col :span="10">
                  <el-input-number v-model="configForm.maxBookingsPerDay" :min="1" :max="10" style="width: 100%" />
                </el-col>
                <el-col :span="2" style="line-height: 32px;">节/天</el-col>
              </el-row>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存配置</el-button>
              <el-button @click="resetConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">系统信息</div>
          </template>
          <el-descriptions :column="1" direction="vertical" border>
            <el-descriptions-item label="系统版本">FitPro v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 3.2.5</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3.4 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
            <el-descriptions-item label="缓存">Redis 7.x</el-descriptions-item>
            <el-descriptions-item label="运行环境">{{ configForm.environment || '开发环境' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const saving = ref(false)
const configForm = reactive({
  systemName: 'FitPro 健身管理系统',
  defaultAvatar: '',
  checkInStart: '06:00',
  checkInEnd: '22:00',
  bookingAdvanceDays: 7,
  maxBookingsPerDay: 3,
  environment: 'dev'
})

const fetchConfig = async () => {
  try {
    const data = await adminApi.getSystemConfig()
    if (data) Object.assign(configForm, data)
  } catch (e) {
    // 配置接口暂未实现，使用默认值
    console.log('使用默认配置')
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await adminApi.updateSystemConfig({ ...configForm })
    ElMessage.success('配置保存成功')
  } catch (e) {
    ElMessage.warning('配置接口暂未实现，数据仅在前端保留')
  } finally {
    saving.value = false
  }
}

const resetConfig = () => {
  configForm.systemName = 'FitPro 健身管理系统'
  configForm.defaultAvatar = ''
  configForm.checkInStart = '06:00'
  configForm.checkInEnd = '22:00'
  configForm.bookingAdvanceDays = 7
  configForm.maxBookingsPerDay = 3
  ElMessage.info('已重置为默认值')
}

onMounted(() => fetchConfig())
</script>

<style scoped>
.system-config-view { max-width: 1100px; }
.page-header { margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.config-card, .info-card { border-radius: 12px; margin-bottom: 16px; }
.card-header { font-weight: 600; }
</style>
