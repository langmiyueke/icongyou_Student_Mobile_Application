<template>
  <div class="page-courses">
    <div class="header">
        <h1>课程学习</h1>
        <div class="controls">
          <div class="search-row">
            <div class="search-box">
              <svg class="search-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="11" cy="11" r="6" stroke="#9CA3AF" stroke-width="1.5"/><path d="M20 20l-4-4" stroke="#9CA3AF" stroke-width="1.5" stroke-linecap="round"/></svg>
              <input ref="searchInput" v-model="query" @keyup.enter="onSearch" class="search" placeholder="搜索课程或教师" />
              <button v-if="query" class="clear-search" @click="clearSearch" aria-label="清除搜索">×</button>
            </div>
          </div>
          <div class="filters-row">
            <div class="filter-tabs">
              <button
                v-for="opt in statusOptions" :key="opt.value"
                :class="['filter-btn', { active: status === opt.value }]"
                @click="status = opt.value"
              >
                {{ opt.label }}
              </button>
            </div>
            <button class="cat-toggle" @click="showCatPopup = true">分类</button>
          </div>
        </div>
      </div>

    <div class="content">
      <aside class="side">
        <div class="side-title">分类筛选</div>
        <div class="categories">
          <label v-for="c in categories" :key="c.value" class="cat">
            <input type="checkbox" v-model="selectedCategories" :value="c.value" />
            <span class="pill">{{ c.label }}</span>
            <span class="count">{{ typeCounts[c.value] || 0 }}</span>
          </label>
        </div>
      </aside>

      <main class="list">
        <div class="list-grid">
          <CourseCard v-for="course in filteredCourses" :key="course.id" :course="course" @enter="enterCourse" @open="enterCourse" />
        </div>
        <div v-if="filteredCourses.length === 0" class="empty">没有符合条件的课程</div>
      </main>
    </div>
  </div>

  <van-popup v-model:show="showCatPopup" position="bottom" :style="{ height: '52%' }">
    <div class="popup-inner">
      <div class="popup-header">
        <div>
          <div class="title">选择分类</div>
          <div class="subtitle">按课程类型筛选，支持多选</div>
        </div>
        <div class="popup-controls">
          <button class="reset-btn" @click="resetCategories">重置</button>
          <button class="close-btn" @click="showCatPopup = false">×</button>
        </div>
      </div>
      <div class="popup-body">
          <div class="cat-grid">
            <label v-for="c in categories" :key="c.value" class="cat-mobile">
              <input type="checkbox" v-model="selectedCategories" :value="c.value" />
              <div class="cat-row">
                <span class="pill">{{ c.label }}</span>
                <span class="count">{{ typeCounts[c.value] || 0 }}</span>
              </div>
            </label>
          </div>
      </div>
      <div class="popup-actions">
        <van-button block type="primary" @click="showCatPopup = false">确定</van-button>
      </div>
    </div>
  </van-popup>

</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { showFailToast } from 'vant'
import { useRouter } from 'vue-router'
import CourseCard from '@/components/CourseCard.vue'
import { fetchCourses } from '@/api/courses'

