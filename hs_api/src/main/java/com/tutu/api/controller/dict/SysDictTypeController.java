package com.tutu.api.controller.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.system.dto.SysDictTypeDTO;
import com.tutu.system.entity.dict.SysDictType;
import com.tutu.system.service.SysDictTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统字典类型控制器
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {
    
    @Autowired
    private SysDictTypeService sysDictTypeService;
    
    /**
     * 分页查询字典类型列表
     * @param current 当前页，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 关键字（可选）
     * @return 分页的字典类型列表
     */
    @GetMapping("/page")
    public BaseResponse<IPage<SysDictType>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<SysDictType> page = sysDictTypeService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询所有启用的字典类型
     * @return 字典类型列表
     */
    @GetMapping("/all")
    public BaseResponse<List<SysDictType>> findAllEnabled() {
        List<SysDictType> dictTypes = sysDictTypeService.findAllEnabled();
        return BaseResponse.success(dictTypes);
    }
    
    /**
     * 根据ID查询字典类型详情
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    @GetMapping("/{id}")
    public BaseResponse<SysDictType> getById(@PathVariable String id) {
        SysDictType dictType = sysDictTypeService.getById(id);
        if (dictType == null) {
            return BaseResponse.error("字典类型不存在");
        }
        return BaseResponse.success(dictType);
    }
    
    /**
     * 根据字典类型查询字典类型信息
     * @param dictType 字典类型
     * @return 字典类型信息
     */
    @GetMapping("/info/{dictType}")
    public BaseResponse<SysDictType> getByDictType(@PathVariable String dictType) {
        SysDictType sysDictType = sysDictTypeService.findByDictType(dictType);
        if (sysDictType == null) {
            return BaseResponse.error("字典类型不存在");
        }
        return BaseResponse.success(sysDictType);
    }
    
    /**
     * 创建字典类型
     * @param dictTypeDTO 字典类型DTO
     * @return 创建结果
     */
    @PostMapping("/create")
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
     * @param dictTypeDTO 字典类型DTO
     * @return 更新结果
     */
    @PutMapping
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
     * @param id 字典类型ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
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
     * @param ids ID列表
     * @return 批量删除结果
     */
    @DeleteMapping("/batch")
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
}

