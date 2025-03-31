import request from "~/utils/request";
// 获取食物列表
export const getFoodTypeList = (data: any) => request.post(
    "/admin/foodType/list",
    data
)

// 添加食物
export const addFoodType = (data: any) => request.post(
    "/admin/foodType/addOrUpdate",
    data
)

// 删除食物
export const delFoodType = (id: any) => request.delete(
    `/admin/foodType/del/${id}`
)