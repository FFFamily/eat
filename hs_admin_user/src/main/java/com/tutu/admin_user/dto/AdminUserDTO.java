package com.tutu.admin_user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 管理员用户DTO
 */
@Data
public class AdminUserDTO {
    
    private String id;
    
    /**
     * 登录账号
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 账号密码
     */
    private String password;
    
    /**
     * 真实姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 部门ID
     */
    private String deptId;
    
    /**
     * 角色ID列表
     */
    private List<String> roleIds;
}
