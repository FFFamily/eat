package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Address;
import com.tutu.user.service.AddressService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 地址管理 Controller
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 新增地址
     */
    @PostMapping("/create")
    public BaseResponse<Boolean> save(@RequestBody Address address) {
        return BaseResponse.success(addressService.save(address));
    }
    
    /**
     * 修改地址
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody Address address) {
        return BaseResponse.success(addressService.updateById(address));
    }
    
    /**
     * 删除地址
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(addressService.removeById(id));
    }
    
    /**
     * 根据ID查询地址
     */
    @GetMapping("/{id}")
    public BaseResponse<Address> getById(@PathVariable String id) {
        return BaseResponse.success(addressService.getById(id));
    }
    
    /**
     * 分页查询地址列表
     */
    @GetMapping("/page")
    public BaseResponse<Page<Address>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String realAddress) {
        Page<Address> result = addressService.findPage(page,size, accountName, realAddress);
        return BaseResponse.success(result);
    }
    
    /**
     * 根据用户ID查询地址列表
     */
    @GetMapping("/user/{accountId}")
    public BaseResponse<Page<Address>> getByAccountId(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Address> page = new Page<>(current, size);
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId);
        queryWrapper.orderByDesc("create_time");
        
        return BaseResponse.success(addressService.page(page, queryWrapper));
    }
} 