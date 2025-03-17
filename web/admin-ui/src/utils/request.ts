import { useUserStore } from '~/store/modules/user';
import axios from 'axios'
import { ElMessageBox, ElMessage } from 'element-plus';

// 创建 axios
const request = axios.create({
    // 请求地址
    baseURL: 'http://127.0.0.1:8081',
    // baseURL: import.meta.env.VITE_APP_BASE_API as String,
    // baseURL: "/dev-api",
    // withCredentials: true,
    // 超时时间
    timeout: 5000,
    withCredentials: true
})

// request interceptor
request.interceptors.request.use(
    config => {
        const store = useUserStore();
        if (store.token) {
            config.headers['Token'] = store.token
        }
        return config
    },
    error => {
        console.log(error);
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        const msg = res.msg;
        if (res.code !== 200) {
            ElMessage({
                message: msg || 'Error',
                type: 'error',
                duration: 5 * 1000
            })

            // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
            // if (res.code === 5008 || res.code === 5012 || res.code === 5014) {
            //     // to re-login
            //     ElMessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
            //         confirmButtonText: 'Re-Login',
            //         cancelButtonText: 'Cancel',
            //         type: 'warning'
            //     }).then(() => {
            //         store.dispatch('user/resetToken').then(() => {
            //             location.reload()
            //         })
            //     })
            // }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        console.log('错误' + error)
        ElMessage({
            message: error.message,
            type: 'error'
        })
        return Promise.reject(error)
    }
)

export default request;