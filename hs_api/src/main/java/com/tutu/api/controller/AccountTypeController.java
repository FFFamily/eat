package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.AccountType;
import com.tutu.user.service.AccountTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账号类型Controller
 */
@RestController
@RequestMapping("/system/account/type")
public class AccountTypeController {

    @Autowired
    private AccountTypeService accountTypeService;

    /**
     * 新增账号类型
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody AccountType accountType) {
        // 检查编号是否已存在
        if (accountTypeService.isCodeExists(accountType.getCode(), null)) {
            return BaseResponse.error("编号已存在");
        }
        boolean result = accountTypeService.save(accountType);
        return BaseResponse.success(result);
    }

    /**
     * 修改账号类型
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody AccountType accountType) {
        if (accountType.getId() == null) {
            return BaseResponse.error("ID不能为空");
        }
        
        // 检查编号是否已存在（排除当前记录）
        if (accountTypeService.isCodeExists(accountType.getCode(), accountType.getId())) {
            return BaseResponse.error("编号已存在");
        }
        
        boolean result = accountTypeService.updateById(accountType);
        return BaseResponse.success(result);
    }

    /**
     * 删除账号类型
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        boolean result = accountTypeService.removeById(id);
        return BaseResponse.success(result);
    }

    /**
     * 批量删除账号类型
     */
    @DeleteMapping("/batch-delete")
    public BaseResponse<Boolean> batchDelete(@RequestBody List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return BaseResponse.error("请选择要删除的记录");
        }
        boolean result = accountTypeService.removeByIds(ids);
        return BaseResponse.success(result);
    }

    /**
     * 根据ID查询账号类型
     */
    @GetMapping("/get/{id}")
    public BaseResponse<AccountType> getById(@PathVariable String id) {
        AccountType accountType = accountTypeService.getById(id);
        return BaseResponse.success(accountType);
    }

    /**
     * 根据编号查询账号类型
     */
    @GetMapping("/get/code/{code}")
    public BaseResponse<AccountType> getByCode(@PathVariable String code) {
        AccountType accountType = accountTypeService.getByCode(code);
        return BaseResponse.success(accountType);
    }

    /**
     * 分页查询账号类型
     */
    @GetMapping("/page")
    public BaseResponse<IPage<AccountType>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            AccountType accountType) {
        Page<AccountType> page = new Page<>(pageNum, pageSize);
        IPage<AccountType> result = accountTypeService.getPage(page, accountType);
        return BaseResponse.success(result);
    }

    /**
     * 获取所有启用的账号类型
     */
    @GetMapping("/enabled")
    public BaseResponse<List<AccountType>> getEnabledTypes() {
        List<AccountType> list = accountTypeService.getEnabledTypes();
        return BaseResponse.success(list);
    }

    /**
     * 获取所有账号类型
     */
    @GetMapping("/list")
    public BaseResponse<List<AccountType>> getAll() {
        List<AccountType> list = accountTypeService.list();
        return BaseResponse.success(list);
    }

} 