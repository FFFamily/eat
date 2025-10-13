package com.tutu.recycle.request.recycle_order;

import java.util.List;

import lombok.Data;

@Data
public class TraceAbilityData {
    // 当前识别码
    private String currentIdentifyCode;
    // 来源
    private List<SourceCode> sourceCodes;
}
