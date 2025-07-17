package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.util.List;

import com.tutu.common.entity.BaseEntity;
import lombok.Data;

@Data
public class AdminUser extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private String deptId;
}
