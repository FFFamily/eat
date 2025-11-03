package com.tutu.api.controller.recycle.contract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleContractBeneficiary;
import com.tutu.recycle.service.RecycleContractBeneficiaryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收合同受益人控制器
 */
@RestController
@RequestMapping("/recycle/contract-beneficiary")
public class RecycleContractBeneficiaryController {

    @Resource
    private RecycleContractBeneficiaryService recycleContractBeneficiaryService;

    /**
     * 创建受益人
     * @param beneficiary 受益人信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public BaseResponse<RecycleContractBeneficiary> createBeneficiary(@RequestBody RecycleContractBeneficiary beneficiary) {
        recycleContractBeneficiaryService.save(beneficiary);
        return BaseResponse.success(beneficiary);
    }

    /**
     * 批量创建受益人
     * @param beneficiaries 受益人列表
     * @return 创建结果
     */
    @PostMapping("/batch-create")
    public BaseResponse<Boolean> batchCreateBeneficiaries(@RequestBody List<RecycleContractBeneficiary> beneficiaries) {
        return BaseResponse.success(recycleContractBeneficiaryService.batchSaveBeneficiaries(beneficiaries));
    }

    /**
     * 根据ID查询受益人
     * @param id 受益人ID
     * @return 受益人信息
     */
    @GetMapping("/{id}")
    public BaseResponse<RecycleContractBeneficiary> getBeneficiary(@PathVariable String id) {
        return BaseResponse.success(recycleContractBeneficiaryService.getById(id));
    }

    /**
     * 根据合同ID查询受益人列表
     * @param contractId 合同ID
     * @return 受益人列表
     */
    @GetMapping("/contract/{contractId}")
    public BaseResponse<List<RecycleContractBeneficiary>> getBeneficiariesByContractId(@PathVariable String contractId) {
        return BaseResponse.success(recycleContractBeneficiaryService.getBeneficiariesByContractId(contractId));
    }

    /**
     * 查询所有受益人
     * @return 受益人列表
     */
    @GetMapping("/list")
    public BaseResponse<List<RecycleContractBeneficiary>> getAllBeneficiaries() {
        return BaseResponse.success(recycleContractBeneficiaryService.list());
    }

    /**
     * 分页查询受益人
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<RecycleContractBeneficiary>> getBeneficiaryPage(
            @RequestParam("pageNum") long pageNum, 
            @RequestParam("pageSize") long pageSize) {
        Page<RecycleContractBeneficiary> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(recycleContractBeneficiaryService.page(page));
    }

    /**
     * 更新受益人信息
     * @param beneficiary 受益人信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateBeneficiary(@RequestBody RecycleContractBeneficiary beneficiary) {
        recycleContractBeneficiaryService.updateById(beneficiary);
        return BaseResponse.success();
    }

    /**
     * 根据ID删除受益人
     * @param id 受益人ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteBeneficiary(@PathVariable String id) {
        recycleContractBeneficiaryService.removeById(id);
        return BaseResponse.success();
    }

    /**
     * 根据合同ID删除所有受益人
     * @param contractId 合同ID
     * @return 删除结果
     */
    @DeleteMapping("/contract/{contractId}")
    public BaseResponse<Void> deleteBeneficiariesByContractId(@PathVariable String contractId) {
        recycleContractBeneficiaryService.removeByContractId(contractId);
        return BaseResponse.success();
    }
}

