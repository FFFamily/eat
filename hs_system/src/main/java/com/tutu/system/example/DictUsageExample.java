package com.tutu.system.example;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.system.dto.SysDictDataDTO;
import com.tutu.system.dto.SysDictTypeDTO;
import com.tutu.system.entity.dict.SysDictData;
import com.tutu.system.entity.dict.SysDictType;
import com.tutu.system.service.SysDictDataService;
import com.tutu.system.service.SysDictTypeService;
import com.tutu.system.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 字典管理使用示例
 */
@Component
public class DictUsageExample {
    
    @Autowired
    private SysDictTypeService sysDictTypeService;
    
    @Autowired
    private SysDictDataService sysDictDataService;
    
    /**
     * 字典类型管理示例
     */
    public void dictTypeExample() {
        // 1. 创建字典类型
        SysDictTypeDTO dictTypeDTO = new SysDictTypeDTO();
        dictTypeDTO.setDictName("性别");
        dictTypeDTO.setDictType("gender");
        dictTypeDTO.setStatus(1);
        dictTypeDTO.setRemark("性别字典");
        
        boolean createResult = sysDictTypeService.createDictType(dictTypeDTO);
        System.out.println("创建字典类型结果: " + createResult);
        
        // 2. 查询字典类型
        SysDictType dictType = sysDictTypeService.findByDictType("gender");
        System.out.println("查询到的字典类型: " + dictType.getDictName());
        
        // 3. 分页查询字典类型
        IPage<SysDictType> dictTypePage = sysDictTypeService.getPageList(1, 10, "性别");
        System.out.println("分页查询结果总数: " + dictTypePage.getTotal());
        
        // 4. 查询所有启用的字典类型
        List<SysDictType> enabledDictTypes = sysDictTypeService.findAllEnabled();
        System.out.println("启用的字典类型数量: " + enabledDictTypes.size());
        
        // 5. 更新字典类型
        dictTypeDTO.setId(dictType.getId());
        dictTypeDTO.setRemark("更新后的性别字典");
        boolean updateResult = sysDictTypeService.updateDictType(dictTypeDTO);
        System.out.println("更新字典类型结果: " + updateResult);
    }
    
    /**
     * 字典数据管理示例
     */
    public void dictDataExample() {
        // 1. 创建字典数据
        SysDictDataDTO dictDataDTO = new SysDictDataDTO();
        dictDataDTO.setDictType("gender");
        dictDataDTO.setDictLabel("男");
        dictDataDTO.setDictValue("1");
        dictDataDTO.setDictSort(1);
        dictDataDTO.setStatus(1);
        dictDataDTO.setIsDefault("1");
        dictDataDTO.setListClass("primary");
        dictDataDTO.setRemark("男性");
        
        boolean createResult = sysDictDataService.createDictData(dictDataDTO);
        System.out.println("创建字典数据结果: " + createResult);
        
        // 创建第二个字典数据
        dictDataDTO = new SysDictDataDTO();
        dictDataDTO.setDictType("gender");
        dictDataDTO.setDictLabel("女");
        dictDataDTO.setDictValue("2");
        dictDataDTO.setDictSort(2);
        dictDataDTO.setStatus(1);
        dictDataDTO.setIsDefault("0");
        dictDataDTO.setListClass("danger");
        dictDataDTO.setRemark("女性");
        
        sysDictDataService.createDictData(dictDataDTO);
        
        // 2. 根据字典类型查询字典数据
        List<SysDictData> dictDataList = sysDictDataService.findByDictType("gender");
        System.out.println("性别字典数据数量: " + dictDataList.size());
        
        // 3. 分页查询字典数据
        IPage<SysDictData> dictDataPage = sysDictDataService.getPageList(1, 10, "gender", "男");
        System.out.println("分页查询字典数据结果: " + dictDataPage.getTotal());
        
        // 4. 根据字典类型和值查询标签
        String label = sysDictDataService.getDictLabel("gender", "1");
        System.out.println("字典值1对应的标签: " + label);
        
        // 5. 根据字典类型和标签查询值
        String value = sysDictDataService.getDictValue("gender", "女");
        System.out.println("字典标签'女'对应的值: " + value);
    }
    
    /**
     * 字典工具类使用示例
     */
    public void dictUtilsExample() {
        // 1. 使用工具类获取字典标签
        String userStatusLabel = DictUtils.getUserStatusLabel("1");
        System.out.println("用户状态1的标签: " + userStatusLabel);
        
        String dataStatusLabel = DictUtils.getDataStatusLabel("0");
        System.out.println("数据状态0的标签: " + dataStatusLabel);
        
        String operationTypeLabel = DictUtils.getOperationTypeLabel("INSERT");
        System.out.println("操作类型INSERT的标签: " + operationTypeLabel);
        
        // 2. 获取字典数据列表
        List<SysDictData> userStatusList = DictUtils.getDictDataList("user_status");
        System.out.println("用户状态字典数据: ");
        userStatusList.forEach(data -> 
            System.out.println("  " + data.getDictLabel() + " -> " + data.getDictValue())
        );
        
        // 3. 通用方法使用
        String customLabel = DictUtils.getDictLabel("gender", "1");
        String customValue = DictUtils.getDictValue("gender", "女");
        System.out.println("自定义字典查询 - 标签: " + customLabel + ", 值: " + customValue);
    }
    
    /**
     * 字典缓存管理示例
     */
    public void dictCacheExample() {
        // 刷新字典类型缓存
        sysDictTypeService.refreshCache();
        
        // 刷新字典数据缓存
        sysDictDataService.refreshCache();
        
        System.out.println("字典缓存已刷新");
    }
    
    /**
     * 字典数据高级操作示例
     */
    public void advancedDictOperations() {
        // 1. 设置默认字典数据
        List<SysDictData> genderList = sysDictDataService.findByDictType("gender");
        if (!genderList.isEmpty()) {
            String firstId = genderList.get(0).getId();
            boolean setDefaultResult = sysDictDataService.setDefault(firstId);
            System.out.println("设置默认字典数据结果: " + setDefaultResult);
        }
        
        // 2. 批量删除字典数据
        List<String> idsToDelete = List.of("id1", "id2", "id3");
        // boolean batchDeleteResult = sysDictDataService.batchDeleteDictData(idsToDelete);
        // System.out.println("批量删除字典数据结果: " + batchDeleteResult);
        
        // 3. 根据字典类型删除所有相关数据
        // boolean deleteByTypeResult = sysDictDataService.deleteByDictType("temp_dict_type");
        // System.out.println("根据类型删除字典数据结果: " + deleteByTypeResult);
    }
}
