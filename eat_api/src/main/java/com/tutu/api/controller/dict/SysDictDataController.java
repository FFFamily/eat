package com.tutu.api.controller.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.OperationLog;
import com.tutu.common.enums.OperationType;
import com.tutu.system.dto.SysDictDataDTO;
import com.tutu.system.entity.dict.SysDictData;
import com.tutu.system.service.SysDictDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {
    
    @Autowired
    private SysDictDataService sysDictDataService;
    
    /**
     * 分页查询字典数据列表
     */
    @GetMapping("/page")
    @OperationLog(value = "查询字典数据列表", type = OperationType.SELECT)
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
     */
    @GetMapping("/type/{dictType}")
    @OperationLog(value = "根据类型查询字典数据", type = OperationType.SELECT)
    public BaseResponse<List<SysDictData>> findByDictType(@PathVariable String dictType) {
        List<SysDictData> dictDataList = sysDictDataService.findByDictType(dictType);
        return BaseResponse.success(dictDataList);
    }
    
    /**
     * 根据ID查询字典数据详情
     */
    @GetMapping("/{id}")
    @OperationLog(value = "查询字典数据详情", type = OperationType.SELECT)
    public BaseResponse<SysDictData> getById(@PathVariable String id) {
        SysDictData dictData = sysDictDataService.getById(id);
        if (dictData == null) {
            return BaseResponse.error("字典数据不存在");
        }
        return BaseResponse.success(dictData);
    }
    
    /**
     * 根据字典类型和字典值查询字典标签
     */
    @GetMapping("/label")
    @OperationLog(value = "查询字典标签", type = OperationType.SELECT)
    public BaseResponse<String> getDictLabel(
            @RequestParam String dictType,
            @RequestParam String dictValue) {
        String dictLabel = sysDictDataService.getDictLabel(dictType, dictValue);
        return BaseResponse.success(dictLabel);
    }
    
    /**
     * 根据字典类型和字典标签查询字典值
     */
    @GetMapping("/value")
    @OperationLog(value = "查询字典值", type = OperationType.SELECT)
    public BaseResponse<String> getDictValue(
            @RequestParam String dictType,
            @RequestParam String dictLabel) {
        String dictValue = sysDictDataService.getDictValue(dictType, dictLabel);
        return BaseResponse.success(dictValue);
    }
    
    /**
     * 创建字典数据
     */
    @PostMapping
    @OperationLog(value = "创建字典数据", type = OperationType.INSERT, recordParams = true)
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
     */
    @PutMapping
    @OperationLog(value = "更新字典数据", type = OperationType.UPDATE, recordParams = true)
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
     */
    @DeleteMapping("/{id}")
    @OperationLog(value = "删除字典数据", type = OperationType.DELETE, recordParams = true)
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
     */
    @DeleteMapping("/batch")
    @OperationLog(value = "批量删除字典数据", type = OperationType.DELETE, recordParams = true)
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
     */
    @PutMapping("/{id}/default")
    @OperationLog(value = "设置默认字典数据", type = OperationType.UPDATE, recordParams = true)
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
    
    /**
     * 刷新字典缓存
     */
    @PostMapping("/refresh")
    @OperationLog(value = "刷新字典数据缓存", type = OperationType.OTHER)
    public BaseResponse<String> refreshCache() {
        try {
            sysDictDataService.refreshCache();
            return BaseResponse.success("缓存刷新成功");
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}
