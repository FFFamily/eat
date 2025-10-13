package com.tutu.recycle.request.recycle_order;

import lombok.Data;

/**
 * 来源识别码
 */
@Data
public class SourceCode {
    // 源识别码
    private String identifyCode;
    // 变更原因
    private String changeReason;
}
