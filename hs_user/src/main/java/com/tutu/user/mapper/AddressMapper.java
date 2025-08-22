package com.tutu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.user.entity.Address;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    /**
     * 分页查询地址
     * @param page 页码
     * @param size 每页条数
     * @param accountName 用户名称
     * @param address 地址
     * @return
     */
    List<Address> findPage(@Param("page") Integer page, @Param("size") Integer size, @Param("accountName") String accountName, @Param("address") String address);
    /**
     * 查询地址数量
     * @param accountName 用户名称
     * @param address 地址
     * @return
     */
    long findPageCount(@Param("accountName") String accountName, @Param("address") String address);
} 