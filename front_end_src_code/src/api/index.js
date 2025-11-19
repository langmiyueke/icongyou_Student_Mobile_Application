// src/api/index.js
import axios from 'axios'

const instance = axios.create({
  baseURL: '/api', // 代理前缀自动匹配vue.config.js配置
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer your_token' // 可选全局认证头
  }
})

// 请求拦截器
instance.interceptors.request.use(config => {
  // 添加token等全局参数
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器
instance.interceptors.response.use(response => {
  return response.data
}, error => {
  // 统一错误处理
  console.error('API Error:', error.response?.data || error.message)
  return Promise.reject(error)
})

export default instance