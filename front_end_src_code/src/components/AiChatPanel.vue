<template>
  <div class="ai-chat-panel">
    <el-row :gutter="20">
      <!-- 聊天主区域 -->
      <el-col :span="16">
        <el-card class="chat-card">
          <template #header>
            <div class="card-header">
              <div class="ai-info">
                <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                <div class="ai-details">
                  <div class="ai-name">AI助手</div>
                  <div class="ai-status">
                    <el-tag type="success" size="small">在线</el-tag>
                  </div>
                </div>
              </div>
              <div class="chat-actions">
                <el-button :icon="Refresh" @click="clearChat">清空对话</el-button>
                <el-button :icon="Download" @click="exportChat">导出记录</el-button>
              </div>
            </div>
          </template>

          <!-- 聊天消息区域 -->
          <div class="chat-messages" ref="messagesContainer">
            <div
              v-for="message in chatMessages"
              :key="message.id"
              :class="['message', message.type]"
            >
              <div class="message-avatar">
                <el-avatar
                  :size="36"
                  :src="message.type === 'ai' 
                    ? 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
                    : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                />
              </div>
              <div class="message-content">
                <div class="message-bubble">
                  <div class="message-text" v-html="formatMessage(message.content)"></div>
                  <div class="message-time">{{ message.time }}</div>
                </div>
                <div class="message-actions" v-if="message.type === 'ai'">
                  <el-button-group size="small">
                    <el-button :icon="CopyDocument" @click="copyMessage(message)">
                      复制
                    </el-button>
                    <el-button :icon="Star" @click="likeMessage(message)">
                      有用
                    </el-button>
                    <el-button :icon="ChatDotRound" @click="replyMessage(message)">
                      追问
                    </el-button>
                  </el-button-group>
                </div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="chat-input">
            <div class="quick-questions">
              <el-text type="info">快速提问：</el-text>
              <el-space wrap>
                <el-tag
                  v-for="question in quickQuestions"
                  :key="question"
                  type="info"
                  effect="plain"
                  class="quick-question"
                  @click="sendQuickQuestion(question)"
                >
                  {{ question }}
                </el-tag>
              </el-space>
            </div>
            <div class="input-area">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入您的问题..."
                :maxlength="1000"
                show-word-limit
                @keydown.enter.exact.prevent="sendMessage"
              />
              <div class="input-actions">
                <div class="input-tools">
                  <el-tooltip content="上传文件">
                    <el-button :icon="Upload" text />
                  </el-tooltip>
                  <el-tooltip content="代码片段">
                    <el-button :icon="Document" text />
                  </el-tooltip>
                  <el-tooltip content="表情">
                    <el-button :icon="Star" text />
                  </el-tooltip>
                </div>
                <el-button
                  type="primary"
                  :icon="Promotion"
                  :loading="isLoading"
                  @click="sendMessage"
                >
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 侧边栏 -->
      <el-col :span="8">
        <!-- 任务相关提示 -->
        <el-card class="sidebar-card">
          <template #header>
            <div class="card-header">
              <span>任务提示</span>
            </div>
          </template>
          <div class="task-tips">
            <el-timeline>
              <el-timeline-item
                v-for="tip in taskTips"
                :key="tip.id"
                :timestamp="tip.time"
                :type="tip.type"
              >
                {{ tip.content }}
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>

        <!-- 常见问题 -->
        <el-card class="sidebar-card">
          <template #header>
            <div class="card-header">
              <span>常见问题</span>
            </div>
          </template>
          <div class="faq-list">
            <el-collapse>
              <el-collapse-item
                v-for="faq in faqList"
                :key="faq.id"
                :title="faq.question"
              >
                <div class="faq-answer">{{ faq.answer }}</div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>

        <!-- 学习资源 -->
        <el-card class="sidebar-card">
          <template #header>
            <div class="card-header">
              <span>学习资源</span>
            </div>
          </template>
          <div class="resources-list">
            <div
              v-for="resource in learningResources"
              :key="resource.id"
              class="resource-item"
              @click="openResource(resource)"
            >
              <el-icon><component :is="resource.icon" /></el-icon>
              <span class="resource-title">{{ resource.title }}</span>
              <el-tag :type="resource.type" size="small">{{ resource.tag }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  CopyDocument,
  Star,
  ChatDotRound,
  Promotion,
  Upload,
  Document,
  VideoPlay,
  Reading
} from '@element-plus/icons-vue'

const props = defineProps({
  taskId: {
    type: [String, Number],
    required: true
  }
})

const messagesContainer = ref(null)
const inputMessage = ref('')
const isLoading = ref(false)

const chatMessages = ref([
  {
    id: 1,
    type: 'ai',
    content: '您好！我是AI助手，专门为您解答关于Vue3组件开发的问题。我可以帮助您解决技术难题、提供代码示例、解释概念等。请随时提问！',
    time: '14:30'
  }
])

const quickQuestions = ref([
  '如何实现Vue3响应式组件？',
  'Composition API的最佳实践？',
  '如何处理组件间的通信？',
  '如何进行组件测试？'
])

