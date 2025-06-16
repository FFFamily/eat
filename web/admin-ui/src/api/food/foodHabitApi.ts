import request from '~/utils/request';

// 定义饮食习惯接口类型
export interface FoodHabit {
  id: number;
  name: string;
  tagIds: number[];
  tags?: { id: number; name: string }[];
}

// 获取所有饮食习惯
export const getAllFoodHabits = () => {
  return request<{ data: FoodHabit[] }>({
    url: '/food/habit/all',
    method: 'get',
  });
};

// 创建饮食习惯
export const createFoodHabit = (data: Omit<FoodHabit, 'id' | 'tags'>) => {
  return request({
    url: '/food/habit',
    method: 'post',
    data,
  });
};

// 更新饮食习惯
export const updateFoodHabit = (id: number, data: Omit<FoodHabit, 'id' | 'tags'>) => {
  return request({
    url: `/food/habit/${id}`,
    method: 'put',
    data,
  });
};

// 删除饮食习惯
export const deleteFoodHabit = (id: number) => {
  return request({
    url: `/food/habit/${id}`,
    method: 'delete',
  });
};

// 获取所有食物标签（复用）
export { getAllFoodTagList } from './foodTagApi';