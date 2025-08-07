package com.tutu.lease.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.lease.dto.LeaseGoodDto;
import com.tutu.lease.entity.LeaseGood;
import com.tutu.lease.entity.LeaseGoodCategory;
import com.tutu.lease.mapper.LeaseGoodCategoryMapper;
import com.tutu.lease.mapper.LeaseGoodMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaseGoodService extends ServiceImpl<LeaseGoodMapper, LeaseGood> {

    @Autowired
    private LeaseGoodCategoryMapper leaseGoodCategoryMapper;

    public Page<LeaseGoodDto> getByPage(Long pageNum, Long pageSize) {
        Page<LeaseGood> page = new Page<>(pageNum, pageSize);
        // 查询商品列表
        IPage<LeaseGood> leaseGoodPage = baseMapper.selectPage(page, null);
        // 查询商品分类列表
        List<LeaseGoodCategory> leaseGoodCategoryList = leaseGoodCategoryMapper.selectList(null);
        // 将商品分类列表转换为Map
        Map<String, String> leaseGoodCategoryMap = leaseGoodCategoryList.stream()
                .collect(Collectors.toMap(LeaseGoodCategory::getId, LeaseGoodCategory::getName));
        // 将商品列表转换为DTO列表
        List<LeaseGoodDto> leaseGoodDtoList = leaseGoodPage.getRecords().stream()
                .map(leaseGood -> {
                    LeaseGoodDto leaseGoodDto = new LeaseGoodDto();
                    BeanUtils.copyProperties(leaseGood, leaseGoodDto);
                    leaseGoodDto.setTypeName(leaseGoodCategoryMap.get(leaseGood.getType()));
                    return leaseGoodDto;
                })
                .collect(Collectors.toList());
        Page<LeaseGoodDto> leaseGoodDtoPage = new Page<>(leaseGoodPage.getCurrent(), leaseGoodPage.getSize());
        leaseGoodDtoPage.setRecords(leaseGoodDtoList);
        leaseGoodDtoPage.setTotal(leaseGoodPage.getTotal());
        return leaseGoodDtoPage;
    }
}