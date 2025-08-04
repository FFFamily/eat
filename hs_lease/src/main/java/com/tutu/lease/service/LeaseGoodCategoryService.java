package com.tutu.lease.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.lease.entity.LeaseGoodCategory;
import com.tutu.lease.mapper.LeaseGoodCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 租赁商品分类服务实现类
 */
@Service
public class LeaseGoodCategoryService extends ServiceImpl<LeaseGoodCategoryMapper, LeaseGoodCategory>  {

    
    public LeaseGoodCategory findByName(String name) {
        LambdaQueryWrapper<LeaseGoodCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseGoodCategory::getName, name)
                .eq(LeaseGoodCategory::getIsDeleted, CommonConstant.NO_STR);
        return getOne(queryWrapper);
    }

    
    public boolean createCategory(LeaseGoodCategory category) {
        // 检查分类名称是否已存在
        if (findByName(category.getName()) != null) {
            throw new RuntimeException("分类名称已存在");
        }
        return save(category);
    }

    
    public boolean updateCategory(LeaseGoodCategory category) {
        LeaseGoodCategory existCategory = getById(category.getId());
        if (existCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查分类名称是否被其他分类使用
        LambdaQueryWrapper<LeaseGoodCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseGoodCategory::getName, category.getName())
                .eq(LeaseGoodCategory::getIsDeleted, CommonConstant.NO_STR)
                .ne(LeaseGoodCategory::getId, category.getId());

        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("分类名称已被使用");
        }

        return updateById(category);
    }

    /**
     * 根据ID列表查询分类
     */
    public List<LeaseGoodCategory> findByIdList(List<String> idList) {
        return listByIds(idList);
    }
}
