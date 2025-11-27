<template>
  <div class="excellent-work-panel">
    <!-- 筛选和排序 -->
    <el-card class="filter-card">
      <div class="filter-row">
        <el-space :size="20">
          <div class="filter-item">
            <span class="filter-label">排序方式:</span>
            <el-select v-model="sortBy" placeholder="请选择排序方式">
              <el-option label="最新发布" value="time" />
              <el-option label="最多点赞" value="likes" />
              <el-option label="最高评分" value="rating" />
            </el-select>
          </div>
          <div class="filter-item">
            <span class="filter-label">作品类型:</span>
            <el-select v-model="workType" placeholder="请选择作品类型">
              <el-option label="全部" value="all" />
              <el-option label="代码实现" value="code" />
              <el-option label="设计文档" value="doc" />
              <el-option label="演示视频" value="video" />
            </el-select>
          </div>
          <el-button type="primary" :icon="Upload" @click="showSubmitDialog = true">
            提交我的作业
          </el-button>
        </el-space>
      </div>
    </el-card>

    <!-- 优秀作业列表 -->
    <div class="works-grid">
      <el-card
        v-for="work in filteredWorks"
        :key="work.id"
        class="work-card"
        shadow="hover"
      >
        <template #header>
          <div class="work-header">
            <div class="author-info">
              <el-avatar :size="40" :src="work.author.avatar" />
              <div class="author-details">
                <div class="author-name">{{ work.author.name }}</div>
                <div class="submit-time">{{ work.submitTime }}</div>
              </div>
            </div>
            <div class="work-rating">
              <el-rate v-model="work.rating" disabled show-score text-color="#ff9900" />
            </div>
          </div>
        </template>
        
        <div class="work-content">
          <h3 class="work-title">{{ work.title }}</h3>
          <p class="work-description">{{ work.description }}</p>
          
          <div class="work-files">
            <el-tag
              v-for="file in work.files"
              :key="file.name"
              :type="getFileType(file.type)"
              class="file-tag"
              @click="downloadFile(file)"
            >
              <el-icon><component :is="getFileIcon(file.type)" /></el-icon>
              {{ file.name }}
            </el-tag>
          </div>
          
          <div class="work-preview" v-if="work.preview">
            <img v-if="work.preview.type === 'image'" :src="work.preview.url" alt="预览图" class="preview-image" />
            <div v-else-if="work.preview.type === 'video'" class="video-preview">
              <video controls :src="work.preview.url" class="preview-video"></video>
            </div>
          </div>
        </div>
        
        <template #footer>
          <div class="work-actions">
            <div class="action-buttons">
              <el-button type="success" :icon="View" @click="viewWorkDetail(work)">
                查看详情
              </el-button>
              <el-button :icon="Download" @click="downloadWork(work)">
                下载
              </el-button>
            </div>
            <div class="action-stats">
              <el-button :icon="Star" text :class="{ liked: work.isLiked }" @click="toggleLike(work)">
                {{ work.likes }}
              </el-button>
              <el-button :icon="ChatDotRound" text @click="showComments(work)">
                {{ work.comments }}
              </el-button>
              <el-button :icon="Share" text @click="shareWork(work)">
                分享
              </el-button>
            </div>
          </div>
        </template>
      </el-card>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="filteredWorks.length === 0" description="暂无优秀作业" />

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[6, 12, 18, 24]"
        :total="totalWorks"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 提交作业对话框 -->
    <el-dialog v-model="showSubmitDialog" title="提交作业" width="600px">
      <el-form :model="submitForm" label-width="80px">
        <el-form-item label="作业标题">
          <el-input v-model="submitForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="作业描述">
          <el-input
            v-model="submitForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的作业内容"
          />
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
            v-model:file-list="submitForm.files"
            multiple
            :limit="5"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">支持上传代码、文档、图片等文件，单个文件不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSubmitDialog = false">取消</el-button>
        <el-button type="primary" @click="submitWork">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  View,
  Download,
  Star,
  ChatDotRound,
  Share,
  Document,
  Picture,
  VideoPlay
} from '@element-plus/icons-vue'

const props = defineProps({
  taskId: {
    type: [String, Number],
    required: true
  }
})

const sortBy = ref('time')
const workType = ref('all')
const currentPage = ref(1)
const pageSize = ref(6)
const showSubmitDialog = ref(false)

const submitForm = ref({
  title: '',
  description: '',
  files: []
})

