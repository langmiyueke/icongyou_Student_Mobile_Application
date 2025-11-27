// src/api/auth/index.js
// NOTE: 目前将后端调用注释并使用 mock 返回，用于本地开发离线调试。
// TODO: 当需要联调后端时，恢复下面被注释的真实请求并移除 mock 实现。

import { mockLogin, mockRegister } from '@/mocks/auth'

export function login (payload) {
  // TODO: 调用后端接口 — 确认 `/api/auth/login` 返回 ActionResult.data 包含 token 与 user
  // 新约定：前端使用姓名 + 手机号 + 密码 登录，payload 示例： { user_name, phone, pwd }
  // const mapped = { user_name: payload.user_name, phone: payload.phone, pwd: payload.pwd }
  // return api.post('/auth/login', mapped)

  // mock 返回，使用共享 mocks（保持向后兼容）
  return Promise.resolve(mockLogin(payload))
}

export function register (payload) {
  // TODO: 调用后端接口 — 确认 `/api/auth/register` 的参数校验与错误码
  // 新约定：前端发送 { user_name, nick_name, phone, pwd, user_type }
  // 后端示例映射：
  // 1) 根据 payload.tenant_name（或 payload.school）在租户表（Tenant）中查找对应 tenant_id；
  //    若找到则在创建 User 时把 tenant_id 写入 User.tenant_id 字段；若未找到则按后端策略创建或返回错误。
  // 2) 在 User 表插入 (user_name, pwd, nick_name, phone, tenant_id)
  // 3) 若 payload.user_type === 1 创建 Student 表记录并保存 school/其他信息
  // const mapped = { user_name: payload.user_name, pwd: payload.pwd, nick_name: payload.nick_name, phone: payload.phone, user_type: payload.user_type }
  // return api.post('/auth/register', mapped)

  return Promise.resolve(mockRegister(payload)) // 保持向后兼容
}

export default { login, register }
