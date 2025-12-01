package com.tutu.api.controller.account;

import com.tutu.api.request.account.UpdateServiceScopeRequest;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Account;
import com.tutu.user.entity.AccountServiceScope;
import com.tutu.user.service.AccountService;
import com.tutu.user.service.AccountServiceScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account/service-provider")
public class ServiceProviderController {
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountServiceScopeService accountServiceScopeService;

    /**
     * 查询业务类型为服务商的账户列表
     * @return 服务商账户列表
     */
    @GetMapping("/list")
    public BaseResponse<List<Account>> getServiceProviderList() {
        return BaseResponse.success(accountService.getServiceProviderList());
    }

    /**
     * 根据账户ID查询服务范围列表
     * @param accountId 账户ID
     * @return 服务范围列表
     */
    @GetMapping("/serviceScope/{accountId}")
    public BaseResponse<List<AccountServiceScope>> getServiceScopeList(@PathVariable String accountId) {
        return BaseResponse.success(accountServiceScopeService.listByAccountIdWithAccount(accountId));
    }

    /**
     * 更新账户的服务范围（省、市、区）
     * 先删除该账户的所有服务范围，然后批量保存新的服务范围
     * @param request 更新请求，包含 accountId 和 serviceScopeList
     * @return 更新成功返回成功响应
     */
    @PutMapping("/updateServiceScope")
    public BaseResponse<Void> updateServiceScope(@RequestBody UpdateServiceScopeRequest request) {
        if (request == null || request.getAccountId() == null) {
            return BaseResponse.success();
        }
        accountServiceScopeService.updateServiceScope(request.getAccountId(), request.getServiceScopeList());
        return BaseResponse.success();
    }
}

