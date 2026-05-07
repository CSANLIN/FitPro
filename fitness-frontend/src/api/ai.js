import request from '@/utils/request'

export const aiApi = {
  weeklySummary: () => request.get('/ai/summary/weekly'),
  monthlySummary: () => request.get('/ai/summary/monthly'),
  saveResponse: (response) => request.post('/ai/chat/save-response', { response })
}

/**
 * AI 流式对话（使用 Fetch API 处理 SSE 流）
 * @param {string} content 用户消息
 * @param {object} callbacks 回调函数
 * @param {function} callbacks.onChunk 每收到一个文本块时调用
 * @param {function} callbacks.onDone 流结束时调用
 * @param {function} callbacks.onError 出错时调用
 * @returns {AbortController} 可用于取消请求
 */
export function chatWithAI(content, { onChunk, onDone, onError }) {
  const controller = new AbortController()
  const token = localStorage.getItem('token')

  fetch('/api/ai/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ content }),
    signal: controller.signal
  })
    .then(async (response) => {
      if (!response.ok) {
        const text = await response.text().catch(() => '')
        throw new Error(text || `请求失败 (${response.status})`)
      }

      const contentType = response.headers.get('content-type') || ''
      const reader = response.body.getReader()
      const decoder = new TextDecoder()

      // 先读取第一块，判断是 SSE 还是 JSON 错误
      const first = await reader.read()
      if (first.done) {
        onDone?.()
        return
      }
      const firstText = decoder.decode(first.value, { stream: true })

      // 如果返回的是 JSON 错误（非 SSE 格式），解析并报错
      if (firstText.trim().startsWith('{')) {
        try {
          const errData = JSON.parse(firstText)
          throw new Error(errData.message || '请求失败')
        } catch (e) {
          if (e instanceof SyntaxError) {
            throw new Error('AI 服务返回格式异常')
          }
          throw e
        }
      }

      // 正常 SSE 流处理
      let buffer = firstText
      while (true) {
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          const trimmed = line.trim()
          if (!trimmed.startsWith('data:') && !trimmed.startsWith('data: ')) continue
          let data = trimmed.substring(5)
          if (data.startsWith(' ')) data = data.substring(1)
          if (data === '[DONE]') {
            onDone?.()
            return
          }
          onChunk?.(data)
        }

        const { done, value } = await reader.read()
        if (done) {
          // 处理 buffer 中剩余的完整行
          if (buffer.trim()) {
            const trimmed = buffer.trim()
            if (trimmed.startsWith('data:') || trimmed.startsWith('data: ')) {
              let data = trimmed.substring(5)
              if (data.startsWith(' ')) data = data.substring(1)
              if (data !== '[DONE]') onChunk?.(data)
            }
          }
          onDone?.()
          break
        }
        buffer += decoder.decode(value, { stream: true })
      }
    })
    .catch((err) => {
      if (err.name !== 'AbortError') {
        onError?.(err)
      }
    })

  return controller
}
