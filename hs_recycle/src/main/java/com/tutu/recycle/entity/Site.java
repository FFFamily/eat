package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 站点实体
 */
@Getter
@Setter
@TableName("site")
public class Site {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点名称
     */
    private String name;

    /**
     * 站点地址
     */
    private String address;
}
