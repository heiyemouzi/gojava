import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

request.interceptors.request.use(cfg => {
  const userStore = useUserStore()
  if (userStore.token) {
    cfg.headers.Authorization = 'Bearer ' + userStore.token
  }
  return cfg
})

request.interceptors.response.use(
  resp => {
    const body = resp.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) return body.data
      ElMessage.error(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg))
    }
    return body
  },
  err => {
    if (err.response?.status === 401) {
      ElMessage.error('未登录或登录已过期')
      useUserStore().logout()
      location.hash = '#/login'
    } else {
      ElMessage.error(err.message)
    }
    return Promise.reject(err)
  }
)

export default request
