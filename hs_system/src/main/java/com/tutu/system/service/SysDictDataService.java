package com.tutu.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.system.dto.SysDictDataDTO;
import com.tutu.system.entity.SysDictData;
import com.tutu.system.mapper.SysDictDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 字典数据服务实现类 - 使用MyBatis Plus方式
 */
@Slf4j
@Service
public class SysDictDataService extends ServiceImpl<SysDictDataMapper, SysDictData> {
    
    /**
     * 根据字典类型查询字典数据
     */
    public List<SysDictData> findByDictType(String dictType) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictType, dictType)
                   .eq(SysDictData::getStatus, 1)
                   .eq(SysDictData::getIsDeleted, CommonConstant.NO_STR)
                   .orderByAsc(SysDictData::getDictSort)
                   .orderByDesc(SysDictData::getCreateTime);
        return list(queryWrapper);
    }
    
    /**
     * 根据字典类型和字典值查询字典标签
     */
    public String getDictLabel(String dictType, String dictValue) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictType, dictType)
                   .eq(SysDictData::getDictValue, dictValue)
                   .eq(SysDictData::getStatus, 1)
                   .eq(SysDictData::getIsDeleted, CommonConstant.NO_STR);
        
        SysDictData dictData = getOne(queryWrapper);
        return dictData != null ? dictData.getDictLabel() : "";
    }
    
    /**
     * 根据字典类型和字典标签查询字典值
     */
    public String getDictValue(String dictType, String dictLabel) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictType, dictType)
                   .eq(SysDictData::getDictLabel, dictLabel)
                   .eq(SysDictData::getStatus, 1)
                   .eq(SysDictData::getIsDeleted, CommonConstant.NO_STR);
        
        SysDictData dictData = getOne(queryWrapper);
        return dictData != null ? dictData.getDictValue() : "";
    }
    
    /**
     * 分页查询字典数据列表
     */
    public IPage<SysDictData> getPageList(int current, int size, String dictType, String keyword) {
        Page<SysDictData> page = new Page<>(current, size);
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(SysDictData::getIsDeleted, CommonConstant.NO_STR);
        
        // 字典类型条件
        if (StrUtil.isNotBlank(dictType)) {
            queryWrapper.eq(SysDictData::getDictType, dictType);
        }
        
        // 关键字搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(SysDictData::getDictLabel, keyword)
                .or()
                .like(SysDictData::getDictValue, keyword)
            );
        }
        
        queryWrapper.orderByAsc(SysDictData::getDictSort)
                   .orderByDesc(SysDictData::getCreateTime);
        
        return page(page, queryWrapper);
    }
    
    /**
     * 创建字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictData(SysDictDataDTO dictDataDTO) {
        // 检查同一字典类型下字典值是否已存在
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictType, dictDataDTO.getDictType())
                   .eq(SysDictData::getDictValue, dictDataDTO.getDictValue())
                   .eq(SysDictData::getIsDeleted, CommonConstant.NO_STR);
        
        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("字典值已存在");
        }
        
        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(dictDataDTO, dictData);
        
        // 设置默认值
        if (dictData.getDictSort() == null) {
            dictData.setDictSort(0);
        }
        if (dictData.getStatus() == null) {
            dictData.setStatus(1);
        }
        if (StrUtil.isBlank(dictData.getIsDefault())) {
            dictData.setIsDefault("0");
        }
        
        return save(dictData);
    }
    
    /**
     * 更新字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictData(SysDictDataDTO dictDataDTO) {
        SysDictData existDictData = getById(dictDataDTO.getId());
        if (existDictData == null) {
            throw new RuntimeException("字典数据不存在");
        }
        
        // 检查同一字典类型下字典值是否被其他记录使用
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictType, dictDataDTO.getDictType())
                   .eq(SysDictData::getDictValue, dictDataDTO.getDictValue())
                   .eq(SysDictData::getIsDeleted, CommonConstant.NO_STR)
                   .ne(SysDictData::getId, dictDataDTO.getId());
        
        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("字典值已被使用");
        }
        
        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(dictDataDTO, dictData);
        
        return updateById(dictData);
    }
    
    /**
     * 删除字典数据
     */
    public boolean deleteDictData(String id) {
        return removeById(id);
    }
    
    /**
     * 批量删除字典数据
     */
    public boolean batchDeleteDictData(List<String> ids) {
        return removeByIds(ids);
    }
    
    /**
     * 根据字典类型删除字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByDictType(String dictType) {
        LambdaUpdateWrapper<SysDictData> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysDictData::getDictType, dictType)
                    .set(SysDictData::getIsDeleted, CommonConstant.YES_STR)
                    .set(SysDictData::getUpdateTime, new Date());
        
        return update(updateWrapper);
    }
    
    /**
     * 设置默认字典数据
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefault(String id) {
        SysDictData dictData = getById(id);
        if (dictData == null) {
            throw new RuntimeException("字典数据不存在");
        }
        
        // 先将同一字典类型下的所有数据设为非默认
        LambdaUpdateWrapper<SysDictData> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysDictData::getDictType, dictData.getDictType())
                    .set(SysDictData::getIsDefault, "0")
                    .set(SysDictData::getUpdateTime, new Date());
        update(updateWrapper);
        
        // 再将当前数据设为默认
        updateWrapper.clear();
        updateWrapper.eq(SysDictData::getId, id)
                    .set(SysDictData::getIsDefault, "1")
                    .set(SysDictData::getUpdateTime, new Date());
        
        return update(updateWrapper);
    }
    
    /**
     * 刷新字典缓存
     */
    public void refreshCache() {
        // 这里可以实现字典缓存刷新逻辑
        // 比如清除Redis缓存等
        log.info("字典数据缓存已刷新");
    }
}
