package com.tutu.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
    // 创建人
    private String createBy;
    // 更新人
    private String updateBy;
}
