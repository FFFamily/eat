package com.tutu.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutu.common.exceptions.ServiceException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 结果转换工具类 - 用于数据映射和字段填充
 * 支持将ID字段映射到名称字段，如userId -> userName
 */
public class ResultCovertUtil {
    
    /**
     * 批量映射ID到名称
     * @param dataList 数据列表
     * @param idFieldName ID字段名（如"userId"）
     * @param nameFieldName 名称字段名（如"userName"）
     * @param relatedList 关联数据列表
     * @param idGetter ID字段的getter方法
     * @param nameGetter 名称字段的getter方法
     * @param <T> 数据类型
     * @param <R> 关联数据类型
     * @param <ID> ID类型
     */
    public static <T, R, ID> void mapIdToName(List<T> dataList, 
                                            String idFieldName, 
                                            String nameFieldName,
                                            List<R> relatedList,
                                            Function<R, ID> idGetter,
                                            Function<R, String> nameGetter) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        
        try {
            // 提取所有ID
            Set<ID> idSet = new HashSet<>();
            for (T data : dataList) {
                Field idField = data.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                ID idValue = (ID) idField.get(data);
                if (idValue != null) {
                    idSet.add(idValue);
                }
            }
            
            if (CollUtil.isEmpty(idSet)) {
                return;
            }
            // 批量查询关联数据
            if (CollUtil.isEmpty(relatedList)) {
                return;
            }
            
            // 构建ID到名称的映射
            Map<ID, String> idToNameMap = relatedList.stream()
                    .collect(Collectors.toMap(
                            idGetter,
                            nameGetter,
                            (oldValue, newValue) -> oldValue
                    ));
            
            // 填充名称字段
            Field nameField = dataList.get(0).getClass().getDeclaredField(nameFieldName);
            nameField.setAccessible(true);
            
            for (T data : dataList) {
                Field idField = data.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                ID idValue = (ID) idField.get(data);
                
                if (idValue != null && idToNameMap.containsKey(idValue)) {
                    nameField.set(data, idToNameMap.get(idValue));
                }
            }
            
        } catch (Exception e) {
            throw new ServiceException("数据映射失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量映射ID到名称（简化版）
     * @param dataList 数据列表
     * @param idFieldName ID字段名
     * @param nameFieldName 名称字段名
     * @param idToNameMap ID到名称的映射Map
     * @param <T> 数据类型
     * @param <ID> ID类型
     */
    public static <T, ID> void mapIdToName(List<T> dataList, 
                                         String idFieldName, 
                                         String nameFieldName,
                                         Map<ID, String> idToNameMap) {
        if (CollUtil.isEmpty(dataList) || CollUtil.isEmpty(idToNameMap)) {
            return;
        }
        
        try {
            Field nameField = dataList.get(0).getClass().getDeclaredField(nameFieldName);
            nameField.setAccessible(true);
            
            for (T data : dataList) {
                Field idField = data.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                ID idValue = (ID) idField.get(data);
                
                if (idValue != null && idToNameMap.containsKey(idValue)) {
                    nameField.set(data, idToNameMap.get(idValue));
                }
            }
            
        } catch (Exception e) {
            throw new ServiceException("数据映射失败: " + e.getMessage());
        }
    }
    
    /**
     * 自定义映射方法
     * @param dataList 数据列表
     * @param mapper 自定义映射函数
     * @param <T> 数据类型
     */
    public static <T> void cover(List<T> dataList, Function<T, T> mapper) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        
        for (int i = 0; i < dataList.size(); i++) {
            dataList.set(i, mapper.apply(dataList.get(i)));
        }
    }
    
    /**
     * 批量设置字段值
     * @param dataList 数据列表
     * @param fieldName 字段名
     * @param value 字段值
     * @param <T> 数据类型
     */
    public static <T> void setFieldValue(List<T> dataList, String fieldName, Object value) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        
        try {
            Field field = dataList.get(0).getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            
            for (T data : dataList) {
                field.set(data, value);
            }
            
        } catch (Exception e) {
            throw new ServiceException("设置字段值失败: " + e.getMessage());
        }
    }
}
