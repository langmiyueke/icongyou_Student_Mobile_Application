<template>
  <div class="course-card" @click="$emit('open', course)">
    <div class="course-card-inner">
      <div class="thumb">
        <div class="cover" :style="`background-image: url(${course.image || defaultCover})`"></div>
      </div>
      <div class="meta">
        <div class="title">{{ course.name }}</div>
        <div class="teacher">{{ teacherName }}</div>
        <div class="tags">
          <van-tag v-if="course.type" plain type="primary" style="margin-right:6px">{{ typeLabel(course.type) }}</van-tag>
          <van-tag v-for="t in course.tags || []" :key="t" plain type="info" style="margin-right:6px">{{ t }}</van-tag>
        </div>
        <div class="footer">
          <div class="progress-wrap">
            <van-progress v-if="typeof course.progress === 'number'" :percentage="course.progress" inactive>
              <template #default>
                <div class="progress-text">{{ course.progress }}%</div>
              </template>
            </van-progress>
          </div>
          <div>
            <van-button size="small" type="primary" @click.stop="$emit('enter', course)">进入课程</van-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, onMounted } from 'vue'
import defaultImg from '@/assets/logo.png'
import { getTeacher } from '@/api/teachers'
export default {
  name: 'CourseCard',
  props: {
    course: { type: Object, required: true }
  },
  setup (props) {
    const defaultCover = defaultImg
    const typeMap = {
      1: '实训课程',
      2: '活动课程',
      3: '必修课',
      4: '选修课',
      5: '公共基础课'
    }
    const typeLabel = (t) => {
      if (t === undefined || t === null) return ''
      return typeMap[Number(t)] || ''
    }

    const teacherName = ref('')
    onMounted(async () => {
      // first try existing embedded teacher (back-compat), otherwise fetch by teacher_id
      const c = props.course || {}
      if (c.teacher) {
        if (typeof c.teacher === 'string') teacherName.value = c.teacher
        else if (c.teacher.name) teacherName.value = c.teacher.name
      } else if (c.teacher_id) {
        try {
          const t = await getTeacher(c.teacher_id)
          teacherName.value = t ? t.name : ''
        } catch (e) {
          teacherName.value = ''
        }
      }
    })

    return { defaultCover, typeLabel, teacherName }
  }
}
</script>

<style scoped>
.course-card { display:flex; gap:8px; padding:6px; border-radius:10px; background:transparent; align-items:center; cursor:pointer }
.course-card-inner { flex:1; background:#fff; box-shadow:0 1px 6px rgba(0,0,0,0.04); border-radius:10px; display:flex; gap:10px; padding:10px; align-items:center }
.thumb { flex:0 0 72px }
.cover { width:72px; height:72px; background-size:cover; background-position:center; border-radius:8px }
.meta { display:flex; flex-direction:column; flex:1 }
.title { font-weight:600; color:#111827; font-size:15px; margin-bottom:4px }
.teacher { color:#6b7280; font-size:13px; margin-bottom:6px }
.tags { margin-bottom:6px; display:flex; gap:6px; flex-wrap:wrap }
.footer { display:flex; justify-content:space-between; align-items:center; gap:8px }
.progress { color:#6b7280; font-size:12px }
.enter-btn { background:#409EFF; color:#fff; border:none; padding:6px 10px; border-radius:6px; cursor:pointer }
.enter-btn:hover { opacity:0.95 }
.course-card-inner:hover { transform: translateY(-2px); box-shadow:0 6px 12px rgba(0,0,0,0.06); transition: all 160ms ease }

@media (max-width: 420px) {
  .course-card-inner { padding:8px }
  .thumb { flex:0 0 60px }
  .cover { width:60px; height:60px }
  .title { font-size:14px }
}
</style>
