package com.tutu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum OperationType {
    
    /**
     * 查询操作
     */
    SELECT("SELECT", "查询"),
    
    /**
     * 新增操作
     */
    INSERT("INSERT", "新增"),
    
    /**
     * 更新操作
     */
    UPDATE("UPDATE", "更新"),
    
    /**
     * 删除操作
     */
    DELETE("DELETE", "删除"),
    
    /**
     * 登录操作
     */
    LOGIN("LOGIN", "登录"),
    
    /**
     * 登出操作
     */
    LOGOUT("LOGOUT", "登出"),
    
    /**
     * 导入操作
     */
    IMPORT("IMPORT", "导入"),
    
    /**
     * 导出操作
     */
    EXPORT("EXPORT", "导出"),
    
    /**
     * 授权操作
     */
    GRANT("GRANT", "授权"),
    
    /**
     * 其他操作
     */
    OTHER("OTHER", "其他");
    
    private final String code;
    private final String description;
}
