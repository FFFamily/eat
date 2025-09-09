package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Address;
import com.tutu.user.mapper.AddressMapper;
import com.tutu.user.service.AddressService;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 地址管理 Service 实现类
 */
@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {
    /**
     * 分页查询地址
     * @param page 页码
     * @param size 每页条数
     * @param accountName 用户名称
     * @param realAddress 地址
     * @return
     */
    public Page<Address> findPage(Integer page, Integer size, String accountName, String realAddress) {
        Page<Address> result = new Page<>(page, size);
        List<Address> list = this.baseMapper.findPage(page, size, accountName, realAddress);
        result.setRecords(list);
        long count =  this.baseMapper.findPageCount(accountName, realAddress);
        result.setTotal(count);
        return result;
    }

    /**
     * 根据账号ID查询地址列表
     * @param accountId 账号ID
     * @return
     */ 
    public List<Address> findByAccountId(Long accountId) {
        return list(new LambdaQueryWrapper<Address>().eq(Address::getAccountId, accountId));
    }
} 