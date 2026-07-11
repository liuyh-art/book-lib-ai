<template>
  <div class="ai-consultant">
    <!-- 聊天面板 -->
    <transition name="slide">
      <div v-if="visible" class="chat-panel" ref="panelRef">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-avatar">
            <el-icon :size="22"><MagicStick /></el-icon>
          </div>
          <div class="header-info">
            <span class="header-title">AI 智能助手</span>
            <span class="header-status" :class="{ online: !loading }">
              {{ loading ? '思考中...' : '在线' }}
            </span>
          </div>
          <button class="close-btn" @click="visible = false">
            <el-icon><Close /></el-icon>
          </button>
        </div>

        <!-- 消息列表 -->
        <div class="chat-body" ref="bodyRef">
          <div v-if="messages.length === 0" class="empty-state">
            <el-icon :size="48" color="#409eff"><ChatLineSquare /></el-icon>
            <p class="empty-title">有什么可以帮你的？</p>
            <p class="empty-desc">请描述您的问题，我会为您提供帮助</p>
            <div class="suggestions">
              <span
                v-for="(suggestion, idx) in suggestions"
                :key="idx"
                class="suggestion-tag"
                @click="handleSuggestClick(suggestion)"
              >
                {{ suggestion }}
              </span>
            </div>
          </div>

          <div v-else class="message-list">
            <div
              v-for="(msg, idx) in messages"
              :key="idx"
              class="message-item"
              :class="msg.role"
            >
              <div class="message-avatar" v-if="msg.role === 'assistant'">
                <el-icon :size="18"><MagicStick /></el-icon>
              </div>
              <div class="message-content">
                <div class="message-bubble">
                  <!-- 支持简单的 markdown 换行 -->
                  <span v-for="(line, li) in msg.content.split('\n')" :key="li">
                    {{ line }}<br v-if="li < msg.content.split('\n').length - 1" />
                  </span>
                </div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
              <div class="message-avatar" v-if="msg.role === 'user'">
                <el-icon :size="18"><UserFilled /></el-icon>
              </div>
            </div>

            <!-- 加载中动画 -->
            <div v-if="loading" class="message-item assistant">
              <div class="message-avatar">
                <el-icon :size="18"><MagicStick /></el-icon>
              </div>
              <div class="message-content">
                <div class="message-bubble typing-indicator">
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="dot"></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-footer">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="2"
            placeholder="请输入您的问题..."
            :disabled="loading"
            @keydown.enter.prevent="handleSend"
            resize="none"
          />
          <el-button
            type="primary"
            :icon="Promotion"
            :loading="loading"
            :disabled="!inputMessage.trim()"
            class="send-btn"
            @click="handleSend"
          >
            发送
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 悬浮按钮 -->
    <div class="float-btn" @click="toggle" :class="{ 'has-unread': hasUnread }">
      <el-icon :size="28"><ChatDotSquare /></el-icon>
      <!-- 未读小红点 -->
      <span v-if="hasUnread" class="unread-dot"></span>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { Close, ChatDotSquare, MagicStick, UserFilled, Promotion, ChatLineSquare } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
// 取消注释以使用真实 API
import { sendMessage } from '@/api/ai'

const visible = ref(false)
const inputMessage = ref('')
const loading = ref(false)
const hasUnread = ref(false)
const messages = ref([])
const bodyRef = ref(null)
const panelRef = ref(null)

const suggestions = [
  '如何借阅图书？',
  '逾期了怎么办？',
  '如何查询个人借阅记录？'
]

function toggle() {
  visible.value = !visible.value
  if (visible.value) {
    hasUnread.value = false
  }
}

