import { defineStore } from 'pinia';
import { login,getLoginInfo,logout } from '~/api/user';

// 定义常量
const TOKEN_KEY = 'TOKEN';
const USER_KEY = 'USER_INFO';

// 定义登录数据类型
interface LoginData {
  // 这里根据实际情况定义登录数据的字段
  username: string;
  password: string;
}

// 定义登录响应类型
interface LoginResponse {
  code: number;
  data: string;
  msg: string;
}

export const useUserStore = defineStore('User', {
  state: () => ({
    // 用户标识
    token: localStorage.getItem(TOKEN_KEY),
  }),
  actions: {
    async userLogin(data: LoginData) {
      try {
        console.log(data);
        const result:LoginResponse = await login(data);
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