package com.tutu.common.Response;

import com.tutu.common.enums.ResponseCodeEnum;
import lombok.Data;

/**
 * 基础响应
 */
@Data
public class BaseResponse {
    // 状态码
    private int code;
    // 消息体
    private String msg;
    // 数据
    private Object data;

    /**
     * 成功返回
     */
    public static BaseResponse success() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCodeEnum.SUCCESS.getCode());
        baseResponse.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        return baseResponse;
    }
    /**
     * 成功返回
     */
    public static BaseResponse success(String msg) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCodeEnum.SUCCESS.getCode());
        baseResponse.setMsg(msg);
        return baseResponse;
    }
    /**
     * 失败返回
     */
    public static BaseResponse error(String msg) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCodeEnum.ERROR.getCode());
        baseResponse.setMsg(msg);
        return baseResponse;
    }
}