const taskTips = ref([
  {
    id: 1,
    time: '2023-12-10',
    type: 'primary',
    content: '建议先学习Vue3基础概念'
  },
  {
    id: 2,
    time: '2023-12-09',
    type: 'success',
    content: '组件设计要考虑可复用性'
  },
  {
    id: 3,
    time: '2023-12-08',
    type: 'warning',
    content: '注意TypeScript类型定义'
  }
])

const faqList = ref([
  {
    id: 1,
    question: 'Vue3和Vue2的主要区别是什么？',
    answer: 'Vue3引入了Composition API、更好的TypeScript支持、性能优化等新特性。'
  },
  {
    id: 2,
    question: '如何实现组件的响应式数据？',
    answer: '可以使用ref()和reactive()函数来创建响应式数据。'
  },
  {
    id: 3,
    question: '什么是Teleport组件？',
    answer: 'Teleport可以将组件的内容渲染到DOM中的其他位置。'
  }
])

const learningResources = ref([
  {
    id: 1,
    title: 'Vue3官方文档',
    icon: Document,
    type: 'primary',
    tag: '文档',
    url: 'https://vuejs.org/'
  },
  {
    id: 2,
    title: '组件开发教程',
    icon: VideoPlay,
    type: 'success',
    tag: '视频',
    url: '#'
  },
  {
    id: 3,
    title: '最佳实践指南',
    icon: Reading,
    type: 'warning',
    tag: '文章',
    url: '#'
  }
])

const formatMessage = (content) => {
  // 简单的消息格式化，可以扩展为Markdown解析
  return content.replace(/\n/g, '<br>')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息')
    return
  }

  // 添加用户消息
  const userMessage = {
    id: Date.now(),
    type: 'user',
    content: inputMessage.value,
    time: new Date().toLocaleTimeString()
  }
  chatMessages.value.push(userMessage)
  
  const userInput = inputMessage.value
  inputMessage.value = ''
  isLoading.value = true
  
  scrollToBottom()

  // 模拟AI回复
  setTimeout(() => {
    const aiResponse = generateAIResponse(userInput)
    const aiMessage = {
      id: Date.now() + 1,
      type: 'ai',
      content: aiResponse,
      time: new Date().toLocaleTimeString()
    }
    chatMessages.value.push(aiMessage)
    isLoading.value = false
    scrollToBottom()
  }, 1000)
}

const generateAIResponse = (question) => {
  // 简单的关键词匹配回复，实际应用中应该调用AI接口
  const responses = {
    'vue3': 'Vue3是一个渐进式JavaScript框架，相比Vue2有更好的性能和开发体验。',
    '组件': '在Vue3中，组件可以使用Composition API来组织代码，提高可复用性。',
    '响应式': 'Vue3使用Proxy实现响应式系统，比Vue2的Object.defineProperty更强大。',
    '默认': `关于"${question}"，这是一个很好的问题！在Vue3组件开发中，建议：
1. 使用Composition API组织代码逻辑
2. 充分利用TypeScript的类型系统
3. 编写可复用的组合式函数
4. 注意组件的性能优化

您能具体说明一下遇到什么问题吗？`
  }

  for (const [key, response] of Object.entries(responses)) {
    if (question.toLowerCase().includes(key)) {
      return response
    }
  }
  
  return responses['默认']
}

const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

const clearChat = () => {
  chatMessages.value = [{
    id: 1,
    type: 'ai',
    content: '对话已清空，请问有什么可以帮助您的？',
    time: new Date().toLocaleTimeString()
  }]
  ElMessage.success('对话已清空')
}

const exportChat = () => {
  ElMessage.success('聊天记录导出成功')
}

const copyMessage = (message) => {
  navigator.clipboard.writeText(message.content)
  ElMessage.success('消息已复制到剪贴板')
}

const likeMessage = (message) => {
  ElMessage.success('感谢反馈！')
}

const replyMessage = (message) => {
  inputMessage.value = `关于您刚才说的"${message.content.substring(0, 50)}..."，我想了解更多：`
}

const openResource = (resource) => {
  ElMessage.info(`打开资源: ${resource.title}`)
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-chat-panel {
  padding: 0;
}

.chat-card {
  height: 600px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-details {
  display: flex;
  flex-direction: column;
}

.ai-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
  max-height: 400px;
}

.message {
  display: flex;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 12px;
}

.message-content {
  max-width: 70%;
}

.message.user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
}

.message.user .message-bubble {
  background: #409EFF;
  color: white;
}

.message-text {
  line-height: 1.5;
  margin-bottom: 4px;
}

.message-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.message.user .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.message-actions {
  margin-top: 8px;
}

.chat-input {
  border-top: 1px solid #e4e7ed;
  padding-top: 16px;
}

.quick-questions {
  margin-bottom: 16px;
}

.quick-question {
  cursor: pointer;
  transition: all 0.3s;
}

.quick-question:hover {
  background-color: #409EFF;
  color: white;
}

.input-area {
  position: relative;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.input-tools {
  display: flex;
  gap: 8px;
}

.sidebar-card {
  margin-bottom: 20px;
}

.task-tips {
  max-height: 200px;
  overflow-y: auto;
}

.faq-list {
  max-height: 300px;
  overflow-y: auto;
}

.faq-answer {
  color: #606266;
  line-height: 1.5;
}

.resources-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.resource-item:hover {
  background-color: #f5f7fa;
}

.resource-title {
  flex: 1;
  font-size: 14px;
}
</style>