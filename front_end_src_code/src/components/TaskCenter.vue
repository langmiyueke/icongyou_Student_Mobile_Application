<template>
  <div class="task-center">
    <el-container>
      <el-header class="page-header">
        <h1>任务中心</h1>
      </el-header>

      <el-main>
        <!-- 数据概览 -->
        <el-row
          :gutter="20"
          class="dashboard"
        >
          <!-- 热度指数 -->
          <el-col :span="8">
            <el-card class="metric-card">
              <template #header>
                <div class="card-header">
                  <span>热度指数</span>
                  <el-icon><TrendCharts /></el-icon>
                </div>
              </template>
              <div class="heat-index">
                <div class="heat-item">
                  <el-statistic
                    title="今日热度"
                    :value="heatData.today"
                  />
                  <el-progress
                    :percentage="heatData.today"
                    :show-text="false"
                  />
                </div>
                <div class="heat-item">
                  <el-statistic
                    title="本周热度"
                    :value="heatData.week"
                  />
                  <el-progress
                    :percentage="(heatData.week / 500) * 100"
                    :show-text="false"
                  />
                </div>
                <div class="heat-item">
                  <el-statistic
                    title="总热度"
                    :value="heatData.total"
                  />
                  <el-progress
                    :percentage="(heatData.total / 2000) * 100"
                    :show-text="false"
                  />
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 提交统计 -->
          <el-col :span="16">
            <el-card class="metric-card">
              <template #header>
                <div class="card-header">
                  <span>提交统计</span>
                  <el-icon><DataAnalysis /></el-icon>
                </div>
              </template>
              <div class="submission-stats">
                <el-row :gutter="20">
                  <el-col
                    :span="6"
                    v-for="stat in submissionStats"
                    :key="stat.day"
                  >
                    <div class="stat-item">
                      <div class="stat-value">{{ stat.value }}</div>
                      <div class="stat-label">{{ stat.day }}</div>
                      <el-progress
                        :percentage="(stat.value / 20) * 100"
                        :show-text="false"
                        :color="stat.color"
                      />
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 快速数据概览 -->
        <el-row
          :gutter="20"
          class="quick-stats"
        >
          <el-col :span="6">
            <el-statistic
              title="总任务数"
              :value="totalTasks"
            />
          </el-col>
          <el-col :span="6">
            <el-statistic
              title="进行中"
              :value="inProgressTasks"
            />
          </el-col>
          <el-col :span="6">
            <el-statistic
              title="已完成"
              :value="completedTasks"
            />
          </el-col>
          <el-col :span="6">
            <el-statistic
              title="参与人数"
              :value="totalParticipants"
            />
          </el-col>
        </el-row>

        <!-- 任务列表 -->
        <el-card class="task-list-card">
          <template #header>
            <div class="card-header">
              <span>任务列表</span>
              <el-button
                type="primary"
                :icon="Plus"
                @click="goToNewTask"
              >
                新建任务
              </el-button>
            </div>
          </template>

          <el-table
            :data="tasks"
            style="width: 100%"
            @row-click="handleTaskClick"
          >
            <el-table-column
              prop="title"
              label="任务名称"
              width="200"
            >
              <template #default="scope">
                <div class="task-title">
                  <el-icon><Document /></el-icon>
                  <span>{{ scope.row.title }}</span>
                  <el-tag
                    :type="scope.row.type === '个人' ? 'success' : 'primary'"
                    size="small"
                  >
                    {{ scope.row.type }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>

            <el-table-column
              prop="description"
              label="任务描述"
            />

            <el-table-column
              prop="deadline"
              label="截止时间"
              width="120"
            >
              <template #default="scope">
                <el-text type="danger">{{ scope.row.deadline }}</el-text>
              </template>
            </el-table-column>

            <el-table-column
              prop="participants"
              label="参与人数"
              width="100"
            >
              <template #default="scope">
                <el-text>{{ scope.row.participants }}人</el-text>
              </template>
            </el-table-column>
 <!--
            <el-table-column
              label="热度"
              width="100"
            >
              <template #default="scope">
                <el-rate
                  v-model="scope.row.heat"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </template>
            </el-table-column>  -->

            <el-table-column
              label="操作"
              width="120"
            >
              <template #default="scope">
                <el-button
                  type="primary"
                  link
                  :icon="View"
                  @click.stop="goToTaskDetail(scope.row.id)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref,watch,onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElIcon } from "element-plus";
import {
  TrendCharts,
  DataAnalysis,
  Plus,
  View,
  Document,
} from "@element-plus/icons-vue";
import { LoadTaskList ,loadHeatData,loadDataView} from "../api/index.js";
import mitt from "mitt";

const emitter = mitt();
const router = useRouter();

// 响应式数据
const heatData = ref({
  today: 85,
  week: 420,
  total: 1560,
});

const submissionStats = ref([
  { day: "周一", value: 12, color: "#409EFF" },
  { day: "周二", value: 19, color: "#67C23A" },
  { day: "周三", value: 8, color: "#E6A23C" },
  { day: "周四", value: 15, color: "#F56C6C" },
  { day: "周五", value: 12, color: "#909399" },
  { day: "周六", value: 10, color: "#409EFF" },
  { day: "周日", value: 7, color: "#67C23A" },
]);

const totalTasks = ref(15);
const inProgressTasks = ref(8);
const completedTasks = ref(5);
const totalParticipants = ref(156);

const loadDataViews = async () => {
  await loadDataView().then((response) => {
    totalTasks.value = response.dataView.totalTasks;
    inProgressTasks.value = response.dataView.inProgressTasks;
    completedTasks.value = response.dataView.completedTasks;
    totalParticipants.value = response.dataView.totalParticipants;
  })
}

const loadTasks = async ()=>{
  await LoadTaskList().then((response)=>{
    tasks.value = response
  })
}

const loadHeatDatas = async () => {
  await loadHeatData().then((response)=>{
    heatData.value = response.heatData
  })
}

 onMounted(()=>{
  loadTasks()
  loadHeatDatas()
  loadDataViews()
})

const tasks = ref([
  {
    id: 1,
    title: "Vue3组件开发",
    description: "使用Vue3开发一个可复用的业务组件",
    type: "个人",
    deadline: "2023-12-15",
    participants: 24,
    heat: 4,
  },
  {
    id: 2,
    title: "SpringBoot微服务",
    description: "设计并实现一个微服务架构",
    type: "团队",
    deadline: "2023-12-20",
    participants: 8,
    heat: 3,
  },
  {
    id: 3,
    title: "数据库设计优化",
    description: "对现有数据库进行性能优化设计",
    type: "个人",
    deadline: "2023-12-18",
    participants: 15,
    heat: 5,
  },
  {
    id: 4,
    title: "前端性能监控",
    description: "实现前端性能监控和错误追踪系统",
    type: "团队",
    deadline: "2023-12-22",
    participants: 6,
    heat: 4,
  },
]);

watch(tasks, ()=>{
  loadTasks()
})

// 方法
const handleTaskClick = (row) => {
  ElMessage.info(`点击了任务: ${row.title}`);
};

const goToTaskDetail = (taskId) => {
  emitter.emit("loadDTask", taskId);
  router.push(`/task/${taskId}`);
};

const goToNewTask = () => {
  router.push('/task/new')
}



</script>

<style scoped>
.task-center {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  align-items: center;
  background: white;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

.dashboard {
  margin-bottom: 20px;
}

.metric-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.heat-index {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.heat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.submission-stats {
  padding: 10px 0;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.quick-stats {
  margin-bottom: 20px;
}

.quick-stats .el-col {
  display: flex;
  justify-content: center;
}

.task-list-card {
  margin-top: 20px;
}

.task-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-table .el-table__row) {
  cursor: pointer;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}
</style>
