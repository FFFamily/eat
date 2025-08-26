package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleContractItem;
import com.tutu.recycle.service.RecycleContractItemService;
import com.tutu.recycle.service.RecycleContractService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收合同项目控制器
 */
@RestController
@RequestMapping("/recycle/contract-item")
public class RecycleContractItemController {

    @Resource
    private RecycleContractService recycleContractService;

    @Autowired
    private RecycleContractItemService recycleContractItemService;

    /**
     * 创建回收合同项目
     * @param item 合同项目信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public BaseResponse<Void> createItem(@RequestBody RecycleContractItem item) {
        recycleContractService.createContractItem(item);
        return BaseResponse.success();
    }

    /**
     * 批量创建回收合同项目
     * @param items 合同项目列表
     * @return 创建结果
     */
    @PostMapping("/batch-create")
    public BaseResponse<Boolean> batchCreateItems(@RequestBody List<RecycleContractItem> items) {
        return BaseResponse.success(recycleContractItemService.batchSaveItems(items));
    }

    /**
     * 根据 ID 查询合同项目
     * @param id 项目 ID
     * @return 项目信息
     */
    @GetMapping("/{id}")
    public BaseResponse<RecycleContractItem> getItem(@PathVariable String id) {
        return BaseResponse.success(recycleContractItemService.getById(id));
    }

    /**
     * 根据合同ID查询项目列表
     * @param contractId 合同ID
     * @return 项目列表
     */
    @GetMapping("/get/{contractId}")
    public BaseResponse<List<RecycleContractItem>> getItemsByContractId(@PathVariable String contractId) {
        return BaseResponse.success(recycleContractItemService.getItemsByContractId(contractId));
    }

    /**
     * 查询所有合同项目
     * @return 项目列表
     */
    @GetMapping("/list")
    public BaseResponse<List<RecycleContractItem>> getAllItems() {
        return BaseResponse.success(recycleContractItemService.list());
    }

    /**
     * 分页查询合同项目
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/page")
    public BaseResponse<IPage<RecycleContractItem>> getItemPage(@RequestBody Page<RecycleContractItem> page) {
        return BaseResponse.success(recycleContractItemService.page(page));
    }

    /**
     * 更新合同项目信息
     * @param item 项目信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateItem(@RequestBody RecycleContractItem item) {
        recycleContractService.updateItem(item);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 删除合同项目
     * @param id 项目 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> deleteItem(@PathVariable String id) {
        recycleContractService.removeItem(id);
        return BaseResponse.success();
    }
} 