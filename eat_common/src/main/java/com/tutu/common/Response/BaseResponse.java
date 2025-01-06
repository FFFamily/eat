package com.tutu.common.Response;

import com.tutu.common.enums.ResponseCodeEnum;
import lombok.Data;

/**
 * 基础响应
 */
@Data
public class BaseResponse<T> {
    // 状态码
    private int code;
    // 消息体
    private String msg;
    // 数据
    private T data;

    /**
     * 成功返回
     */
    public static <T> BaseResponse<T> success() {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode(ResponseCodeEnum.SUCCESS.getCode());
        baseResponse.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        return baseResponse;
    }
    /**
     * 成功返回
     */
    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode(ResponseCodeEnum.SUCCESS.getCode());
        baseResponse.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        baseResponse.setData(data);
        return baseResponse;
    }
    /**
     * 失败返回
     */
    public static <T> BaseResponse<T> error(String msg) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode(ResponseCodeEnum.ERROR.getCode());
        baseResponse.setMsg(msg);
        return baseResponse;
    }

}
