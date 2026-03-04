package com.tutu.system.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysTenantCreateDTO {

    @NotBlank(message = "租户编码(code)不能为空")
    private String code;

    @NotBlank(message = "租户名称(name)不能为空")
    private String name;

    /**
     * 1-启用，0-禁用
     */
    private Integer status;

    private String remark;
}

