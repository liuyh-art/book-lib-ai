import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
    baseURL: '/api',
    timeout: 30000
})

// 请求拦截器 - 自动携带token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器 - 统一处理错误
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            if (res.code === 401 || res.code === 4001 || res.code === 4002) {
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')
                router.push('/login')
            }
            return Promise.reject(new Error(res.message))
        }
        return res
    },
    error => {
        if (error.response) {
            const status = error.response.status
            if (status === 401) {
                ElMessage.error('未登录或token已过期')
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')
                router.push('/login')
            } else if (status === 403) {
                ElMessage.error('无权限访问')
            } else if (status === 500) {
                ElMessage.error('服务器异常')
            } else {
                ElMessage.error(error.message || '网络错误')
            }
        } else {
            ElMessage.error('网络连接失败')
        }
        return Promise.reject(error)
    }
)

export default request
