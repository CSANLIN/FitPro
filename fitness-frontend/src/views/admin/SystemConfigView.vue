<template>
  <div class="system-config-view">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">系统核心配置</h1>
        <p class="page-subtitle">调整系统全局运行参数与环境变量</p>
      </div>
    </div>

    <el-row :gutter="24">
      <el-col :xs="24" :lg="16">
        <el-card class="premium-panel config-card">
          <template #header>
            <div class="card-header">
              <span class="panel-title">运行参数设置</span>
            </div>
          </template>

          <el-form :model="configForm" label-width="140px" label-position="left" class="config-form">
            <div class="form-section-title">基础品牌配置</div>
            <el-form-item label="系统对外名称">
              <el-input v-model="configForm.systemName" placeholder="例如：FitPro 旗舰中心" class="premium-input" />
            </el-form-item>
            <el-form-item label="默认会员头像 URL">
              <el-input v-model="configForm.defaultAvatar" placeholder="https://..." class="premium-input" />
            </el-form-item>

            <div class="divider"></div>
            <div class="form-section-title">业务规则配置</div>
            
            <el-form-item label="允许签到时段">
              <div class="time-range-picker">
                <el-time-picker v-model="configForm.checkInStart" placeholder="开启时间" format="HH:mm" value-format="HH:mm" class="flex-1" />
                <span class="range-separator">至</span>
                <el-time-picker v-model="configForm.checkInEnd" placeholder="结束时间" format="HH:mm" value-format="HH:mm" class="flex-1" />
              </div>
            </el-form-item>
            
            <el-form-item label="课程预约窗口">
              <div class="input-with-unit">
                提前 <el-input-number v-model="configForm.bookingAdvanceDays" :min="1" :max="30" class="number-input" /> 天可预约
              </div>
            </el-form-item>
            
            <el-form-item label="防刷课限制">
              <div class="input-with-unit">
                单人单日最多可预约 <el-input-number v-model="configForm.maxBookingsPerDay" :min="1" :max="10" class="number-input" /> 节课程
              </div>
            </el-form-item>

            <div class="form-actions">
              <el-button @click="resetConfig" round plain>恢复默认配置</el-button>
              <el-button type="primary" color="#3b82f6" :loading="saving" @click="handleSave" round>保存并立即生效</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="premium-panel info-card">
          <template #header>
            <div class="card-header">
              <span class="panel-title">服务器运行环境</span>
            </div>
          </template>
          <div class="sys-info-list">
            <div class="info-item">
              <div class="info-label">核心版本</div>
              <div class="info-value"><el-tag size="small" effect="dark" round>FitPro Admin v1.2</el-tag></div>
            </div>
            <div class="info-item">
              <div class="info-label">后端运行架构</div>
              <div class="info-value">Spring Boot 3.2.5</div>
            </div>
            <div class="info-item">
              <div class="info-label">前端驱动</div>
              <div class="info-value">Vue 3.4 + Vite 5</div>
            </div>
            <div class="info-item">
              <div class="info-label">数据持久层</div>
              <div class="info-value">MySQL 8.0 + MyBatis-Plus</div>
            </div>
            <div class="info-item">
              <div class="info-label">高速缓存</div>
              <div class="info-value">Redis 7.2 (Standalone)</div>
            </div>
            <div class="info-item">
              <div class="info-label">当前运行模式</div>
              <div class="info-value"><span class="status-dot"></span> {{ configForm.environment === 'prod' ? '生产环境 (Production)' : '开发环境 (Development)' }}</div>
            </div>
          </div>
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
  checkInEnd: '23:30',
  bookingAdvanceDays: 7,
  maxBookingsPerDay: 3,
  environment: 'dev'
})

const fetchConfig = async () => {
  try {
    const data = await adminApi.getSystemConfig()
    if (data) Object.assign(configForm, data)
  } catch (e) {
    console.log('使用默认配置')
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await adminApi.updateSystemConfig({ ...configForm })
    ElMessage.success('系统配置已成功保存')
  } catch (e) {
    ElMessage.warning('目前仅在前端展示，待后端实现持久化')
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
  ElMessage.info('参数已重置为出厂设置')
}

onMounted(() => fetchConfig())
</script>

<style scoped>
.system-config-view { max-width: 1200px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 800; margin: 0 0 4px; color: #0f172a; }
.page-subtitle { font-size: 14px; color: #64748b; margin: 0; }

.premium-panel { border: none !important; margin-bottom: 24px; }
.card-header { font-weight: 700; font-size: 16px; color: #0f172a; }

.config-form { padding: 10px 0; }

.form-section-title {
  font-size: 14px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 20px;
  letter-spacing: 0.5px;
}

.divider {
  height: 1px;
  background-color: #f1f5f9;
  margin: 32px 0 24px;
}

.time-range-picker {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 320px;
}

.range-separator { color: #94a3b8; font-size: 13px; }

.input-with-unit {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #475569;
}

.number-input { width: 140px; }

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px dashed #e2e8f0;
}

/* 系统信息列表 */
.sys-info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.info-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.6);
}
</style>
