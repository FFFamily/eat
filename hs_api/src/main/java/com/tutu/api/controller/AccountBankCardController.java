package com.tutu.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.AccountBankCard;
import com.tutu.user.service.AccountBankCardService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
     * 根据ID查询银行卡
     */
    @GetMapping("/get/{id}")
    public BaseResponse<AccountBankCard> getById(@PathVariable String id) {
        AccountBankCard accountBankCard = accountBankCardService.getById(id);
        return BaseResponse.success(accountBankCard);
    }

    /**
     * 根据账户ID查询银行卡列表
     */
    @GetMapping("/account/{accountId}")
    public BaseResponse<List<AccountBankCard>> getByAccountId(@PathVariable String accountId) {
        LambdaQueryWrapper<AccountBankCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBankCard::getAccountId, accountId);
        List<AccountBankCard> list = accountBankCardService.list(queryWrapper);
        return BaseResponse.success(list);
    }

    /**
     * 分页查询银行卡信息
     */
    @GetMapping("/page")
    public BaseResponse<IPage<AccountBankCard>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String accountId) {
        Page<AccountBankCard> ipage = new Page<>(page, size);
        LambdaQueryWrapper<AccountBankCard> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(accountId) ) {
            queryWrapper.eq(AccountBankCard::getAccountId, accountId);
        }
        queryWrapper.orderByDesc(AccountBankCard::getCreateTime);
        IPage<AccountBankCard> result = accountBankCardService.page(ipage, queryWrapper);
        return BaseResponse.success(result);
    }
}