async function handleSend() {
  const text = inputMessage.value.trim()
  if (!text || loading.value) return

  // 用户消息
  const now = new Date()
  messages.value.push({
    role: 'user',
    content: text,
    time: now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })
  inputMessage.value = ''
  hasUnread.value = false
  scrollToBottom()

  // AI 响应
  loading.value = true
  scrollToBottom()

  try {
    // 尝试调用真实 API，失败则使用本地模拟
    let reply
    try {
      const res = await sendMessage(text)
      reply = res.data
      // throw new Error('API not configured')
    } catch {
      // reply = await getMockReply(text)
    }

    messages.value.push({
      role: 'assistant',
      content: reply,
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } catch (e) {
    ElMessage.error('回复获取失败，请稍后重试')
    messages.value.push({
      role: 'assistant',
      content: '抱歉，我暂时无法回复，请稍后重试。',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

function handleSuggestClick(text) {
  inputMessage.value = text
  handleSend()
}

function scrollToBottom() {
  nextTick(() => {
    if (bodyRef.value) {
      bodyRef.value.scrollTop = bodyRef.value.scrollHeight
    }
  })
}

/**
 * 模拟AI回复（演示用）
 */
function getMockReply(message) {
  return new Promise((resolve) => {
    const delay = 800 + Math.random() * 1200
    setTimeout(() => {
      const replies = [
        '您好！感谢您的咨询。关于这个问题，建议您先登录系统，在"借阅管理"模块中查看详细说明。如果仍有疑问，请提供更多信息。',
        '我理解您的需求。您可以在"图书管理"页面搜索相关图书，查看馆藏状态和可借阅数量。如有需要，可以直接在线提交借阅申请。',
        '感谢您的提问！根据系统规则，普通用户最多可同时借阅5本图书，借阅期限为30天。到期前可通过"个人中心"续借一次，延长15天。',
        '很高兴为您服务！您的问题已记录下来，建议您也可以查看系统的"常见问题"帮助文档。如果问题紧急，请联系管理员处理。',
        '您好！关于图书分类，系统目前支持按书名、作者、ISBN、分类号等多种方式进行检索。您可以在搜索框输入关键词快速查找。',
      ]
      const idx = Math.floor(Math.random() * replies.length)
      resolve(replies[idx])
    }, delay)
  })
}

// 关闭时判断是否设置未读标记
watch(visible, (val) => {
  if (!val && messages.value.length > 0) {
    const last = messages.value[messages.value.length - 1]
    if (last.role === 'assistant') {
      hasUnread.value = false
    } else {
      hasUnread.value = true
    }
  }
})
</script>

<style scoped>
.ai-consultant {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 2000;
}

/* 浮动按钮 */
.float-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
  transition: all 0.3s ease;
  position: relative;
  margin-left: auto;
}

.float-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.55);
}

.float-btn:active {
  transform: scale(0.95);
}

.float-btn.has-unread {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4); }
  50% { box-shadow: 0 4px 24px rgba(64, 158, 255, 0.7); }
}

.unread-dot {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 10px;
  height: 10px;
  background: #f56c6c;
  border-radius: 50%;
  border: 2px solid #fff;
}

/* 聊天面板 */
.chat-panel {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 380px;
  height: 560px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
  gap: 12px;
}

.header-avatar {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
}

.header-status {
  font-size: 12px;
  opacity: 0.8;
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-status.online::before {
  content: '';
  width: 6px;
  height: 6px;
  background: #67c23a;
  border-radius: 50%;
  display: inline-block;
}

.close-btn {
  background: none;
  border: none;
  color: #fff;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.15);
}

/* 消息体 */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  gap: 8px;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-top: 8px;
}

.empty-desc {
  font-size: 13px;
  color: #909399;
}

.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  justify-content: center;
}

.suggestion-tag {
  padding: 6px 14px;
  background: #ecf5ff;
  color: #409eff;
  border-radius: 20px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #d9ecff;
}

.suggestion-tag:hover {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

/* 消息列表 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-item.assistant .message-avatar {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: #fff;
}

.message-item.user .message-avatar {
  background: #c0c4cc;
  color: #fff;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-item.user .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.message-item.assistant .message-bubble {
  background: #fff;
  color: #303133;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.message-item.user .message-bubble {
  background: #409eff;
  color: #fff;
  border-top-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  padding: 0 4px;
}

/* 输入区 */
.chat-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 10px;
  align-items: flex-end;
  background: #fff;
}

.chat-footer .el-input {
  flex: 1;
}

.chat-footer :deep(.el-textarea__inner) {
  border-radius: 8px;
  font-size: 13px;
}

.send-btn {
  height: 36px;
  flex-shrink: 0;
  border-radius: 8px;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 14px 18px !important;
}

.typing-indicator .dot {
  width: 8px;
  height: 8px;
  background: #c0c4cc;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator .dot:nth-child(1) { animation-delay: 0s; }
.typing-indicator .dot:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator .dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

/* 过渡动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
  transform-origin: bottom right;
}

.slide-leave-to {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
  transform-origin: bottom right;
}

/* 滚动条 */
.chat-body::-webkit-scrollbar {
  width: 4px;
}

.chat-body::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 2px;
}

.chat-body::-webkit-scrollbar-track {
  background: transparent;
}
</style>
