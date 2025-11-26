package com.tutu.user.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

/**
 * 账户服务范围
 */
@Getter
@Setter
@TableName("account_service_scope")
public class AccountServiceScope extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 账户ID
    private String accountId;
    @TableField(exist = false)
    private String accountName;
    // 服务范围省
    private String province;
    // 服务范围市
    private String city;
    // 服务范围区
    private String district;
}

