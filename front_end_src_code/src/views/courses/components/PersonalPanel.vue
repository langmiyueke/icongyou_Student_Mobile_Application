<template>
  <div class="personal-panel plain-panel">
    <div class="panel-card-plain">
        <header class="panel-header">
          <div>
            <h3 class="panel-title">个人数据</h3>
            <div class="panel-sub">{{ data.student_name || '—' }}</div>
          </div>
        </header>

        <div v-if="loading" class="loading-overlay">加载中…</div>
        <section class="panel-body" :aria-busy="loading">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-num">{{ completedCount }}</div>
              <div class="stat-label">已完成任务数</div>
            </div>
            <div class="stat-card">
              <div class="stat-num">{{ pendingCount }}</div>
              <div class="stat-label">待完成任务数</div>
            </div>
            <div class="stat-card">
              <div class="stat-num">{{ data.total_score ?? '—' }}</div>
              <div class="stat-label">总分</div>
            </div>
            <div class="stat-card">
              <div class="stat-num">{{ data.total_rank ?? '—' }}</div>
              <div class="stat-label">总排名</div>
            </div>
          </div>

          <div class="dimensions">
            <div class="dim-title">能力维度</div>
            <div class="dim-pie-wrap">
              <div v-if="!dimensionKeys.length" class="dim-empty">— 暂无维度数据</div>
              <div v-else class="pies-grid">
                <div v-for="entry in dimensionEntries" :key="entry.key" class="mini-pie-card">
                  <svg class="mini-pie" :viewBox="`0 0 ${miniSize} ${miniSize}`" :width="miniSize" :height="miniSize" role="img" :aria-label="`维度 ${entry.key} 饼图`">
                    <g :transform="`translate(${miniCenter}, ${miniCenter})`">
                      <circle class="pie-bg" :r="miniRadius" fill="none" :stroke-width="miniStroke" stroke="#eef6ff" />
                      <circle class="pie-fg" :r="miniRadius" fill="none" :stroke-width="miniStroke" :stroke="getColor(entry.key)"
                        :stroke-dasharray="(miniCircumference * (entry.value/100)) + ' ' + miniCircumference"
                        :transform="`rotate(-90)`" stroke-linecap="round" />
                      <text x="0" y="4" text-anchor="middle" font-size="12" fill="#0f1724">{{ entry.value + '%' }}</text>
                    </g>
                  </svg>
                  <div class="mini-label">{{ entry.key }}</div>
                
                </div>
              </div>
            </div>
          </div>

          <!-- debug JSON removed from UI (kept in code as debugJSON variable) -->
        </section>
      </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { fetchUserCourseProgress } from '@/api/progress'