export default {
  name: 'Courses',
  components: { CourseCard },
  setup () {
    const query = ref('')
    const status = ref('all')
    const statusOptions = [
      { label: '全部', value: 'all' },
      { label: '未开课', value: 1 },
      { label: '进行中', value: 2 },
      { label: '已完成', value: 3 }
    ]

    const categories = [
      { label: '实训课程', value: 1 },
      { label: '活动课程', value: 2 },
      { label: '必修课', value: 3 },
      { label: '选修课', value: 4 },
      { label: '公共基础课', value: 5 }
    ]
    const selectedCategories = ref([])
    const showCategories = ref(false)
    const showCatPopup = ref(false)
    const searchInput = ref(null)

    const courses = ref([])

    const teachersMap = ref({})
    onMounted(async () => {
      try {
        // TODO: 原本这里会调用后端 fetchCourses()
        // 当前 mock 实现已在 `src/api/courses.js` 中支持传参（{ status, query, types }），
        // 当需要切换到真实后端时，可直接调用 `await fetchCourses(params)` 并移除 mock 导入。
        const resp = await fetchCourses({ status: status.value, query: query.value, types: selectedCategories.value, page: 1, size: 1000 })
        // 兼容后端分页返回结构：{ items, total, page, size }
        courses.value = resp && resp.items ? resp.items : (Array.isArray(resp) ? resp : [])
      } catch (err) {
        // 更详细的调试信息：打印可能来自后端的响应体
        console.error('加载课程失败', err)
        console.error('response data:', err?.response?.data)
        showFailToast({ message: `加载课程失败：${err?.message || '服务器错误'}`, duration: 3000 })
      }
      // 注意：不预取教师列表以避免前端缓存。CourseCard 会按需调用 `getTeacher(id)` 获取教师信息，
      // 推荐后端在返回课程列表时直接聚合教师姓名以减少请求数。
    })

    const getTeacherName = (c) => {
      if (!c) return ''
      if (c.teacher) {
        if (typeof c.teacher === 'string') return c.teacher
        if (c.teacher.name) return c.teacher.name
      }
      if (c.teacher_id && teachersMap.value[String(c.teacher_id)]) return teachersMap.value[String(c.teacher_id)]
      return ''
    }

    const filteredCourses = computed(() => {
      return courses.value.filter(c => {
        if (status.value !== 'all' && String(c.status) !== String(status.value)) return false
        // 根据 course.type 进行过滤（匹配上面 categories 的 value）
        if (selectedCategories.value.length && !selectedCategories.value.includes(Number(c.type))) return false
        if (query.value) {
          const q = query.value.toLowerCase()
          const matchesName = String(c.name || '').toLowerCase().includes(q)
          const matchesTeacher = String(getTeacherName(c) || '').toLowerCase().includes(q)
          const matchesCode = String(c.code || '').toLowerCase().includes(q)
          const matchesTags = Array.isArray(c.tags) && c.tags.some(t => String(t).toLowerCase().includes(q))
          if (!matchesName && !matchesTeacher && !matchesCode && !matchesTags) return false
        }
        return true
      })
    })

    // 计算在当前状态/搜索条件下，每种 type 的候选数量（不考虑已选的 type 筛选），用于在分类旁显示徽章
    const typeCounts = computed(() => {
      const counts = {}
      const candidate = courses.value.filter(c => {
        if (status.value !== 'all' && String(c.status) !== String(status.value)) return false
        if (query.value) {
          const q = query.value.toLowerCase()
          const matchesName = String(c.name || '').toLowerCase().includes(q)
          const matchesTeacher = String(getTeacherName(c) || '').toLowerCase().includes(q)
          const matchesCode = String(c.code || '').toLowerCase().includes(q)
          const matchesTags = Array.isArray(c.tags) && c.tags.some(t => String(t).toLowerCase().includes(q))
          if (!matchesName && !matchesTeacher && !matchesCode && !matchesTags) return false
        }
        return true
      })
      candidate.forEach(c => {
        const t = Number(c.type) || 0
        counts[t] = (counts[t] || 0) + 1
      })
      return counts
    })

    const router = useRouter()
    const enterCourse = (course) => {
      router.push({ name: 'course', params: { id: course.id } })
    }

    const onSearch = () => {
      try { searchInput.value && searchInput.value.blur() } catch (e) {}
    }

    const clearSearch = () => {
      query.value = ''
      try { searchInput.value && searchInput.value.focus() } catch (e) {}
    }

    const resetCategories = () => { selectedCategories.value = [] }

    return { query, status, statusOptions, categories, selectedCategories, filteredCourses, enterCourse, showCategories, showCatPopup, typeCounts, searchInput, onSearch, clearSearch, resetCategories }
  }
}
</script>

