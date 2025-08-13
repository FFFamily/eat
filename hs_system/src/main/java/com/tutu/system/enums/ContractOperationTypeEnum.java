package com.tutu.system.enums;

import lombok.Getter;

/**
 * 合同操作类型枚举
 */
@Getter
public enum ContractOperationTypeEnum {

    /**
     * 创建
     */
    CREATE("CREATE", "创建"),

    /**
     * 更新
     */
    UPDATE("UPDATE", "更新"),

    /**
     * 删除
     */
    DELETE("DELETE", "删除"),

    /**
     * 审核
     */
    APPROVE("APPROVE", "审核"),

    /**
     * 驳回
     */
    REJECT("REJECT", "驳回"),

    /**
     * 签署
     */
    SIGN("SIGN", "签署"),

    /**
     * 生效
     */
    ACTIVATE("ACTIVATE", "生效"),

    /**
     * 终止
     */
    TERMINATE("TERMINATE", "终止");

    private final String code;
    private final String description;

    ContractOperationTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ContractOperationTypeEnum getByCode(String code) {
        for (ContractOperationTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 