import request from "~/utils/request";
import type {loginResponse,loginForm} from "./type"


// 获取推荐食物
export  const getRecommendFood = (data:any) => request.post<any,loginResponse>("/user/food/recommendFood", data);