<style scoped>
.page-courses { padding: 12px }
.header { display:flex; flex-direction:column; gap:8px; margin-bottom:10px }
.header h1 { margin:0; font-size:20px }
.controls { display:flex; gap:8px; align-items:center; width:100%; flex-direction:column }
.search { padding:8px 10px; border-radius:8px; border:1px solid #e6eefc; flex:1; min-width:0 }
.search-row { width:100% }
.search-box { display:flex; align-items:center; gap:8px; background: #fff; border-radius:12px; padding:8px; box-shadow: 0 6px 18px rgba(23,94,122,0.06); border: 1px solid rgba(23,94,122,0.06) }
.search-box .search-icon { flex:0 0 18px; margin-left:4px }
.search-box .search { border: none; padding:6px 4px; outline: none; font-size:14px }
.search-box .search::placeholder { color:#9CA3AF }
.clear-search { background:transparent; border:none; font-size:18px; color:#9CA3AF; padding:6px; cursor:pointer }
.filters-row { display:flex; gap:8px; align-items:center; width:100% }
.filter-tabs { display:flex; gap:8px; overflow-x:auto; padding:6px 0; flex:1 }
.filter-tabs::-webkit-scrollbar { display:none }
.filter-btn { padding:6px 10px; border:1px solid #e6eefc; background:#fff; border-radius:18px; cursor:pointer; white-space:nowrap; font-size:13px }
.filter-btn.active { background:#409EFF; color:white; border-color:transparent }
.cat-toggle { background:#fff; border:1px solid #e6eefc; padding:6px 10px; border-radius:8px; margin-left:6px }
.mobile-cats { display:flex; gap:10px; flex-wrap:wrap; padding:8px 0 }
.cat-mobile { display:flex; gap:6px; align-items:center; font-size:13px; background:#fff; padding:6px 8px; border-radius:8px; border:1px solid #f0f4f9 }
.cat-mobile .count { background:#eef6fb; color:#175e7a; font-size:12px; padding:2px 6px; border-radius:10px; margin-left:8px }
.content { display:flex; gap:12px }
.side { width:160px; background:#fff; border-radius:8px; padding:12px; box-shadow:0 2px 10px rgba(0,0,0,0.04) }
.side-title { font-weight:600; margin-bottom:8px }
.categories { display:flex; flex-direction:column; gap:8px }
.cat { display:flex; gap:8px; align-items:center; font-size:14px }
.cat .count { background:#eef6fb; color:#175e7a; font-size:12px; padding:2px 6px; border-radius:10px; margin-left:8px }
.list { flex:1 }
.list-grid { display:flex; flex-direction:column; gap:10px }
.empty { color:#9CA3AF; padding:16px; text-align:center }

/* Popup / category drawer styles */
.popup-inner { padding:12px }
.popup-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:12px }
.popup-header .title { font-weight:700; font-size:16px; color:#0f3740 }
.popup-header .subtitle { font-size:12px; color:#6b7280; margin-top:4px }
.popup-controls { display:flex; gap:8px; align-items:center }
.reset-btn { background:#fff; border:1px solid #e6eefc; padding:6px 10px; border-radius:8px; color:#175e7a }
.close-btn { background:transparent; border:none; font-size:18px; line-height:1; color:#6b7280 }
.popup-body { padding:8px 0 }
.cat-grid { display:grid; grid-template-columns: repeat(2, 1fr); gap:10px }
.cat-mobile { display:block }
.cat-mobile input { display:none }
.cat-mobile .cat-row { display:flex; align-items:center; justify-content:space-between; gap:8px; padding:10px 12px; border-radius:12px; background:#fff; border:1px solid #f1f5f9; box-shadow: 0 6px 14px rgba(15,55,64,0.03); transition: all 160ms ease }
.cat-mobile .pill { display:inline-block; padding:6px 10px; border-radius:10px; background:transparent; color:#175e7a; font-weight:600; font-size:14px }
.cat-mobile .count { background:#eef6fb; color:#175e7a; font-size:12px; padding:4px 8px; border-radius:10px }
.cat-mobile input:checked ~ .cat-row { background:#175e7a; }
.cat-mobile input:checked ~ .cat-row .pill { color:#fff }
.cat-mobile input:checked ~ .cat-row .count { background: rgba(255,255,255,0.18); color: #fff }

@media (min-width: 700px) {
  .cat-grid { grid-template-columns: repeat(3, 1fr) }
}
.popup-actions { margin-top:14px }

@media (max-width: 800px) {
  .content { flex-direction:column }
  .side { display:none }
  .controls { align-items:stretch }
  .filters-row { justify-content:space-between }
}
</style>
