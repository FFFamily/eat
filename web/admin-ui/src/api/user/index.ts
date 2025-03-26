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
export const logout = () => request.get(API.LOOUT_URL);
// 以下为admin的接口
// 获取用户信息
export const findUserInfo = (id: number) => request.get(`/admin/user/info/${id}`);
// 删除用户
export const removeUser = (id: number) => request.delete(`/admin/user/delete/${id}`);
// 新增用户
export const createUser = (data: any) => request.post("/admin/user/add", data);
// 修改用户
export const updateUser = (data: any) => request.put("/admin/user/update", data);
// 获取用户列表信息
export const findUserList = () => request.get("/admin/user/list");