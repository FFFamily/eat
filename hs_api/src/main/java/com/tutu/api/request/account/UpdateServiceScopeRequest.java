package com.tutu.api.request.account;

import com.tutu.user.entity.AccountServiceScope;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 更新服务商服务范围请求
 */
@Getter
@Setter
public class UpdateServiceScopeRequest {
    /**
     * 账户ID
     */
    private String accountId;
    
    /**
     * 服务范围列表
     */
    private List<AccountServiceScope> serviceScopeList;
}

