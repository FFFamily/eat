import request from "~/utils/request";


// 获取历史记录
export const eatHistoryList = () => request.get(`/user/food/history/list`);