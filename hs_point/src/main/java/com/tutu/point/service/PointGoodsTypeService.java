package com.tutu.point.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.point.entity.PointGoodsType;
import com.tutu.point.mapper.PointGoodsTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 积分商品分类 Service
 */
@Service
public class PointGoodsTypeService extends ServiceImpl<PointGoodsTypeMapper, PointGoodsType> {
    
    /**
     * 创建分类
     */
    public PointGoodsType createType(PointGoodsType type) {
        if (StrUtil.isBlank(type.getTypeName())) {
            throw new ServiceException("分类名称不能为空");
        }
        
        // 校验名称唯一性
        LambdaQueryWrapper<PointGoodsType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointGoodsType::getTypeName, type.getTypeName());
        if (count(wrapper) > 0) {
            throw new ServiceException("分类名称已存在");
        }
        
        // 设置默认状态
        if (StrUtil.isBlank(type.getStatus())) {
            type.setStatus("1");
        }
        
        // 设置默认排序
        if (type.getSortNum() == null) {
            type.setSortNum(0);
        }
        
        save(type);
        return type;
    }
    
    /**
     * 更新分类
     */
    public PointGoodsType updateType(PointGoodsType type) {
        if (StrUtil.isBlank(type.getId())) {
            throw new ServiceException("分类ID不能为空");
        }
        
        PointGoodsType existing = getById(type.getId());
        if (existing == null) {
            throw new ServiceException("分类不存在");
        }
        
        // 如果修改了分类名称，需要校验唯一性
        if (!existing.getTypeName().equals(type.getTypeName())) {
            LambdaQueryWrapper<PointGoodsType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointGoodsType::getTypeName, type.getTypeName());
            wrapper.ne(PointGoodsType::getId, type.getId());
            if (count(wrapper) > 0) {
                throw new ServiceException("分类名称已存在");
            }
        }
        
        updateById(type);
        return type;
    }
    
    /**
     * 启用/禁用分类
     */
    public void updateTypeStatus(String typeId, String status) {
        PointGoodsType type = getById(typeId);
        if (type == null) {
            throw new ServiceException("分类不存在");
        }
        
        type.setStatus(status);
        updateById(type);
    }
    
    /**
     * 分页查询分类
     */
    public Page<PointGoodsType> pageType(Integer page, Integer size, String typeName, String status) {
        LambdaQueryWrapper<PointGoodsType> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(typeName)) {
            wrapper.like(PointGoodsType::getTypeName, typeName);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(PointGoodsType::getStatus, status);
        }
        
        wrapper.orderByAsc(PointGoodsType::getSortNum);
        wrapper.orderByDesc(PointGoodsType::getCreateTime);
        
        return page(new Page<>(page, size), wrapper);
    }
    
    /**
     * 获取所有启用的分类
     */
    public List<PointGoodsType> listActiveTypes() {
        LambdaQueryWrapper<PointGoodsType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointGoodsType::getStatus, "1");
        wrapper.orderByAsc(PointGoodsType::getSortNum);
        return list(wrapper);
    }
}

