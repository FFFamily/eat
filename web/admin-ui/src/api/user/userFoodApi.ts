import request from "~/utils/request";
import type {LoginResponse,loginForm} from "./type"


// 获取推荐食物
export  const getRecommendFood = (data:any) => request.post<any,LoginResponse>("/user/food/recommendFood", data);
// 吃
export const eatFood = (data: any) => request.post(`/user/food/eat`,data);