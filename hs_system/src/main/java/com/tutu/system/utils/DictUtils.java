package com.tutu.system.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.tutu.system.entity.SysDictData;
import com.tutu.system.service.SysDictDataService;

import java.util.List;

/**
 * 字典工具类
 */
public class DictUtils {
    
    private static SysDictDataService sysDictDataService;
    
    /**
     * 获取字典数据服务
     */
    private static SysDictDataService getDictDataService() {
        if (sysDictDataService == null) {
            sysDictDataService = SpringUtil.getBean(SysDictDataService.class);
        }
        return sysDictDataService;
    }
    
    /**
     * 根据字典类型和字典值获取字典标签
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue) {
        return getDictDataService().getDictLabel(dictType, dictValue);
    }
    
    /**
     * 根据字典类型和字典标签获取字典值
     * 
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel) {
        return getDictDataService().getDictValue(dictType, dictLabel);
    }
    
    /**
     * 根据字典类型获取字典数据列表
     * 
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    public static List<SysDictData> getDictDataList(String dictType) {
        return getDictDataService().findByDictType(dictType);
    }
    
    /**
     * 获取用户状态标签
     * 
     * @param status 状态值
     * @return 状态标签
     */
    public static String getUserStatusLabel(String status) {
        return getDictLabel("user_status", status);
    }
    
    /**
     * 获取数据状态标签
     * 
     * @param status 状态值
     * @return 状态标签
     */
    public static String getDataStatusLabel(String status) {
        return getDictLabel("data_status", status);
    }
    
    /**
     * 获取操作类型标签
     * 
     * @param operationType 操作类型值
     * @return 操作类型标签
     */
    public static String getOperationTypeLabel(String operationType) {
        return getDictLabel("operation_type", operationType);
    }
}
