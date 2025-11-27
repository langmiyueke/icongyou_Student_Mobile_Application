// src/api/index.js
import request from "../utils/request/"
import axios from 'axios'
import { showFailToast } from 'vant'
import router from '@/router'

// 使用 Vue CLI 环境变量：VUE_APP_API_BASE_URL
const BASE_URL = process.env.VUE_APP_API_BASE_URL || '/api'

const instance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const LoadTaskList = () =>{
  return request.get("/task/loadTaskList")
}

export const loadDataView = () =>{
   return request.get("/task/loadDataView")
}

export const loadHeatData = () =>{
  return request.get("/task/loadHeatData")
}

export const newTask = (data) =>{
  return request.post("/task/newTask",data)
}

export const loadDetailTask = (taskId) =>{
  return request.get("/task/loadDetailTask",
    {params: {id: taskId}}
  )
}

export const loadTeamMembers = (taskId) =>{
  return request.get("/task/loadTeamMembers",
    {params: {id: taskId}}
  )
}

export const loadTeamProcess = (taskId) =>{
  return request.get("/task/loadTeamProcess",
    {params: {id: taskId}}
  )
}







// 请求拦截器：自动注入 token
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器：unwrap data，统一处理 401/错误提示
// 严格处理后端 ActionResult 规范：{ code, message, data }
instance.interceptors.response.use(res => {
  // 若后端不包装 ActionResult 则直接返回 res.data
  const payload = res.data
    if (payload && typeof payload === 'object' && 'code' in payload) {
    const { code, message, data } = payload
    if (code === 0) {
      return data
    }
    // 业务错误：展示消息并抛出带 code 的 Error
    try { showFailToast({ message: message || `错误：${code}`, duration: 2000 }) } catch (e) { /* ignore */ }
    const err = new Error(message || '业务错误')
    err.code = code
    err.payload = payload
    return Promise.reject(err)
  }
  // 回退：未按 ActionResult 包装，返回原始数据
  return payload === undefined ? res : payload
}, error => {
  const status = error.response?.status
  const respData = error.response?.data
  if (status === 401) {
    // 清理登录态并通过 router 跳转到登录页，保留重定向信息
    try { localStorage.removeItem('token') } catch (e) { /* ignore */ }
    const current = router && router.currentRoute && router.currentRoute.value && router.currentRoute.value.fullPath
    const redirect = current || window.location.pathname || '/'
    try {
      router.replace({ path: '/login', query: { redirect } })
    } catch (e) {
      // fallback to full reload when router not available
      window.location.href = '/login'
    }
    return Promise.reject(new Error('Unauthorized'))
  }
  // 非业务错误或网络错误：显示统一提示
  const msg = respData?.message || error.message || '请求出错'
  try { showFailToast({ message: msg, duration: 2000 }) } catch (e) { /* ignore */ }
  console.error('API Error:', respData || error.message)
  return Promise.reject(error)
})

// 辅助方法（可按需导出）
export function setAuthToken(token) {
  if (token) {
    localStorage.setItem('token', token)
    instance.defaults.headers.Authorization = `Bearer ${token}`
  } else {
    localStorage.removeItem('token')
    delete instance.defaults.headers.Authorization
  }
}

export default instance