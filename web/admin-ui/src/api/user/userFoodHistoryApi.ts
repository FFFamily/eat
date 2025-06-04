import request from "~/utils/request";


// 获取历史记录
export const getEatHistoryList = () => request.get(`/user/food/history/list`);
// 条件查询历史记录
export const getQueryEatHistoryList = (date: any) => request.get(`/user/food/history/list/date`,  {
    params: {
        date: date
    }
} );