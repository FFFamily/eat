package com.tutu.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 字典数据DTO
 */
@Data
public class SysDictDataDTO {
    
    private String id;
    
    /**
     * 字典排序
     */
    private Integer dictSort;
    
    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;
    
    /**
     * 字典键值
     */
    @NotBlank(message = "字典键值不能为空")
    private String dictValue;
    
    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
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
