package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.system.entity.SysContractItem;
import com.tutu.system.service.SysContractItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合同子项Controller
 */
@RestController
@RequestMapping("/system/contract-item")
public class SysContractItemController {

    @Autowired
    private SysContractItemService sysContractItemService;

    /**
     * 新增合同子项
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody SysContractItem contractItem) {
        boolean result = sysContractItemService.save(contractItem);
        return BaseResponse.success(result);
    }

    /**
     * 修改合同子项
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody SysContractItem contractItem) {
        if (contractItem.getId() == null) {
            return BaseResponse.error("ID不能为空");
        }
        boolean result = sysContractItemService.updateById(contractItem);
        return BaseResponse.success(result);
    }

    /**
     * 删除合同子项
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        boolean result = sysContractItemService.removeById(id);
        return BaseResponse.success(result);
    }

    /**
     * 根据ID查询合同子项
     */
    @GetMapping("/get/{id}")
    public BaseResponse<SysContractItem> getById(@PathVariable String id) {
        SysContractItem contractItem = sysContractItemService.getById(id);
        return BaseResponse.success(contractItem);
    }

    /**
     * 分页查询合同子项
     */
    @GetMapping("/page")
    public BaseResponse<IPage<SysContractItem>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            SysContractItem contractItem) {
        Page<SysContractItem> page = new Page<>(pageNum, pageSize);
        IPage<SysContractItem> result = sysContractItemService.getPage(page, contractItem);
        return BaseResponse.success(result);
    }

    /**
     * 根据合同ID查询合同子项列表
     */
    @GetMapping("/list/contract/{contractId}")
    public BaseResponse<List<SysContractItem>> getByContractId(@PathVariable String contractId) {
        List<SysContractItem> list = sysContractItemService.getByContractId(contractId);
        return BaseResponse.success(list);
    }

    /**
     * 批量保存合同子项
     */
    @PostMapping("/batch-save")
    public BaseResponse<Boolean> batchSave(@RequestBody List<SysContractItem> contractItems) {
        boolean result = sysContractItemService.saveBatch(contractItems);
        return BaseResponse.success(result);
    }

    /**
     * 根据合同ID删除合同子项
     */
    @DeleteMapping("/delete/contract/{contractId}")
    public BaseResponse<Boolean> deleteByContractId(@PathVariable String contractId) {
        boolean result = sysContractItemService.deleteByContractId(contractId);
        return BaseResponse.success(result);
    }

    /**
     * 根据回收货物ID查询合同子项
     */
    @GetMapping("/list/recycle-good/{recycleGoodId}")
    public BaseResponse<List<SysContractItem>> getByRecycleGoodId(@PathVariable String recycleGoodId) {
        List<SysContractItem> list = sysContractItemService.getByRecycleGoodId(recycleGoodId);
        return BaseResponse.success(list);
    }

    /**
     * 根据租赁设备ID查询合同子项
     */
    @GetMapping("/list/lease-good/{leaseGoodId}")
    public BaseResponse<List<SysContractItem>> getByLeaseGoodId(@PathVariable String leaseGoodId) {
        List<SysContractItem> list = sysContractItemService.getByLeaseGoodId(leaseGoodId);
        return BaseResponse.success(list);
    }

    /**
     * 根据合同ID统计合同子项数量
     */
    @GetMapping("/count/contract/{contractId}")
    public BaseResponse<Long> countByContractId(@PathVariable String contractId) {
        long count = sysContractItemService.countByContractId(contractId);
        return BaseResponse.success(count);
    }

    /**
     * 根据合同ID获取回收货物列表
     */
    @GetMapping("/recycle-goods/contract/{contractId}")
    public BaseResponse<List<SysContractItem>> getRecycleGoodsByContractId(@PathVariable String contractId) {
        List<SysContractItem> list = sysContractItemService.getRecycleGoodsByContractId(contractId);
        return BaseResponse.success(list);
    }

    /**
     * 根据合同ID获取租赁设备列表
     */
    @GetMapping("/lease-goods/contract/{contractId}")
    public BaseResponse<List<SysContractItem>> getLeaseGoodsByContractId(@PathVariable String contractId) {
        List<SysContractItem> list = sysContractItemService.getLeaseGoodsByContractId(contractId);
        return BaseResponse.success(list);
    }

    /**
     * 获取所有合同子项列表
     */
    @GetMapping("/list")
    public BaseResponse<List<SysContractItem>> getAll() {
        List<SysContractItem> list = sysContractItemService.list();
        return BaseResponse.success(list);
    }
} 