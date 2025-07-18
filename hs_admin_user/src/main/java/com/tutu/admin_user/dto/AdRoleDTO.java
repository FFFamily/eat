package com.tutu.admin_user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色DTO
 */
@Data
public class AdRoleDTO {
    
    private String id;
    
    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    private String name;
    
    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String code;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 权限ID列表
     */
    private List<String> permissionIds;
}
