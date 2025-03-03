package com.tutu.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    // 创建人
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    // 更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
