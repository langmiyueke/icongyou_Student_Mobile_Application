<template>
  <div class="new-task">
    <el-container>
      <el-header class="page-header">
        <el-page-header @back="router.back()">
          <template #content>
            <span class="text-large font-600 mr-3">新建任务</span>
          </template>
        </el-page-header>
      </el-header>
      
      <el-main>
        <el-card class="task-form-card">
          <template #header>
            <div class="card-header">
              <span>任务信息</span>
            </div>
          </template>
          
          <el-form 
            ref="taskFormRef" 
            :model="taskForm" 
            :rules="taskRules" 
            label-width="120px"
            class="task-form"
          >
            <el-form-item label="任务标题" prop="title">
              <el-input 
                v-model="taskForm.title" 
                placeholder="请输入任务标题"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="任务类型" prop="type">
              <el-radio-group v-model="taskForm.type">
                <el-radio label="个人">个人任务</el-radio>
                <el-radio label="团队">团队任务</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="任务描述" prop="description">
              <el-input
                v-model="taskForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入任务详细描述"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="截止时间" prop="deadline">
              <el-date-picker
                v-model="taskForm.deadline"
                type="date"
                placeholder="选择截止日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
              />
            </el-form-item>
            
            <el-form-item label="任务难度" prop="difficulty">
              <el-rate
                v-model="taskForm.difficulty"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                show-text
              />
            </el-form-item>
            
            <el-form-item label="最大参与人数" prop="maxParticipants">
              <el-input-number
                v-model="taskForm.maxParticipants"
                :min="1"
                :max="100"
                controls-position="right"
              />
            </el-form-item>
            
            <el-form-item label="任务标签" prop="tags">
              <el-select
                v-model="taskForm.tags"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="选择或输入标签"
                style="width: 100%"
              >
                <el-option
                  v-for="tag in presetTags"
                  :key="tag"
                  :label="tag"
                  :value="tag"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitTask" :loading="loading">
                创建任务
              </el-button>
              <el-button @click="resetForm">重置</el-button>
              <el-button @click="router.back()">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {newTask} from '../api/index.js'

const router = useRouter()
const taskFormRef = ref()
const loading = ref(false)

// 表单数据
const taskForm = reactive({
  title: '',
  type: '个人',
  description: '',
  deadline: '',
  difficulty: 3,
  maxParticipants: 10,
  tags: [] // 不要了
})

// 预设标签
const presetTags = ['前端', '后端', 'Vue', 'React', 'SpringBoot', '数据库', '算法', '设计', '测试']

// 表单验证规则
const taskRules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入任务描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  deadline: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最大参与人数', trigger: 'blur' }
  ]
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 提交任务
const submitTask = async () => {
  if (!taskFormRef.value) return
  
  try {
    const valid = await taskFormRef.value.validate()
    if (!valid) return
    
    loading.value = true
    
    // 模拟API调用
    await newTask(taskForm).then((response)=>{
      ElMessage.success('任务创建成功！')
    }).catch((error)=>{
      ElMessage.error('表单验证失败，请检查输入')
    })
    router.push(`/task/${newTaskId}`)
  } catch (error) {
    //ElMessage.error('表单验证失败，请检查输入')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  taskFormRef.value?.resetFields()
  taskForm.tags = []
  taskForm.difficulty = 3
  taskForm.maxParticipants = 10
}

// 保存草稿（可选功能）
const saveDraft = () => {
  const draftData = { ...taskForm }
  localStorage.setItem('taskDraft', JSON.stringify(draftData))
  ElMessage.info('已保存为草稿')
}

// 加载草稿（可选功能）
const loadDraft = () => {
  const draft = localStorage.getItem('taskDraft')
  if (draft) {
    ElMessageBox.confirm('检测到有未保存的草稿，是否加载？', '提示', {
      confirmButtonText: '加载',
      cancelButtonText: '忽略',
      type: 'warning'
    }).then(() => {
      const draftData = JSON.parse(draft)
      Object.assign(taskForm, draftData)
    })
  }
}

// 组件挂载时加载草稿
// onMounted(() => {
//   loadDraft()
// })
</script>

<style scoped>
.new-task {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-header {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
}

.task-form-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  font-size: 16px;
}

.task-form {
  padding: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-textarea .el-input__count) {
  background: transparent;
}
</style>