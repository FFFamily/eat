package com.tutu.api.controller.account;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.AccountBankCard;
import com.tutu.user.service.AccountBankCardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户银行卡信息控制器
 */
@RestController
@RequestMapping("/account/bank-card")
public class AccountBankCardController {

    @Resource
    private AccountBankCardService accountBankCardService;

    /**
     * 新增银行卡
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody AccountBankCard accountBankCard) {
        boolean result = accountBankCardService.save(accountBankCard);
        return BaseResponse.success(result);
    }
    
    /**
     * 修改银行卡信息
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody AccountBankCard accountBankCard) {
        boolean result = accountBankCardService.updateById(accountBankCard);
        return BaseResponse.success(result);
    }

    /**
     * 删除银行卡
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        boolean result = accountBankCardService.removeById(id);
        return BaseResponse.success(result);
    }

    /**
     * 根据ID查询银行卡（带账户名称）
     */
    @GetMapping("/get/{id}")
    public BaseResponse<AccountBankCard> getById(@PathVariable String id) {
        AccountBankCard accountBankCard = accountBankCardService.getByIdWithAccount(id);
        return BaseResponse.success(accountBankCard);
    }
    
    /**
     * 设置默认银行卡
     * @param accountId
     * @return
     */
    @PutMapping("/set-default/{id}")
    public BaseResponse<Void> setDefault(@PathVariable String id) {
        accountBankCardService.updateDefaultCard(id);
        return BaseResponse.success();
    }

    /**
     * 根据账户ID查询银行卡列表（带账户名称）
     */
    @GetMapping("/account/{accountId}")
    public BaseResponse<List<AccountBankCard>> getByAccountId(@PathVariable String accountId) {
        List<AccountBankCard> list = accountBankCardService.listByAccountIdWithAccount(accountId);
        return BaseResponse.success(list);
    }

    /**
     * 分页查询银行卡信息（带账户名称）
     */
    @GetMapping("/page")
    public BaseResponse<IPage<AccountBankCard>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            AccountBankCard query) {
        Page<AccountBankCard> ipage = new Page<>(page, size);
        IPage<AccountBankCard> result = accountBankCardService.pageWithAccount(ipage, query);
        return BaseResponse.success(result);
    }
}