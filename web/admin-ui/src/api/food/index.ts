import request from "~/utils/request";

export const getAllFoods = () => request.get('/user/food');
// 添加食物
export const addFood = (food: any) => request.post('/user/food', food);
// 删除食物
export const deleteFood = (id: number) => request.delete(`/user/food/${id}`);