import request from "~/utils/request";
import type {loginResponse,loginForm} from "./type"
enum API {
    LOGIN_URL = "/admin/auth/login",
    GET_USER = "/get_user",
}

// 登录
export  const login = (data:loginForm) => request.post<any,loginResponse>(API.LOGIN_URL, data);

// 获取用户信息
export  const getLoginInfo = (data:any) => request.get(API.GET_USER, { data });