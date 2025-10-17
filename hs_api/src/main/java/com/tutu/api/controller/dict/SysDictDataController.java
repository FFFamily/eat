package com.tutu.api.controller.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.system.dto.SysDictDataDTO;
import com.tutu.system.entity.dict.SysDictData;
import com.tutu.system.service.SysDictDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统字典数据控制器
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {
    
    @Autowired
    private SysDictDataService sysDictDataService;
    
    /**
     * 分页查询字典数据列表
     * @param current 当前页，默认为1
     * @param size 每页大小，默认为10
     * @param dictType 字典类型（可选）
     * @param keyword 关键字（可选）
     * @return 分页的字典数据列表
     */
    @GetMapping("/page")
    public BaseResponse<IPage<SysDictData>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String keyword) {
        IPage<SysDictData> page = sysDictDataService.getPageList(current, size, dictType, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 根据字典类型查询字典数据
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @GetMapping("/type/{dictType}")
    public BaseResponse<List<SysDictData>> findByDictType(@PathVariable String dictType) {
        List<SysDictData> dictDataList = sysDictDataService.findByDictType(dictType);
        return BaseResponse.success(dictDataList);
    }
    
    /**
     * 根据ID查询字典数据详情
     * @param id 字典数据ID
     * @return 字典数据详情
     */
    @GetMapping("/{id}")
    public BaseResponse<SysDictData> getById(@PathVariable String id) {
        SysDictData dictData = sysDictDataService.getById(id);
        if (dictData == null) {
            return BaseResponse.error("字典数据不存在");
        }
        return BaseResponse.success(dictData);
    }
    
    /**
     * 根据字典类型和字典值查询字典标签
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    @GetMapping("/label")
    public BaseResponse<String> getDictLabel(
            @RequestParam String dictType,
            @RequestParam String dictValue) {
        String dictLabel = sysDictDataService.getDictLabel(dictType, dictValue);
        return BaseResponse.success(dictLabel);
    }
    
    /**
     * 根据字典类型和字典标签查询字典值
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    @GetMapping("/value")
    public BaseResponse<String> getDictValue(
            @RequestParam String dictType,
            @RequestParam String dictLabel) {
        String dictValue = sysDictDataService.getDictValue(dictType, dictLabel);
        return BaseResponse.success(dictValue);
    }
    
    /**
     * 创建字典数据
     * @param dictDataDTO 字典数据DTO
     * @return 创建结果
     */
    @PostMapping("/create")
    public BaseResponse<String> createDictData(@Valid @RequestBody SysDictDataDTO dictDataDTO) {
        try {
            boolean result = sysDictDataService.createDictData(dictDataDTO);
            if (result) {
                return BaseResponse.success("创建成功");
            } else {
                return BaseResponse.error("创建失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新字典数据
     * @param dictDataDTO 字典数据DTO
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<String> updateDictData(@Valid @RequestBody SysDictDataDTO dictDataDTO) {
        try {
            boolean result = sysDictDataService.updateDictData(dictDataDTO);
            if (result) {
                return BaseResponse.success("更新成功");
            } else {
                return BaseResponse.error("更新失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除字典数据
     * @param id 字典数据ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteDictData(@PathVariable String id) {
        try {
            boolean result = sysDictDataService.deleteDictData(id);
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除字典数据
     * @param ids ID列表
     * @return 批量删除结果
     */
    @DeleteMapping("/batch")
    public BaseResponse<String> batchDeleteDictData(@RequestBody List<String> ids) {
        try {
            boolean result = sysDictDataService.batchDeleteDictData(ids);
            if (result) {
                return BaseResponse.success("批量删除成功");
            } else {
                return BaseResponse.error("批量删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 设置默认字典数据
     * @param id 字典数据ID
     * @return 设置结果
     */
    @PutMapping("/{id}/default")
    public BaseResponse<String> setDefault(@PathVariable String id) {
        try {
            boolean result = sysDictDataService.setDefault(id);
            if (result) {
                return BaseResponse.success("设置成功");
            } else {
                return BaseResponse.error("设置失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}

