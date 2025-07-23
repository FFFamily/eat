package com.tutu.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.OperationLog;
import com.tutu.common.enums.OperationType;
import com.tutu.system.dto.SysDictTypeDTO;
import com.tutu.system.entity.SysDictType;
import com.tutu.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 字典类型控制器
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {
    
    @Autowired
    private SysDictTypeService sysDictTypeService;
    
    /**
     * 分页查询字典类型列表
     */
    @GetMapping("/page")
    @OperationLog(value = "查询字典类型列表", type = OperationType.SELECT)
    public BaseResponse<IPage<SysDictType>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<SysDictType> page = sysDictTypeService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询所有启用的字典类型
     */
    @GetMapping("/all")
    @OperationLog(value = "查询所有字典类型", type = OperationType.SELECT)
    public BaseResponse<List<SysDictType>> findAllEnabled() {
        List<SysDictType> dictTypes = sysDictTypeService.findAllEnabled();
        return BaseResponse.success(dictTypes);
    }
    
    /**
     * 根据ID查询字典类型详情
     */
    @GetMapping("/{id}")
    @OperationLog(value = "查询字典类型详情", type = OperationType.SELECT)
    public BaseResponse<SysDictType> getById(@PathVariable String id) {
        SysDictType dictType = sysDictTypeService.getById(id);
        if (dictType == null) {
            return BaseResponse.error("字典类型不存在");
        }
        return BaseResponse.success(dictType);
    }
    
    /**
     * 根据字典类型查询字典类型信息
     */
    @GetMapping("/info/{dictType}")
    @OperationLog(value = "根据类型查询字典", type = OperationType.SELECT)
    public BaseResponse<SysDictType> getByDictType(@PathVariable String dictType) {
        SysDictType sysDictType = sysDictTypeService.findByDictType(dictType);
        if (sysDictType == null) {
            return BaseResponse.error("字典类型不存在");
        }
        return BaseResponse.success(sysDictType);
    }
    
    /**
     * 创建字典类型
     */
    @PostMapping
    @OperationLog(value = "创建字典类型", type = OperationType.INSERT, recordParams = true)
    public BaseResponse<String> createDictType(@Valid @RequestBody SysDictTypeDTO dictTypeDTO) {
        try {
            boolean result = sysDictTypeService.createDictType(dictTypeDTO);
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
     * 更新字典类型
     */
    @PutMapping
    @OperationLog(value = "更新字典类型", type = OperationType.UPDATE, recordParams = true)
    public BaseResponse<String> updateDictType(@Valid @RequestBody SysDictTypeDTO dictTypeDTO) {
        try {
            boolean result = sysDictTypeService.updateDictType(dictTypeDTO);
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
     * 删除字典类型
     */
    @DeleteMapping("/{id}")
    @OperationLog(value = "删除字典类型", type = OperationType.DELETE, recordParams = true)
    public BaseResponse<String> deleteDictType(@PathVariable String id) {
        try {
            boolean result = sysDictTypeService.deleteDictType(id);
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
     * 批量删除字典类型
     */
    @DeleteMapping("/batch")
    @OperationLog(value = "批量删除字典类型", type = OperationType.DELETE, recordParams = true)
    public BaseResponse<String> batchDeleteDictTypes(@RequestBody List<String> ids) {
        try {
            boolean result = sysDictTypeService.batchDeleteDictTypes(ids);
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
     * 刷新字典缓存
     */
    @PostMapping("/refresh")
    @OperationLog(value = "刷新字典缓存", type = OperationType.OTHER)
    public BaseResponse<String> refreshCache() {
        try {
            sysDictTypeService.refreshCache();
            return BaseResponse.success("缓存刷新成功");
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}
