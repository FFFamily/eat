import { defineStore } from 'pinia';
import { login,getLoginInfo,logout } from '~/api/user';
import {LoginData} from "~/api/user/type";

// 定义常量
const TOKEN_KEY = 'TOKEN';
const USER_KEY = 'USER_INFO';
// 用户相关
export const useUserStore = defineStore('User', {
  state: () => ({
    // 用户标识
    token: localStorage.getItem(TOKEN_KEY),
  }),
  actions: {
    async userLogin(data: LoginData) {
      try {
        console.log(data);
        const result = await login(data);
        if (result.code === 200) {
          this.token = result.data;
          // 持久化
          localStorage.setItem(TOKEN_KEY, this.token);
          // 获取用户信息
          const userInfo = await getLoginInfo();
          // 持久化
          localStorage.setItem(USER_KEY,userInfo.data);
        } else {
          return Promise.reject(result.msg);
        }
      } catch (error) {
        return Promise.reject('登录请求失败');
      }
    },
    async userLogout() {
      logout().then((res:any) => {
        if (res.code !== 200) {
          return Promise.reject(res.msg);
        }
        this.token = null;
        // 清除持久化
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
      });
    },
  },
  getters: {},
});