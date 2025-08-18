package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.entity.BusinessScope;
import com.tutu.recycle.mapper.BusinessScopeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessScopeService extends ServiceImpl<BusinessScopeMapper, BusinessScope> {
    
    /**
     * 根据货物类型查询经营范围
     */
    public List<BusinessScope> getByGoodType(String goodType) {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusinessScope::getGoodType, goodType);
        return list(wrapper);
    }
    
    /**
     * 根据货物名称模糊查询经营范围
     */
    public List<BusinessScope> searchByGoodName(String goodName) {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(BusinessScope::getGoodName, goodName);
        return list(wrapper);
    }
    
    /**
     * 创建经营范围
     */
    public BusinessScope createBusinessScope(BusinessScope businessScope) {
        save(businessScope);
        return businessScope;
    }
    
    /**
     * 更新经营范围
     */
    public boolean updateBusinessScope(BusinessScope businessScope) {
        return updateById(businessScope);
    }
    
    /**
     * 删除经营范围
     */
    public boolean deleteBusinessScope(String id) {
        return removeById(id);
    }

    /**
     * 更新是否显示
     */
    public void updateIsShow(BusinessScope param) {
        BusinessScope businessScope = getById(param.getId());
        if (businessScope == null) {
            throw new ServiceException("经营范围不存在");
        }
        businessScope.setIsShow(param.getIsShow());
        updateById(businessScope);
    }
} 