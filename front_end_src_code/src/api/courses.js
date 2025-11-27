import { mockCourses } from '@/mocks/courses'
// import api from './index'

// TODO: 调用后端接口 — 确认 `/api/courses` 返回的 ActionResult.data 结构及错误码处理
// 当需要联调后端时，取消下面的 mock 实现并恢复为 `return api.get('/courses')`
export function fetchCourses (params = {}) {
  // 支持 params: { page, size, status, query, types }
  // 原始真实请求（保留为注释）：
  // return api.get('/courses', { params })

  // 在 mock 情况下，根据 params 做简单过滤以模拟后端行为
  const { status, query, types } = params || {}
  let list = mockCourses.slice()
  if (status && status !== 'all') list = list.filter(c => String(c.status) === String(status))
  if (Array.isArray(types) && types.length) list = list.filter(c => types.includes(Number(c.type)))
  if (query) {
    const q = String(query).toLowerCase()
    list = list.filter(c => {
      const name = String(c.name || '').toLowerCase()
      const code = String(c.code || '').toLowerCase()
      const tags = (c.tags || []).map(t => String(t).toLowerCase()).join(' ')
      return name.includes(q) || code.includes(q) || tags.includes(q)
    })
  }
  // 支持分页：若提供 page/size，则返回对应页的数据；否则返回全部作为单页
  const page = Number(params.page) || 1
  const size = Number(params.size) || list.length
  const total = list.length
  const start = (page - 1) * size
  const items = list.slice(start, start + size)
  return Promise.resolve({ items, total, page, size })
}

export function getCourse (id) {
  // 原始真实请求（保留为注释）：
  // return api.get(`/courses/${id}`)
  const found = mockCourses.find(c => String(c.id) === String(id) || c.id === id)
  if (!found) return Promise.resolve(null)
  // 将返回严格映射到 Course 表字段，避免测试/联调时多余字段导致歧义
  const course = {
    id: found.id,
    name: found.name,
    status: found.status,
    duration: found.duration,
    teacher_id: found.teacher_id,
    semester_id: found.semester_id,
    create_time: found.create_time,
    task_number: found.task_number,
    student_number: found.student_number,
    type: found.type,
    start_time: found.start_time,
    end_time: found.end_time,
    image: found.image,
    code: found.code
  }
  // 可选扩展：如果 mock 中包含 description 或 tags，保留为非必须字段
  if (found.description) course.description = found.description
  if (found.tags) course.tags = found.tags
  if (found.teacher) course.teacher = found.teacher
  return Promise.resolve(course)
}
 
