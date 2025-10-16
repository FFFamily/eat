package com.tutu.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.system.dto.SysDictTypeDTO;
import com.tutu.system.entity.dict.SysDictData;
import com.tutu.system.entity.dict.SysDictType;
import com.tutu.system.mapper.SysDictTypeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统字典类型服务实现类
 * 使用MyBatis-Plus方式
 */
@Service
public class SysDictTypeService extends ServiceImpl<SysDictTypeMapper, SysDictType> {
    
    @Autowired
    private SysDictDataService sysDictDataService;
    
    /**
     * 根据字典类型查询字典类型信息
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    public SysDictType findByDictType(String dictType) {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictType::getDictType, dictType)
                   .eq(SysDictType::getIsDeleted, CommonConstant.NO_STR);
        return getOne(queryWrapper);
    }
    
    /**
     * 分页查询字典类型列表
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    public IPage<SysDictType> getPageList(int current, int size, String keyword) {
        Page<SysDictType> page = new Page<>(current, size);
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(SysDictType::getIsDeleted, CommonConstant.NO_STR);
        
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(SysDictType::getDictName, keyword)
                .or()
                .like(SysDictType::getDictType, keyword)
            );
        }
        
        queryWrapper.orderByDesc(SysDictType::getCreateTime);
        
        return page(page, queryWrapper);
    }
    
    /**
     * 创建字典类型
     * @param dictTypeDTO 字典类型DTO
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictType(SysDictTypeDTO dictTypeDTO) {
        // 检查字典类型是否已存在
        if (findByDictType(dictTypeDTO.getDictType()) != null) {
            throw new RuntimeException("字典类型已存在");
        }
        
        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(dictTypeDTO, dictType);
        
        // 设置默认状态
        if (dictType.getStatus() == null) {
            dictType.setStatus(1);
        }
        
        return save(dictType);
    }
    
    /**
     * 更新字典类型
     * @param dictTypeDTO 字典类型DTO
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictTypeDTO dictTypeDTO) {
        SysDictType existDictType = getById(dictTypeDTO.getId());
        if (existDictType == null) {
            throw new RuntimeException("字典类型不存在");
        }
        
        // 检查字典类型是否被其他记录使用
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictType::getDictType, dictTypeDTO.getDictType())
                   .eq(SysDictType::getIsDeleted, CommonConstant.NO_STR)
                   .ne(SysDictType::getId, dictTypeDTO.getId());
        
        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("字典类型已被使用");
        }
        
        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(dictTypeDTO, dictType);
        
        return updateById(dictType);
    }
    
    /**
     * 删除字典类型
     * @param id 字典类型ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(String id) {
        SysDictType dictType = getById(id);
        if (dictType == null) {
            throw new RuntimeException("字典类型不存在");
        }
        
        // 检查是否有关联的字典数据
        List<SysDictData> dictDataList = sysDictDataService.findByDictType(dictType.getDictType());
        if (!dictDataList.isEmpty()) {
            throw new RuntimeException("存在关联的字典数据，无法删除");
        }
        
        return removeById(id);
    }
    
    /**
     * 批量删除字典类型
     * @param ids ID列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteDictTypes(List<String> ids) {
        for (String id : ids) {
            SysDictType dictType = getById(id);
            if (dictType != null) {
                List<SysDictData> dictDataList = sysDictDataService.findByDictType(dictType.getDictType());
                if (!dictDataList.isEmpty()) {
                    throw new RuntimeException("字典类型【" + dictType.getDictName() + "】存在关联的字典数据，无法删除");
                }
            }
        }
        return removeByIds(ids);
    }
    
    /**
     * 查询所有启用的字典类型
     * @return 字典类型列表
     */
    public List<SysDictType> findAllEnabled() {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictType::getStatus, 1)
                   .eq(SysDictType::getIsDeleted, CommonConstant.NO_STR)
                   .orderByDesc(SysDictType::getCreateTime);
        return list(queryWrapper);
    }
}
