package com.tutu.system.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantBootstrapAdminDTO {

    @NotBlank(message = "管理员账号(username)不能为空")
    private String username;

    @NotBlank(message = "管理员密码(password)不能为空")
    private String password;

    private String nickname;
    private String phone;
}

