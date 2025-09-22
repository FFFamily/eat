package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 城市实体类
 */
@Getter
@Setter
@TableName("sys_city")
public class City extends BaseEntity {
    
    /**
     * 城市ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 城市编码
     */
    private String code;
    
    /**
     * 城市名称
     */
    private String name;
    
    /**
     * 父级城市编码
     */
    private String pCode;
    
    /**
     * 城市层级链（如：中国/广东省/深圳市）
     */
    private String chain;
}
