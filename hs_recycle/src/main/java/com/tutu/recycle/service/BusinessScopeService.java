package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.entity.BusinessScope;
import com.tutu.recycle.mapper.BusinessScopeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusinessScopeService extends ServiceImpl<BusinessScopeMapper, BusinessScope> {
    
    /**
     * 根据货物类型查询经营范围（按排序号排序）
     */
    public List<BusinessScope> getByGoodType(String goodType) {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusinessScope::getGoodType, goodType);
        wrapper.orderByAsc(BusinessScope::getSortNum);
        return list(wrapper);
    }
    
    /**
     * 根据货物名称模糊查询经营范围
     */
    public List<BusinessScope> searchByGoodName(String goodName) {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(BusinessScope::getGoodName, goodName);
        wrapper.orderByAsc(BusinessScope::getSortNum);
        return list(wrapper);
    }
    
    /**
     * 创建经营范围
     */
    @Transactional(rollbackFor = Exception.class)
    public BusinessScope createBusinessScope(BusinessScope businessScope) {
        // 检查编号唯一性
        checkNoUniqueness(businessScope.getNo(), null);
        
        Integer maxSortNum = getMaxSortNum();
        businessScope.setSortNum(maxSortNum + 1);
        save(businessScope);
        return businessScope;
    }
    
    /**
     * 更新经营范围
     */
    public boolean updateBusinessScope(BusinessScope businessScope) {
        // 检查编号唯一性
        checkNoUniqueness(businessScope.getNo(), businessScope.getId());
        
        return updateById(businessScope);
    }
    
    /**
     * 删除经营范围
     */
    @Transactional(rollbackFor = Exception.class)
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
    
    /**
     * 检查编号唯一性
     * @param no 编号
     * @param excludeId 排除的ID（更新时使用）
     */
    private void checkNoUniqueness(String no, String excludeId) {
        if (no == null || no.trim().isEmpty()) {
            throw new ServiceException("编号不能为空");
        }
        
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusinessScope::getNo, no);
        
        // 如果是更新操作，排除当前记录
        if (excludeId != null) {
            wrapper.ne(BusinessScope::getId, excludeId);
        }
        
        BusinessScope existing = getOne(wrapper);
        if (existing != null) {
            throw new ServiceException("编号已存在，请使用其他编号");
        }
    }
    
    /**
     * 获取指定货物类型的最大排序号
     */
    private Integer getMaxSortNum() {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BusinessScope::getSortNum);
        wrapper.last("LIMIT 1");
        BusinessScope maxSortRecord = getOne(wrapper);
        return maxSortRecord != null ? maxSortRecord.getSortNum() : 0;
    }
    
    /**
     * 上移经营范围
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveUp(String id) {
        BusinessScope current = getById(id);
        if (current == null) {
            throw new ServiceException("经营范围不存在");
        }
        
        // 查找同类型中排序号小于当前记录的最大排序号记录
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(BusinessScope::getSortNum, current.getSortNum());
        wrapper.orderByDesc(BusinessScope::getSortNum);
        wrapper.last("LIMIT 1");
        BusinessScope previous = getOne(wrapper);
        
        if (previous == null) {
            throw new ServiceException("已经是第一个，无法上移");
        }
        
        // 交换排序号
        Integer tempSortNum = current.getSortNum();
        current.setSortNum(previous.getSortNum());
        previous.setSortNum(tempSortNum);
        
        updateById(current);
        updateById(previous);
    }
    
    /**
     * 下移经营范围
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveDown(String id) {
        BusinessScope current = getById(id);
        if (current == null) {
            throw new ServiceException("经营范围不存在");
        }
        
        // 查找同类型中排序号大于当前记录的最小排序号记录
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(BusinessScope::getSortNum, current.getSortNum());
        wrapper.orderByAsc(BusinessScope::getSortNum);
        wrapper.last("LIMIT 1");
        BusinessScope next = getOne(wrapper);
        
        if (next == null) {
            throw new ServiceException("已经是最后一个，无法下移");
        }
        
        // 交换排序号
        Integer tempSortNum = current.getSortNum();
        current.setSortNum(next.getSortNum());
        next.setSortNum(tempSortNum);
        
        updateById(current);
        updateById(next);
    }

    
    /**
     * 获取所有经营范围（按排序号排序）
     */
    public List<BusinessScope> getAllOrdered() {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BusinessScope::getGoodType, BusinessScope::getSortNum);
        return list(wrapper);
    }
} 