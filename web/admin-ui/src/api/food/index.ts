import request from "~/utils/request";

export const getAllFoods = (data:any) => request.get('/food');