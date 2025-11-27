<template>
  <div class="course-detail">
    <div class="top-nav">
      <van-nav-bar :title="course.name || '课程详情'" left-text="返回" left-arrow @click-left="goBack">

      </van-nav-bar>
    </div>
    <div class="nav-placeholder" aria-hidden="true"></div>

    <div>
      <div class="top-tabs">
          <van-button size="small" plain :class="{active: activeTab===0}" @click="activeTab=0">课程详情</van-button>
          <van-button size="small" plain :class="{active: activeTab===1}" @click="activeTab=1">课程资料</van-button>
          <van-button size="small" plain :class="{active: activeTab===2}" @click="activeTab=2">任务中心</van-button>
          <van-button size="small" plain :class="{active: activeTab===3}" @click="activeTab=3">个人数据</van-button>
      </div>
        <div class="top-tabs-placeholder" aria-hidden="true"></div>

      <div class="tab-content">
        <div v-show="activeTab===0" class="detail-card">
          <div class="head">
            <div class="cover" :style="`background-image: url(${course.image || defaultCover})`"></div>
            <div class="info">
              <h2>{{ course.name }}</h2>
              <div class="meta-grid">
                <div class="meta-item">
                  <div class="meta-label">教师</div>
                  <div class="meta-value">{{ course._teacherName || teacherName }}</div>
                </div>
                  <div class="meta-item">
                    <div class="meta-label">状态</div>
                    <div class="meta-value">{{ statusLabel(course.status) }}</div>
                  </div>
                <div class="meta-item">
                  <div class="meta-label">课程编号</div>
                  <div class="meta-value">{{ course.code || '—' }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">学时</div>
                  <div class="meta-value">{{ course.duration || '—' }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">类型</div>
                  <div class="meta-value">{{ typeLabel(course.type) }}</div>
                </div>
                
                <div class="meta-item">
                  <div class="meta-label">作业</div>
                  <div class="meta-value">{{ course.task_number ?? '—' }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">选课人数</div>
                  <div class="meta-value">{{ course.student_number ?? '—' }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">学期</div>
                  <div class="meta-value">{{ course.semester_id || '—' }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">开始</div>
                  <div class="meta-value">{{ formatDate(course.start_time) }}</div>
                </div>
                <div class="meta-item">
                  <div class="meta-label">结束</div>
                  <div class="meta-value">{{ formatDate(course.end_time) }}</div>
                </div>
              </div>
              <!-- description 不是 Course 表的物理列，后端可按需返回；前端仅在存在时显示 -->
              <div v-if="course.description" class="desc">{{ course.description }}</div>
              <div class="progress-wrap">
                <van-progress v-if="typeof course.progress === 'number'" :percentage="course.progress" />
              </div>
              
            </div>
          </div>
        </div>

        <div v-show="activeTab===1" class="tab-panel">
          <CourseMaterials />
        </div>

        <div v-show="activeTab===2" class="tab-panel">
          <TaskCenter />
        </div>

        <div v-show="activeTab===3" class="tab-panel">
          <PersonalPanel :courseId="panelCourseId" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourse } from '@/api/courses'
import defaultImg from '@/assets/logo.png'
import PersonalPanel from '@/views/courses/components/PersonalPanel.vue'
import TaskCenter from '@/views/courses/components/TaskCenter.vue'
import CourseMaterials from '@/views/courses/components/CourseMaterials.vue'

export default {
  name: 'CourseDetail',
  components: { PersonalPanel, TaskCenter, CourseMaterials },
  setup () {
    const route = useRoute()
    const router = useRouter()
    const id = route.params.id
    const course = ref({ id: null, name: '', teacher: '', description: '', image: '', code: '', duration: 0, start_time: '', end_time: '', task_number: 0, student_number: 0, semester_id: null, create_time: '' })
    const defaultCover = defaultImg

    const goBack = () => {
      // 始终返回课程列表页，避免在深层嵌套或来自外部链接时返回到不期望的历史记录
      router.replace({ path: '/courses' })
    }

    const panelCourseId = computed(() => {
      if (course.value && course.value.id !== undefined && course.value.id !== null) return course.value.id
      return route.params && route.params.id ? route.params.id : null
    })

    const activeTab = ref(0)

    if (route.name === 'course-tasks' || (route.query && route.query.tab === 'tasks')) {
      activeTab.value = 2
    }

    watchEffect(() => {
      if (route.name === 'course-tasks' || (route.query && route.query.tab === 'tasks')) {
        activeTab.value = 2
      }
    })

    const teacherName = computed(() => {
      if (course.value && course.value._teacherName) return course.value._teacherName
      const t = course.value && course.value.teacher
      if (!t) return ''
      if (typeof t === 'string') return t
      if (t.name) return t.name
      return ''
    })

    const statusMap = {
      1: '未开课',
      2: '进行中',
      3: '已结课'
    }
    const statusLabel = (s) => { if (s === undefined || s === null) return '—'; return statusMap[Number(s)] || String(s) }

    const typeMap = {
      1: '实训课程',
      2: '活动课程',
      3: '必修课',
      4: '选修课',
      5: '公共基础课'
    }
    const typeLabel = (t) => { if (t === undefined || t === null) return '—'; return typeMap[Number(t)] || String(t) }

    const formatDate = (iso) => {
      if (!iso) return ''
      try {
        const d = new Date(iso)
        if (isNaN(d.getTime())) return iso
        return d.toLocaleString()
      } catch (e) {
        return iso
      }
    }

    onMounted(async () => {
      try {
        const c = await getCourse(id)
        course.value = c
        // resolve teacher name via teachers API
        try {
          const { getTeacher } = await import('@/api/teachers')
          const t = await getTeacher(c.teacher_id)
          if (t && t.name) course.value._teacherName = t.name
        } catch (e) {
          // ignore
        }
      } catch (err) {
        console.error(err)
      }
    })

    const openTasks = () => {
        activeTab.value = 2
    }

    return { course, defaultCover, goBack, teacherName, formatDate, panelCourseId, openTasks, activeTab, statusLabel, typeLabel }
  }
}
</script>

<style scoped>
.course-detail { padding: 12px }
.detail-card { background:#fff; border-radius:10px; padding:12px; box-shadow:0 6px 18px rgba(0,0,0,0.04) }
.head { display:flex; gap:16px; align-items:flex-start; flex-wrap:wrap }
.cover { width:140px; height:100px; background-size:cover; background-position:center; border-radius:8px; flex:0 0 140px }
.info { flex:1; min-width:180px }
.info h2 { margin:0 0 8px; font-size:20px; text-align:left }
.teacher { color:#6b7280; margin-bottom:8px }
.desc { color:#374151; margin-top:8px }
.meta-grid { display:grid; grid-template-columns: repeat(2, 1fr); gap:10px 14px; margin-bottom:10px }
.meta-item { background: #f8fbfd; padding:10px 12px; border-radius:10px; display:flex; flex-direction:column; align-items:center }
.meta-label { font-size:12px; color:#6b7280 }
.meta-value { font-weight:700; color:#0f3740; margin-top:6px }
.progress-wrap { margin-top:14px }

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 70;
  background: #ffffff;
  display: flex;
  align-items: center;
  height: var(--nav-bar-height, 56px);
  box-shadow: 0 1px 0 rgba(0,0,0,0.04);
}
.nav-placeholder { height: var(--nav-bar-height, 56px) }

.top-nav .van-nav-bar { width: 100%; height: 100%; padding: 0 12px; box-sizing: border-box }
.top-nav .van-nav-bar__left { z-index:75; color:#0f1724 }
.top-nav .van-nav-bar__title { z-index:72 }

.top-tabs {
  position: fixed;
  top: calc(var(--nav-bar-height,56px));
  left: 0;
  right: 0;
  z-index: 60;
  background: #f5f7fa;
  display: flex;
  justify-content: space-around;
  gap: 10px;
  padding: 6px 12px;
  box-shadow: 0 1px 0 rgba(0,0,0,0.04);
  align-items: center;
  height: 48px;
}
.top-tabs-placeholder { height: 48px }
.top-tabs .van-button {
  padding: 6px 12px;
  border-radius: 6px;
  background: transparent;
  border: none;
  color: #6b7280;
  box-shadow: none;
}
.top-tabs .van-button.active {
  background: transparent;
  color: #1677ff;
  box-shadow: none;
  border-bottom: 2px solid #1677ff;
  border-radius: 0;
}
.tab-content { padding: 8px 12px 40px }
.tab-panel { padding-top: 6px }
</style>