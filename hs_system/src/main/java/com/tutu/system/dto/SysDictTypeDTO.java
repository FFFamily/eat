package com.tutu.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 字典类型DTO
 */
@Data
public class SysDictTypeDTO {
    
    private String id;
    
    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    private String dictName;
    
    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
