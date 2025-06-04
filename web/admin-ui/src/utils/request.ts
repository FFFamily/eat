import { useUserStore } from '~/store/modules/user';
import axios from 'axios'
import { ElMessageBox, ElMessage } from 'element-plus';

// 创建 axios
const request = axios.create({
    // 请求地址
    // baseURL: 'http://127.0.0.1:8081',
    baseURL: import.meta.env.VITE_APP_BASE_API as string,
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
            config.headers['Token-Key'] = store.token
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
           
            if (res.code === 401) {
                ElMessageBox.confirm('您已注销，可以取消以留在此页面，或重新登录', '确认注销', {
                    confirmButtonText: '重新登录',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const store = useUserStore();
                    store.userLogout();
                })

            }else if(res.code === 500){
                ElMessage({
                    message: msg || 'Error',
                    type: 'error',
                    duration: 10 * 1000
                })
            }
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        console.log(error.status)
        if (error.status === 400) {
            ElMessage({
                message: '请求参数错误',
                type: 'error'
            })
        }else{
            ElMessage({
                message: error.message,
                type: 'error'
            })
        }
        
        return Promise.reject(error)
    }
)

export default request;