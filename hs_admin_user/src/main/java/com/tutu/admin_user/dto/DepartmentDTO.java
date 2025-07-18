package com.tutu.admin_user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 部门DTO
 */
@Data
public class DepartmentDTO {
    
    private String id;
    
    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String name;
    
    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    private String code;
    
    /**
     * 父部门ID
     */
    private String parentId;
    
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
