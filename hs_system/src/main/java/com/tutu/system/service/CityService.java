package com.tutu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.City;
import com.tutu.system.mapper.CityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 城市服务类
 */
@Service
public class CityService extends ServiceImpl<CityMapper, City> {
    
    /**
     * 根据父级编码查询子级城市
     */
    public List<City> getByParentCode(String pCode) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getPCode, pCode);
        wrapper.orderByAsc(City::getCode);
        return list(wrapper);
    }
    
    /**
     * 根据城市编码查询城市
     */
    public City getByCode(String code) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getCode, code);
        return getOne(wrapper);
    }
    
    /**
     * 根据城市名称模糊查询
     */
    public List<City> searchByName(String name) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(City::getName, name);
        wrapper.orderByAsc(City::getCode);
        return list(wrapper);
    }
    
    /**
     * 分页查询城市
     */
    public Page<City> pageQuery(Page<City> page, String name, String pCode) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        
        // 城市名称模糊查询
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(City::getName, name.trim());
        }
        
        // 父级编码查询
        if (pCode != null && !pCode.trim().isEmpty()) {
            wrapper.eq(City::getPCode, pCode.trim());
        }
        
        wrapper.orderByAsc(City::getCode);
        return page(page, wrapper);
    }
    
    /**
     * 创建城市
     */
    @Transactional(rollbackFor = Exception.class)
    public City createCity(City city) {
        // 检查编码唯一性
        checkCodeUniqueness(city.getCode(), null);
        
        // 构建层级链
        buildChain(city);
        
        save(city);
        return city;
    }
    
    /**
     * 更新城市
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCity(City city) {
        // 检查编码唯一性
        checkCodeUniqueness(city.getCode(), city.getId());
        
        // 构建层级链
        buildChain(city);
        
        return updateById(city);
    }
    
    /**
     * 删除城市
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCity(String id) {
        // 检查是否有子级城市
        City city = getById(id);
        if (city != null) {
            List<City> children = getByParentCode(city.getCode());
            if (!children.isEmpty()) {
                throw new ServiceException("该城市下还有子级城市，无法删除");
            }
        }
        
        return removeById(id);
    }
    
    /**
     * 检查编码唯一性
     */
    private void checkCodeUniqueness(String code, String excludeId) {
        if (code == null || code.trim().isEmpty()) {
            throw new ServiceException("城市编码不能为空");
        }
        
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getCode, code);
        
        // 如果是更新操作，排除当前记录
        if (excludeId != null) {
            wrapper.ne(City::getId, excludeId);
        }
        
        City existing = getOne(wrapper);
        if (existing != null) {
            throw new ServiceException("城市编码已存在，请使用其他编码");
        }
    }
    
    /**
     * 构建层级链
     */
    private void buildChain(City city) {
        if (city.getPCode() == null || city.getPCode().trim().isEmpty()) {
            // 顶级城市，层级链就是自己的名称
            city.setChain(city.getName());
        } else {
            // 有父级城市，需要构建完整的层级链
            City parent = getByCode(city.getPCode());
            if (parent != null) {
                city.setChain(parent.getChain() + "_" + city.getName());
            } else {
                throw new ServiceException("父级城市不存在");
            }
        }
    }
    
    /**
     * 获取所有顶级城市（省份）
     */
    public List<City> getTopLevelCities() {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(City::getPCode).or().eq(City::getPCode, "");
        wrapper.orderByAsc(City::getCode);
        return list(wrapper);
    }
    
    /**
     * 根据层级链查询城市
     */
    public City getByChain(String chain) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getChain, chain);
        return getOne(wrapper);
    }
}
