// import api from './index'
import { mockProgressMap } from '@/mocks/progress'

export function fetchUserCourseProgress (courseId) {
  // TODO: 调用后端接口 — 确认 `/api/courses/{id}/progress` 返回的 PersonalCourseProgress 字段及鉴权要求
  // 真实请求示例（取消注释以联调后端）：
  // return api.get(`/courses/${courseId}/progress`)

  // 返回 mock 数据以支持离线开发：
  // 将 mock 中常见的 camelCase/other keys 规范映射到 DB 对应字段名（snake_case）
  const raw = mockProgressMap[courseId] || {}
  const progress = {
    id: raw.id || null,
    student_id: raw.student_id || raw.studentId || null,
    course_id: raw.course_id || courseId,
    total_score: raw.total_score || raw.totalScore || raw.total || null,
    total_rank: raw.total_rank || raw.totalRank || null,
    dimension_progress: raw.dimension_progress || raw.dimensionProgress || raw.dimensions || {},
    completed_tasks: raw.completed_tasks || raw.completedTasks || [],
    pending_tasks: raw.pending_tasks || raw.pendingTasks || [],
    last_active: raw.last_active || raw.lastActive || null
  }
  return Promise.resolve(progress)
}
