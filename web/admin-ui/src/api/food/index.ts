import request from "~/utils/request";

export const getAllFoods = () => request.get('/admin/food');
// 添加食物
export const addFood = (food: any) => request.post('/admin/food', food);