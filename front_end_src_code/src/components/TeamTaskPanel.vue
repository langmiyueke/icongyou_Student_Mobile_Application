<template>
  <div class="team-task-panel">
    <el-row :gutter="20">
      <!-- 团队成员 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>团队成员 ({{ teamMembers.length }})</span>
              <el-button
                type="primary"
                text
                :icon="Plus"
                >邀请成员</el-button
              >
            </div>
          </template>

          <el-table
            :data="teamMembers"
            style="width: 100%"
            empty-text="暂无团队成员"
          >
            <el-table-column
              label="成员"
              width="200"
            >
              <template #default="scope">
                <div class="member-info">
                  <el-avatar
                    :size="36"
                    :src="scope.row.avatar"
                  />
                  <div class="member-details">
                    <div class="member-name">{{ scope.row.name }}</div>
                    <div class="member-email">{{ scope.row.email }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column
              label="角色"
              width="100"
            >
              <template #default="scope">
                <el-tag
                  :type="scope.row.role === '组长' ? 'danger' : 'primary'"
                >
                  {{ scope.row.role }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column
              prop="progress"
              label="进度"
              width="120"
            >
              <template #default="scope">
                <el-progress
                  :percentage="scope.row.progress"
                  :show-text="false"
                />
                <span style="font-size: 12px; color: #909399"
                  >{{ scope.row.progress }}%</span
                >
              </template>
            </el-table-column>

            <el-table-column
              label="状态"
              width="80"
            >
              <template #default="scope">
                <el-tag
                  :type="scope.row.status === '在线' ? 'success' : 'info'"
                  size="small"
                >
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 团队进度和活动 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>团队进度</span>
            </div>
          </template>

          <div class="team-progress">
            <el-progress
              type="circle"
              :percentage="teamProgress"
              :width="120"
              :color="progressColor"
            />
            <div class="progress-stats">
              <div class="stat-item">
                <span class="label">总任务:</span>
                <span class="value">{{ totalTasks }}</span>
              </div>
              <div class="stat-item">
                <span class="label">已完成:</span>
                <span class="value success">{{ completedTasks }}</span>
              </div>
              <div class="stat-item">
                <span class="label">进行中:</span>
                <span class="value warning">{{ inProgressTasks }}</span>
              </div>
              <div class="stat-item">
                <span class="label">待开始:</span>
                <span class="value info">{{ pendingTasks }}</span>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="recent-activity">
            <h4>最近活动</h4>
            <el-timeline>
              <el-timeline-item
                v-for="activity in recentActivities"
                :key="activity.id"
                :timestamp="activity.time"
                :type="activity.type"
                :icon="getActivityIcon(activity.type)"
              >
                <div class="activity-content">
                  <span class="activity-user">{{ activity.user }}</span>
                  <span>{{ activity.content }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 团队任务分配 -->
    <el-card class="task-assignment">
      <template #header>
        <div class="card-header">
          <span>任务分配</span>
          <el-button
            type="primary"
            :icon="Plus"
            >分配任务</el-button
          >
        </div>
      </template>

      <el-table
        :data="teamTasks"
        style="width: 100%"
      >
        <el-table-column
          prop="title"
          label="任务名称"
          width="200"
        />
        <el-table-column
          label="负责人"
          width="120"
        >
          <template #default="scope">
            <div class="assignee">
              <el-avatar
                :size="24"
                :src="scope.row.assignee.avatar"
              />
              <span>{{ scope.row.assignee.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="deadline"
          label="截止时间"
          width="120"
        />
        <el-table-column
          label="优先级"
          width="100"
        >
          <template #default="scope">
            <el-tag :type="getPriorityType(scope.row.priority)">
              {{ scope.row.priority }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          width="100"
        >
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="120"
        >
          <template #default="scope">
            <el-button
              type="primary"
              link
              :icon="Edit"
              >编辑</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { Plus, Edit, Check, Clock, Warning } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { loadTeamMembers, loadTeamProcess } from "../api/index.js";

const props = defineProps({
  taskId: {
    type: [String, Number],
    required: true,
  },
});

const teamMembers = ref([
  {
    id: 1,
    name: "张三",
    email: "zhangsan@example.com",
    role: "组长",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    progress: 80,
    status: "在线",
  },
  {
    id: 2,
    name: "李四",
    email: "lisi@example.com",
    role: "前端开发",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    progress: 65,
    status: "在线",
  },
  {
    id: 3,
    name: "王五",
    email: "wangwu@example.com",
    role: "后端开发",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    progress: 45,
    status: "离线",
  },
  {
    id: 4,
    name: "赵六",
    email: "zhaoliu@example.com",
    role: "测试",
    avatar: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    progress: 30,
    status: "在线",
  },
]);

const teamProgress = ref(65);
const totalTasks = ref(20);
const completedTasks = ref(13);
const inProgressTasks = ref(5);
const pendingTasks = ref(2);

// 异步加载团队成员信息
const loadTeamMember = async () => {
  try {
    
    const res = await loadTeamMembers(props.taskId);
    console.log(res);
    teamMembers.value = res.teamMembers;
    
  } catch (error) {
    console.error('加载团队成员失败:', error);
    ElMessage.error('加载团队成员失败');
  }
};

// 加载团队进度信息
const loadTeamP = async () => {
  try {
    const res = await loadTeamProcess(props.taskId);
    teamProgress.value = res.teamProgress;
    totalTasks.value = res.totalTasks;
    completedTasks.value = res.completedTasks;
    inProgressTasks.value = res.inProgressTasks;
    pendingTasks.value = res.pendingTasks;

  } catch (error) {
    console.error('加载团队进度失败:', error);
    ElMessage.error('加载团队进度失败');
  }
};

onMounted(() => {
  console.log('组件已挂载，开始加载数据...');
  loadTeamMember();
  loadTeamP();
});

// 其余代码保持不变...
</script>
<style scoped>
.team-task-panel {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-details {
  display: flex;
  flex-direction: column;
}

.member-name {
  font-weight: 500;
  margin-bottom: 2px;
}

.member-email {
  font-size: 12px;
  color: #909399;
}

.team-progress {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 20px 0;
}

.progress-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 150px;
}

.stat-item .label {
  color: #606266;
}

.stat-item .value {
  font-weight: 500;
}

.stat-item .value.success {
  color: #67c23a;
}

.stat-item .value.warning {
  color: #e6a23c;
}

.stat-item .value.info {
  color: #909399;
}

.recent-activity h4 {
  margin: 0 0 16px 0;
  color: #303133;
}

.activity-content {
  display: flex;
  gap: 8px;
  align-items: center;
}

.activity-user {
  font-weight: 500;
  color: #409eff;
}

.task-assignment {
  margin-top: 20px;
}

.assignee {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
