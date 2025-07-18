package com.tutu.admin_user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限DTO
 */
@Data
public class PermissionDTO {
    
    private String id;
    
    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String name;
    
    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String code;
    
    /**
     * 权限类型：1-菜单，2-按钮
     */
    @NotNull(message = "权限类型不能为空")
    private Integer type;
    
    /**
     * 父权限ID
     */
    private String parentId;
    
    /**
     * 路由路径
     */
    private String path;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
