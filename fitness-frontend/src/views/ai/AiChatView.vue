<template>
  <div class="ai-chat-view">
    <div class="chat-header">
      <el-button text @click="goBack" :disabled="streaming">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <div class="header-info">
        <div class="header-avatar">
          <el-icon :size="20"><MagicStick /></el-icon>
        </div>
        <div>
          <h2 class="header-title">健身小助手</h2>
          <p class="header-status" v-if="streaming">正在输入...</p>
          <p class="header-status" v-else>在线</p>
        </div>
      </div>
    </div>

    <div class="message-area" ref="messageArea">
      <div v-if="messages.length === 0 && !loadingSummary" class="welcome-section">
        <div class="welcome-icon">
          <el-icon :size="48"><MagicStick /></el-icon>
        </div>
        <h3 class="welcome-title">你好！我是健身小助手</h3>
        <p class="welcome-desc">我可以回答你的健身问题，分析训练数据，生成训练总结</p>
        <div class="suggestion-grid">
          <div class="suggestion-card" v-for="(q, i) in quickQuestions" :key="i" @click="handleQuickQuestion(q)">
            <el-icon><component :is="q.icon" /></el-icon>
            <span>{{ q.label }}</span>
          </div>
        </div>
      </div>

      <div v-for="(msg, idx) in messages" :key="idx" class="message-row"
        :class="msg.role === 'user' ? 'user-row' : 'ai-row'">
        <div v-if="msg.role === 'ai'" class="msg-avatar ai-avatar">
          <el-icon :size="18"><MagicStick /></el-icon>
        </div>
        <div class="message-bubble" :class="msg.role === 'user' ? 'user-bubble' : 'ai-bubble'">
          <div v-if="msg.role === 'user'" class="message-content">{{ msg.content }}</div>
          <div v-else class="message-content markdown-body" v-html="renderMarkdown(msg.content)"></div>
        </div>
        <div v-if="msg.role === 'user'" class="msg-avatar user-avatar">
          <span>{{ userInitial }}</span>
        </div>
      </div>

      <div v-if="streaming" class="message-row ai-row">
        <div class="msg-avatar ai-avatar">
          <el-icon :size="18"><MagicStick /></el-icon>
        </div>
        <div class="message-bubble ai-bubble">
          <div class="message-content">{{ streamingContent }}<span class="typing-cursor">|</span></div>
        </div>
      </div>

      <div v-if="loadingSummary" class="message-row ai-row">
        <div class="msg-avatar ai-avatar">
          <el-icon :size="18"><MagicStick /></el-icon>
        </div>
        <div class="message-bubble ai-bubble">
          <div class="loading-dots">
            <span class="dot"></span><span class="dot"></span><span class="dot"></span>
          </div>
        </div>
      </div>

      <div v-if="errorMsg" class="error-tip">
        <el-alert :title="errorMsg" type="error" show-icon :closable="false" />
        <el-button size="small" round @click="retryLast" class="retry-btn">重试</el-button>
      </div>
    </div>

    <div class="input-area">
      <div class="input-wrapper">
        <el-input
          v-model="inputText"
          :disabled="streaming || loadingSummary"
          placeholder="输入你的健身问题..."
          maxlength="2000"
          show-word-limit
          @keyup.enter="sendMessage"
          class="chat-input"
        >
          <template #prefix>
            <el-icon><ChatLineRound /></el-icon>
          </template>
        </el-input>
        <el-button
          type="primary"
          :icon="Promotion"
          :disabled="!inputText.trim() || streaming || loadingSummary"
          @click="sendMessage"
          class="send-btn"
          circle
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { aiApi, chatWithAI } from '@/api/ai'
import { marked } from 'marked'
import {
  ArrowLeft, MagicStick, ChatLineRound, Promotion,
  QuestionFilled, TrendCharts, Timer, MilkTea
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const messages = ref([])
const inputText = ref('')
const streaming = ref(false)
const streamingContent = ref('')
const loadingSummary = ref(false)
const errorMsg = ref('')
const messageArea = ref(null)
let abortController = null

const userInitial = computed(() => {
  return authStore.userInfo?.nickname?.charAt(0) || 'U'
})

const renderMarkdown = (text) => {
  if (!text) return ''
  try {
    let cleaned = text
      // 1. 修复标题：##text → ## text（#后面必须有空格）
      .replace(/^(#{1,6})([^\s#]{1,100}.*)$/gm, '$1 $2')
      // 2. 修复表格分隔行（--| → |---|，---| → |---|）
      .replace(/^[-|]+\|$/gm, '|---|')
      // 3. 修复表格行尾多余竖线（|a|b|| → |a|b|）
      .replace(/^(\|.*)\|(\|)$/gm, '$1|')
      // 4. 合并连续空行（超过2个 → 2个）
      .replace(/\n{3,}/g, '\n\n')
      // 5. 分隔线 --- 前后补空行
      .replace(/([^\n])---([^\n])/g, '$1\n\n---\n\n$2')
      .replace(/([^\n])---\n/g, '$1\n\n---\n')
      .replace(/\n---([^\n])/g, '\n---\n\n$1')

    return marked.parse(cleaned, { breaks: true })
  } catch {
    return text
  }
}

const quickQuestions = [
  { label: '如何正确做深蹲？', icon: QuestionFilled, text: '如何正确做深蹲？请详细说明动作要领、常见错误和注意事项。' },
  { label: '减脂期训练建议', icon: TrendCharts, text: '减脂期应该怎么安排训练？请给一个一周训练计划建议。' },
  { label: '训练后吃什么', icon: MilkTea, text: '力量训练后应该吃什么？有哪些饮食建议？' },
  { label: '生成本周总结', icon: Timer, text: null, action: 'weeklySummary' }
]

const scrollToBottom = async () => {
  await nextTick()
  if (messageArea.value) {
    messageArea.value.scrollTop = messageArea.value.scrollHeight
  }
}

watch([messages, streamingContent, loadingSummary], () => {
  scrollToBottom()
}, { deep: true })

const handleQuickQuestion = (q) => {
  if (q.action === 'weeklySummary') {
    fetchSummary('weekly')
  } else {
    inputText.value = q.text
    sendMessage()
  }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || streaming.value || loadingSummary.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  errorMsg.value = ''

  streaming.value = true
  streamingContent.value = ''

  abortController = chatWithAI(text, {
    onChunk: (chunk) => {
      streamingContent.value += chunk
    },
    onDone: () => {
      if (streamingContent.value) {
        messages.value.push({ role: 'ai', content: streamingContent.value })
        // 保存 AI 回复到后端对话历史
        aiApi.saveResponse(streamingContent.value).catch(() => {})
      }
      streamingContent.value = ''
      streaming.value = false
      abortController = null
    },
    onError: (err) => {
      console.error('AI 对话出错:', err)
      errorMsg.value = err.message || 'AI 服务暂时不可用，请稍后重试'
      streaming.value = false
      streamingContent.value = ''
      abortController = null
    }
  })
}

const fetchSummary = async (type) => {
  if (loadingSummary.value || streaming.value) return
  loadingSummary.value = true
  errorMsg.value = ''
  const label = type === 'weekly' ? '本周训练总结' : '本月训练总结'
  messages.value.push({ role: 'user', content: `请生成${label}` })
  try {
    let result
    if (type === 'weekly') {
      result = await aiApi.weeklySummary()
    } else {
      result = await aiApi.monthlySummary()
    }
    messages.value.push({ role: 'ai', content: result || '暂无训练数据，开始训练后再来查看总结吧！' })
  } catch (e) {
    console.error('获取总结失败:', e)
    messages.value.push({ role: 'ai', content: '生成总结失败，请稍后重试。' })
  } finally {
    loadingSummary.value = false
  }
}

const retryLast = () => {
  errorMsg.value = ''
  if (messages.value.length >= 2) {
    const lastUserMsg = messages.value.filter(m => m.role === 'user').pop()
    if (lastUserMsg) {
      inputText.value = lastUserMsg.content
      messages.value.splice(messages.value.indexOf(lastUserMsg), 1)
      const idx = messages.value.indexOf(lastUserMsg)
      if (idx + 1 < messages.value.length && messages.value[idx + 1].role === 'ai') {
        messages.value.splice(idx + 1, 1)
      }
      sendMessage()
    }
  }
}

const goBack = () => {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  router.back()
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-chat-view {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 180px);
  max-width: 700px;
  margin: 0 auto;
}
.chat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0 16px;
  position: sticky;
  top: 0;
  z-index: 10;
  background: var(--bg-base);
}
.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.header-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
}
.header-status {
  font-size: 12px;
  color: #10b981;
  margin: 2px 0 0;
}
.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 8px 4px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.message-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}
.user-row {
  flex-direction: row-reverse;
}
.ai-row {
  flex-direction: row;
}
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ai-avatar {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
}
.user-avatar {
  background: #1e293b;
  color: white;
  font-size: 14px;
  font-weight: 700;
}
.message-bubble {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.6;
  font-size: 14px;
}
.user-bubble {
  background: #1e293b;
  color: white;
  border-bottom-right-radius: 6px;
}
.ai-bubble {
  background: white;
  color: #1e293b;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.message-content {
  white-space: pre-wrap;
  word-break: break-word;
}
/* Markdown 渲染样式（只对完整消息生效） */
.markdown-body :deep(p) { margin: 0 0 8px; }
.markdown-body :deep(p:last-child) { margin-bottom: 0; }
.markdown-body :deep(strong) { font-weight: 700; }
.markdown-body :deep(ul), .markdown-body :deep(ol) { margin: 4px 0; padding-left: 20px; }
.markdown-body :deep(li) { margin-bottom: 4px; }
.markdown-body :deep(h1), .markdown-body :deep(h2), .markdown-body :deep(h3),
.markdown-body :deep(h4) { margin: 12px 0 6px; font-weight: 700; }
.markdown-body :deep(h1) { font-size: 16px; }
.markdown-body :deep(h2) { font-size: 15px; }
.markdown-body :deep(h3) { font-size: 14px; }
.markdown-body :deep(code) { background: #f1f5f9; padding: 2px 6px; border-radius: 4px; font-size: 13px; }
.markdown-body :deep(pre) { background: #f8fafc; padding: 12px; border-radius: 8px; overflow-x: auto; margin: 8px 0; }
.markdown-body :deep(blockquote) { border-left: 3px solid #6366f1; padding-left: 12px; color: #64748b; margin: 8px 0; }

.typing-cursor {
  animation: blink 1s step-end infinite;
  color: #6366f1;
  font-weight: 700;
}
@keyframes blink {
  50% { opacity: 0; }
}
.welcome-section {
  text-align: center;
  padding: 40px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.welcome-icon {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 16px;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}
.welcome-title {
  font-size: 22px;
  font-weight: 800;
  margin: 0 0 8px;
  color: #1e293b;
}
.welcome-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 32px;
}
.suggestion-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  max-width: 400px;
  width: 100%;
}
.suggestion-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}
.suggestion-card:hover {
  border-color: #6366f1;
  color: #6366f1;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1);
  transform: translateY(-2px);
}
.loading-dots {
  display: flex;
  gap: 6px;
  padding: 4px 0;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #6366f1;
  animation: bounce 1.4s ease-in-out infinite;
}
.dot:nth-child(2) { animation-delay: 0.16s; }
.dot:nth-child(3) { animation-delay: 0.32s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}
.error-tip { text-align: center; padding: 8px 0; }
.retry-btn { margin-top: 8px; }
.input-area {
  padding: 12px 0 8px;
  position: sticky;
  bottom: 0;
  background: var(--bg-base);
}
.input-wrapper { display: flex; gap: 8px; align-items: center; }
.chat-input { flex: 1; }
.chat-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  padding: 4px 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04) !important;
}
.send-btn { flex-shrink: 0; }
</style>
