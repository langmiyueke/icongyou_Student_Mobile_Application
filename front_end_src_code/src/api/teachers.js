import { mockTeachers } from '@/mocks/teachers'
// import api from './index'

// NOTE: `GET /teachers` 列表接口已在仓库中移除（deprecated）。
// 建议前端依赖下列两种方式之一：
// 1) `/courses` 接口在返回课程对象时聚合 `teacher: { id, name }`，前端直接使用该字段。
// 2) 按需调用 `GET /teachers/{id}`（保留），获取单个教师信息。
// 因此此文件不再导出 `fetchTeachers()` 列表方法，以避免在 UI 中误用全量拉取并缓存。

export function getTeacher (id) {
  if (id === undefined || id === null) return Promise.resolve(null)
  // 真实请求示例（联调时可启用）：
  // return api.get(`/teachers/${id}`)

  const found = mockTeachers.find(t => String(t.id) === String(id) || t.id === id)
  return Promise.resolve(found || null)
}

export async function getTeacherNameCached (id) {
  if (id === undefined || id === null) return ''
  try {
    const t = await getTeacher(id)
    return t?.name || ''
  } catch (e) {
    return ''
  }
}
