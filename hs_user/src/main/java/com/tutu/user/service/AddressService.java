package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.Address;
import com.tutu.user.mapper.AddressMapper;
import com.tutu.user.service.AddressService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 地址管理 Service 实现类
 */
@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {

    /**
     * 新增地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAddress(Address address) {
        String accountId = address.getAccountId();
        String isDefault = address.getIsDefault();
        // 如果是第一个地址，自动设为默认地址
        List<Address> existingAddresses = findByAccountId(accountId);
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(CommonConstant.YES_STR);
        } else {
            // 新地址默认设为非默认
            if (StringUtils.isEmpty(isDefault)) {
                address.setIsDefault(CommonConstant.NO_STR);
            }else {
                address.setIsDefault(isDefault);
            }
            if (address.getIsDefault().equals(CommonConstant.YES_STR)) {
                LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Address::getAccountId, address.getAccountId())
                        .set(Address::getIsDefault, CommonConstant.NO_STR);
                this.update(updateWrapper);
            }
        }
        address.setRealAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        save(address);
    }

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
    public List<Address> findByAccountId(String accountId) {
        return list(new LambdaQueryWrapper<Address>().eq(Address::getAccountId, accountId));
    }

    /**
     * 设为默认地址
     * @param addressId 地址ID
     * @param accountId 用户ID
     * @return 是否成功
     */
    @Transactional
    public boolean setDefaultAddress(String addressId) {
        Address address = getById(addressId);
        if (address == null) {
            throw new ServiceException("地址不存在");
        }
    

        // 1. 先将该用户的所有地址设为非默认
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getAccountId, address.getAccountId())
                    .set(Address::getIsDefault, CommonConstant.NO_STR);
        this.update(updateWrapper);
        
        // 2. 将指定地址设为默认
        LambdaUpdateWrapper<Address> setDefaultWrapper = new LambdaUpdateWrapper<>();
        setDefaultWrapper.eq(Address::getId, addressId)
                        .eq(Address::getAccountId, address.getAccountId())
                        .set(Address::getIsDefault, CommonConstant.YES_STR);
        
        return this.update(setDefaultWrapper);
    }
} 