export default {
  name: 'PersonalPanel',
  props: {
    courseId: { type: [String, Number], required: true }
  },
  setup (props) {
    const router = useRouter()
    const data = ref({
      id: null,
      student_id: null,
      course_id: null,
      total_score: null,
      total_rank: null,
      dimension_progress: {},
      completed_tasks: [],
      pending_tasks: []
    })
    const loading = ref(false)

    const load = async () => {
      loading.value = true
      try {
        // 调用统一的 progress API（当前为 mock 实现）
        const res = await fetchUserCourseProgress(props.courseId)
        console.debug('[PersonalPanel] progress fetch', res)
        const merged = Object.assign({}, data.value, res)
        try { if (typeof merged.dimension_progress === 'string') merged.dimension_progress = JSON.parse(merged.dimension_progress) } catch (e) { merged.dimension_progress = {} }
        try { if (typeof merged.completed_tasks === 'string') merged.completed_tasks = JSON.parse(merged.completed_tasks) } catch (e) { merged.completed_tasks = [] }
        try { if (typeof merged.pending_tasks === 'string') merged.pending_tasks = JSON.parse(merged.pending_tasks) } catch (e) { merged.pending_tasks = [] }

        data.value = merged
      } catch (e) {
        console.warn('fetch progress failed', e)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => { load() })

    const onContinue = () => {
      router.push({ name: 'course', params: { id: props.courseId }, query: { jumpTo: 'last' } })
    }

    const completedCount = computed(() => (data.value.completed_tasks || []).length)
    const pendingCount = computed(() => (data.value.pending_tasks || []).length)
    const dimensionKeys = computed(() => Object.keys(data.value.dimension_progress || {}))
    const debugJSON = computed(() => JSON.stringify(data.value, null, 2))

    const colors = ['#1677ff', '#60a5fa', '#34d399', '#f59e0b', '#f97316', '#ef4444', '#a78bfa', '#f472b6']
    const miniSize = 80
    const miniCenter = miniSize / 2
    const miniRadius = 26
    const miniStroke = 10
    const miniCircumference = 2 * Math.PI * miniRadius

    const dimensionEntries = computed(() => {
      const dims = data.value.dimension_progress || {}
      return Object.keys(dims).map(k => {
        let raw = Math.round(Number(dims[k]) || 0)
        if (raw < 0) raw = 0
        if (raw > 100) raw = 100
        return { key: k, value: raw }
      })
    })

    const getColor = (key) => {
      const hash = Array.from(key).reduce((s, c) => s + c.charCodeAt(0), 0)
      return colors[hash % colors.length]
    }

    return { data, loading, onContinue, completedCount, pendingCount, dimensionKeys, debugJSON,
      dimensionEntries, miniSize, miniCenter, miniRadius, miniStroke, miniCircumference, getColor }
  }
}
</script>

<style scoped>
.personal-panel { margin-bottom: 12px }
.panel-card { padding: 10px }
.top-row { display:flex; gap:12px; align-items:center }
.stats { display:flex; gap:10px; align-items:center }
.stat { display:flex; flex-direction:column; align-items:flex-start }
.num { font-weight:700 }
.label { font-size:12px; color:#6b7280 }
.meta-row { display:flex; justify-content:space-between; align-items:center; margin-top:10px }
.actions { display:flex; gap:8px }
@media (max-width:720px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr) }
}
.stats-grid { display:grid; grid-template-columns: repeat(2, 1fr); gap:12px }
.stat-card { background:#f8fbfd; padding:12px; border-radius:8px; text-align:left; min-width:0 }

.panel-card-plain { background: #fff; padding: 14px; border-radius: 10px; box-shadow: 0 6px 18px rgba(0,0,0,0.04) }
.panel-header { display:flex; justify-content:space-between; align-items:center; gap:12px; margin-bottom:12px }
.panel-title { margin:0; font-size:18px }
.panel-sub { font-size:12px; color:#6b7280 }
.header-actions { display:flex; align-items:center }
.loading-overlay { padding:18px; color:#6b7280 }
.panel-body { display:flex; flex-direction:column; gap:12px }
.stat-num { font-weight:700; font-size:18px }
.stat-label { font-size:12px; color:#6b7280; margin-top:6px }
.detail-col { display:flex; flex-direction:column; gap:6px }
.detail-item { font-size:14px }
.detail-item .k { color:#6b7280; margin-right:8px }
.dimensions { margin-top:6px }
.dim-title { font-size:14px; color:#0f1724; margin-bottom:8px }
.dim-row { display:flex; align-items:center; gap:10px; margin-bottom:8px }
.dim-label { width:72px; color:#334155 }
.dim-bar { flex:1; background:#eef6ff; height:10px; border-radius:6px; overflow:hidden }
.dim-fill { height:100%; background:linear-gradient(90deg,#1677ff,#60a5fa); width:6% }
.dim-val { width:48px; text-align:right; font-size:13px }
.dim-empty { color:#9ca3af }
.debug-json { background:#0b1220; color:#d1f5ff; padding:10px; border-radius:6px; overflow:auto; max-height:280px }
.pies-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap:16px; align-items:start; justify-items:center }
.mini-pie-card { width:100%; max-width:160px; display:flex; flex-direction:column; align-items:center; gap:8px; padding:8px }
.mini-pie { width:80px; height:80px }
.mini-pie .pie-bg { stroke-width:10 }
.mini-pie .pie-fg { stroke-width:10; transform-origin: 0 0 }
.mini-label { font-size:13px; color:#0f1724 }
</style>
