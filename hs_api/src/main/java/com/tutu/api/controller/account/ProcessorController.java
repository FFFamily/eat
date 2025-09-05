package com.tutu.api.controller.account;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Processor;
import com.tutu.user.service.ProcessorService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 经办人 Controller
 */
@RestController
@RequestMapping("/processor")
public class ProcessorController {
    
    @Autowired
    private ProcessorService processorService;
    
    /**
     * 新增经办人
     */
    @PostMapping
    public BaseResponse<Boolean> save(@RequestBody Processor processor) {
        return BaseResponse.success(processorService.save(processor));
    }
    
    /**
     * 修改经办人
     */
    @PutMapping
    public BaseResponse<Boolean> update(@RequestBody Processor processor) {
        return BaseResponse.success(processorService.updateById(processor));
    }
    
    /**
     * 删除经办人
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(processorService.removeById(id));
    }
    
    /**
     * 根据ID查询经办人
     */
    @GetMapping("/{id}")
    public BaseResponse<Processor> getById(@PathVariable String id) {
        return BaseResponse.success(processorService.getById(id));
    }
    
    /**
     * 分页查询经办人列表
     */
    @GetMapping("/page")
    public BaseResponse<Page<Processor>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Processor processor) {
        List<Processor> list = processorService.findPage(page, size, processor);
        Page<Processor> result = new Page<>(page, size);
        result.setRecords(list);
        long count = processorService.findPageCount(processor);
        result.setTotal(count);
        return BaseResponse.success(result);
    }
}