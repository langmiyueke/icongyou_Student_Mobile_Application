<template>
  <div class="ai-chat-container">
    <div class="header">
      <h1>腾讯元宝 AI助手</h1>
      <p>智能问答，随时为您服务</p>
    </div>
    
    <div class="chat-container" ref="chatBox">
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        class="message" 
        :class="message.isUser ? 'user-message' : 'ai-message'"
      >
        <div class="message-bubble" :class="message.isUser ? 'user-bubble' : 'ai-bubble'">
          <span v-if="message.isThinking" class="thinking">{{ message.content }}</span>
          <span v-else>{{ message.content }}</span>
          <div class="timestamp" :class="message.isUser ? '' : 'ai-timestamp'">
            {{ message.timestamp }}
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-area">
      <input 
        type="text" 
        class="user-input" 
        v-model="userInput" 
        placeholder="请输入您的问题..." 
        autocomplete="off"
        @keypress.enter="sendMessage"
        ref="inputElement"
      >
      <button 
        class="send-btn" 
        @click="sendMessage"
        :disabled="isSending"
      >
        {{ isSending ? '发送中...' : '发送' }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue';

export default {
  name: 'AIChat',
  setup() {
    const messages = ref([
      {
        content: '您好！我是腾讯元宝AI助手，很高兴为您服务。请问有什么可以帮您的？',
        isUser: false,
        timestamp: getCurrentTime(),
        isThinking: false
      }
    ]);
    
    const userInput = ref('');
    const isSending = ref(false);
    const chatBox = ref(null);
    const inputElement = ref(null);
    
    // 模拟AI回复函数
    const getAIResponse = (userMessage) => {
      const responses = [
        "这是一个很好的问题！基于我的理解，这个问题的关键在于...",
        "感谢您的提问。根据现有信息，我的分析是...",
        "我理解您的疑惑。从技术角度来说，这个问题涉及...",
        "这个问题很有深度！让我为您详细解释一下...",
        "基于腾讯的最佳实践，我建议您可以考虑..."
      ];
      return responses[Math.floor(Math.random() * responses.length)];
    };
    
    // 获取当前时间
    function getCurrentTime() {
      const now = new Date();
      return now.getHours().toString().padStart(2, '0') + ':' + 
             now.getMinutes().toString().padStart(2, '0') + ':' + 
             now.getSeconds().toString().padStart(2, '0');
    }
    
    // 发送消息处理
    const sendMessage = async () => {
      const message = userInput.value.trim();
      if (message && !isSending.value) {
        isSending.value = true;
        
        // 添加用户消息
        messages.value.push({
          content: message,
          isUser: true,
          timestamp: getCurrentTime(),
          isThinking: false
        });
        
        userInput.value = '';
        
        // 滚动到底部
        await nextTick();
        scrollToBottom();
        
        // 添加思考中消息
        messages.value.push({
          content: '思考中...',
          isUser: false,
          timestamp: getCurrentTime(),
          isThinking: true
        });
        
        await nextTick();
        scrollToBottom();
        
        // 模拟AI思考延迟
        setTimeout(() => {
          // 移除思考中消息
          messages.value.pop();
          
          const aiResponse = getAIResponse(message);
          messages.value.push({
            content: aiResponse,
            isUser: false,
            timestamp: getCurrentTime(),
            isThinking: false
          });
          
          isSending.value = false;
          
          // 滚动到底部并聚焦输入框
          nextTick(() => {
            scrollToBottom();
            inputElement.value.focus();
          });
        }, 1500);
      }
    };
    
    // 滚动到底部
    const scrollToBottom = () => {
      if (chatBox.value) {
        chatBox.value.scrollTop = chatBox.value.scrollHeight;
      }
    };
    
    onMounted(() => {
      // 初始焦点
      inputElement.value.focus();
    });
    
    return {
      messages,
      userInput,
      isSending,
      chatBox,
      inputElement,
      sendMessage
    };
  }
}
</script>

<style scoped>
.ai-chat-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 120, 255, 0.1);
  overflow: hidden;
  min-height: 600px;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  padding: 25px 30px;
  text-align: center;
  flex-shrink: 0;
}

.header h1 {
  font-size: 28px;
  margin-bottom: 5px;
  font-weight: 600;
}

.header p {
  opacity: 0.9;
  font-size: 14px;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #fafbfc;
  min-height: 0;
}

.message {
  margin-bottom: 20px;
  display: flex;
  animation: fadeIn 0.3s ease;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 18px;
  border-radius: 18px;
  line-height: 1.4;
  word-wrap: break-word;
  position: relative;
}

.user-bubble {
  background: #1890ff;
  color: white;
  border-bottom-right-radius: 5px;
}

.ai-bubble {
  background: white;
  color: #333;
  border: 1px solid #e8f4ff;
  border-bottom-left-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.input-area {
  padding: 20px;
  background: white;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.user-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e6f7ff;
  border-radius: 25px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s ease;
  font-family: inherit;
}

.user-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
}

.send-btn {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  font-family: inherit;
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(24, 144, 255, 0.3);
}

.send-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.timestamp {
  font-size: 11px;
  color: #999;
  margin-top: 5px;
  text-align: right;
}

.ai-timestamp {
  text-align: left;
}

.thinking {
  opacity: 0.7;
  font-style: italic;
}

@keyframes fadeIn {
  from { 
    opacity: 0; 
    transform: translateY(10px); 
  }
  to { 
    opacity: 1; 
    transform: translateY(0); 
  }
}

.chat-container::-webkit-scrollbar {
  width: 6px;
}

.chat-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
  transition: background 0.3s ease;
}

.chat-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
