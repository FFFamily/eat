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