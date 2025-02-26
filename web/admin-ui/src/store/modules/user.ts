// 用户相关的小仓库
import  {defineStore} from 'pinia'
import {login} from "~/api/user"
import type {loginForm} from "~/api/user/type"
// 创凯
let ussUserStore = defineStore('User',{
    // 存数据
    state: () => {
        return {
            // 用户标识
            token: localStorage.getItem('TOKEN'),
        }
    },
    // 动作
    actions:{
        async userLogin(data:any){
            console.log(data)
            const result = await login(data)
            if (result.code == 200){
                this.token = result.data
                // 持久化
                localStorage.setItem("TOKEN",this.token)
            }else {
                return Promise.reject(result.msg)
            }

        }
    },
    // 获取
    getters:{

    }
})
export default ussUserStore;