package com.tutu.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.constant.CommonConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    // 创建人
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    // 更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    // 逻辑删除
    @TableLogic(value = CommonConstant.NO_STR, delval = CommonConstant.YES_STR)
    private String isDeleted;
}
