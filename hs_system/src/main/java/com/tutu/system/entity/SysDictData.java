package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 字典排序
     */
    private Integer dictSort;
    
    /**
     * 字典标签
     */
    private String dictLabel;
    
    /**
     * 字典键值
     */
    private String dictValue;
    
    /**
     * 字典类型
     */
    private String dictType;
    
    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;
    
    /**
     * 表格回显样式
     */
    private String listClass;
    
    /**
     * 是否默认（1是 0否）
     */
    private String isDefault;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
