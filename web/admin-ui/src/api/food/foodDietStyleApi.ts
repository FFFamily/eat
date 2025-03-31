import request from "~/utils/request";
// 获取饮食方式
export const getAllFoodDietStyleList = () => request.get(
    "/admin/dietStyle/all"
)
// 添加饮食方式
export const addFoodDietStyle = (data:any) => request.post(
    "/admin/dietStyle/add",
    data
)

// 添加饮食方式
export const delFoodDietStyle = (id:string) => request.delete(
    `/admin/dietStyle/delete/${id}`
)

// 添加饮食方式
export const updateFoodDietStyle = (data:any) => request.put(
    `/admin/dietStyle/update`,
    data
)
