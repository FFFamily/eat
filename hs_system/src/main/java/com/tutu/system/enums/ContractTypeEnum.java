package com.tutu.system.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractTypeEnum implements BaseEnum<ContractTypeEnum,String> {
    RECYCLE("recycle", "回收合同"),
    LEASE("lease", "租赁合同")
    ;
    private final String code;
    private final String title;
}
