package com.tutu.api.controller.account;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.AccountCustomer;
import com.tutu.user.service.AccountCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户客户关系控制器
 */
@RestController
@RequestMapping("/account/customer")
public class AccountCustomerController {

    @Autowired
    private AccountCustomerService accountCustomerService;

    /**
     * 新增账户客户关系
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody AccountCustomer accountCustomer) {
        boolean result = accountCustomerService.save(accountCustomer);
        return BaseResponse.success(result);
    }

    /**
     * 修改账户客户关系
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody AccountCustomer accountCustomer) {
        boolean result = accountCustomerService.updateById(accountCustomer);
        return BaseResponse.success(result);
    }

    /**
     * 删除账户客户关系
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        boolean result = accountCustomerService.removeById(id);
        return BaseResponse.success(result);
    }

    /**
     * 根据ID查询详情（带账户名称）
     */
    @GetMapping("/get/{id}")
    public BaseResponse<AccountCustomer> getById(@PathVariable String id) {
        AccountCustomer accountCustomer = accountCustomerService.getByIdWithAccount(id);
        return BaseResponse.success(accountCustomer);
    }

    /**
     * 根据账户ID查询客户列表（带账户名称）
     */
    @GetMapping("/account/{accountId}")
    public BaseResponse<List<AccountCustomer>> getByAccountId(@PathVariable String accountId) {
        List<AccountCustomer> list = accountCustomerService.listByAccountIdWithAccount(accountId);
        return BaseResponse.success(list);
    }

    /**
     * 根据客户账户ID查询账户列表（带账户名称）
     */
    @GetMapping("/customer/{customerAccountId}")
    public BaseResponse<List<AccountCustomer>> getByCustomerAccountId(@PathVariable String customerAccountId) {
        List<AccountCustomer> list = accountCustomerService.listByCustomerAccountIdWithAccount(customerAccountId);
        return BaseResponse.success(list);
    }

    /**
     * 分页查询账户客户关系（带账户名称）
     */
    @GetMapping("/page")
    public BaseResponse<IPage<AccountCustomer>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            AccountCustomer query) {
        Page<AccountCustomer> ipage = new Page<>(page, size);
        IPage<AccountCustomer> result = accountCustomerService.pageWithAccount(ipage, query);
        return BaseResponse.success(result);
    }

    /**
     * 根据账户ID和客户账户ID删除关系
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteByAccountIdAndCustomerAccountId(
            @RequestParam String accountId,
            @RequestParam String customerAccountId) {
        boolean result = accountCustomerService.deleteByAccountIdAndCustomerAccountId(accountId, customerAccountId);
        return BaseResponse.success(result);
    }
}

