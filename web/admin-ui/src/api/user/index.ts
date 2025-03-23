import request from "~/utils/request";
import type {LoginResponse,loginForm} from "./type"
enum API {
    LOGIN_URL = "/auth/login",
    GET_USER = "/auth/getLoginInfo",
    LOOUT_URL = "/auth/logout"
}

// 登录
export  const login = (data:loginForm) => request.post<any,LoginResponse>(API.LOGIN_URL, data);

// 获取用户信息
export  const getLoginInfo = () => request.get(API.GET_USER);
// 退出登录
export const logout = () => request.post(API.LOOUT_URL);