const excellentWorks = ref([
  {
    id: 1,
    title: 'Vue3响应式组件完整实现',
    description: '基于Vue3 Composition API实现的响应式数据表格组件，支持排序、筛选、分页等功能。组件采用TypeScript编写，具有完整的类型定义和单元测试。',
    author: {
      id: 1,
      name: '李四',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    submitTime: '2023-12-10 14:30',
    files: [
      { name: 'DataTable.vue', type: 'code' },
      { name: 'types.ts', type: 'code' },
      { name: '设计文档.pdf', type: 'doc' },
      { name: '效果图.png', type: 'image' }
    ],
    preview: {
      type: 'image',
      url: 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png'
    },
    likes: 15,
    comments: 8,
    rating: 4.7,
    isLiked: false,
    type: 'code'
  },
  {
    id: 2,
    title: '微服务架构设计方案',
    description: '完整的SpringBoot微服务架构设计方案，包含服务注册发现、配置中心、API网关、熔断降级等核心组件的实现方案。',
    author: {
      id: 2,
      name: '王五',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    submitTime: '2023-12-09 16:20',
    files: [
      { name: '架构设计文档.pdf', type: 'doc' },
      { name: 'docker-compose.yml', type: 'code' }
    ],
    likes: 23,
    comments: 12,
    rating: 4.9,
    isLiked: true,
    type: 'doc'
  },
  {
    id: 3,
    title: '组件演示视频',
    description: '录制的组件功能演示视频，展示了组件的各项功能和实际使用场景。',
    author: {
      id: 3,
      name: '赵六',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    submitTime: '2023-12-08 10:15',
    files: [
      { name: 'demo.mp4', type: 'video' }
    ],
    preview: {
      type: 'video',
      url: '/videos/demo.mp4'
    },
    likes: 8,
    comments: 3,
    rating: 4.5,
    isLiked: false,
    type: 'video'
  }
])

const filteredWorks = computed(() => {
  let works = [...excellentWorks.value]
  
  // 按类型筛选
  if (workType.value !== 'all') {
    works = works.filter(work => work.type === workType.value)
  }
  
  // 排序
  works.sort((a, b) => {
    switch (sortBy.value) {
      case 'likes':
        return b.likes - a.likes
      case 'rating':
        return b.rating - a.rating
      case 'time':
      default:
        return new Date(b.submitTime) - new Date(a.submitTime)
    }
  })
  
  return works
})

const totalWorks = computed(() => filteredWorks.value.length)

const getFileType = (fileType) => {
  const types = {
    code: '',
    doc: 'warning',
    image: 'success',
    video: 'danger'
  }
  return types[fileType] || ''
}

const getFileIcon = (fileType) => {
  const icons = {
    code: Document,
    doc: Document,
    image: Picture,
    video: VideoPlay
  }
  return icons[fileType] || Document
}

const viewWorkDetail = (work) => {
  ElMessage.info(`查看作业详情: ${work.title}`)
}

const downloadWork = (work) => {
  ElMessage.success(`开始下载: ${work.title}`)
}

const downloadFile = (file) => {
  ElMessage.info(`下载文件: ${file.name}`)
}

const toggleLike = (work) => {
  work.isLiked = !work.isLiked
  work.likes += work.isLiked ? 1 : -1
  ElMessage.success(work.isLiked ? '已点赞' : '已取消点赞')
}

const showComments = (work) => {
  ElMessageBox.alert(`查看 ${work.title} 的评论`, '作业评论', {
    confirmButtonText: '确定'
  })
}

const shareWork = (work) => {
  ElMessage.success(`已复制分享链接: ${work.title}`)
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传5个文件')
}

const submitWork = () => {
  if (!submitForm.value.title || !submitForm.value.description) {
    ElMessage.error('请填写完整的作业信息')
    return
  }
  
  ElMessage.success('作业提交成功！')
  showSubmitDialog.value = false
  // 重置表单
  submitForm.value = {
    title: '',
    description: '',
    files: []
  }
}
</script>

<style scoped>
.excellent-work-panel {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  color: #606266;
  font-size: 14px;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.work-card {
  height: 100%;
  transition: all 0.3s ease;
}

.work-card:hover {
  transform: translateY(-4px);
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.submit-time {
  font-size: 12px;
  color: #909399;
}

.work-content {
  min-height: 200px;
}

.work-title {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.work-description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.work-files {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.file-tag {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.work-preview {
  margin-top: 12px;
}

.preview-image {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.video-preview {
  width: 100%;
}

.preview-video {
  width: 100%;
  max-height: 200px;
  border-radius: 4px;
}

.work-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-stats {
  display: flex;
  gap: 8px;
}

.action-stats .el-button {
  padding: 4px 8px;
}

.action-stats .liked {
  color: #e6a23c;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>