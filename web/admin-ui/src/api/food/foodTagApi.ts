import request from "~/utils/request";
const FOOD_TAG_API = "/user/foodTag";
// 获取标签列表
export const getAllFoodTagList = () => request.get(
    FOOD_TAG_API + "/all"
)

// 添加标签
export const createFoodTag = (data: any) => request.post(
    FOOD_TAG_API + "/create",
    data
)

// 删除标签
export const deleteFoodTag = (id: any) => request.delete(
    FOOD_TAG_API + `/del/${id}`
)

// 更新标签
export const updateFoodTag = (data: any) => request.post(
    FOOD_TAG_API + "/addOrUpdate",
    data
)

// 为食物添加标签
export const addFoodTag = (data: any) => request.post(
    FOOD_TAG_API + "/addFoodTag",
    data
)

// 为食物移除标签
export const removeFoodTag = (data: any) => request.post(
    FOOD_TAG_API + "/removeFoodTag",
    data
)