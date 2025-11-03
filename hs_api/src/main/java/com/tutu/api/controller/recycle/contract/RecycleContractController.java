package com.tutu.api.controller.recycle.contract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.service.RecycleContractService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收合同控制器
 */
@RestController
@RequestMapping("/recycle/contract")
public class RecycleContractController {

    @Resource
    private RecycleContractService recycleContractService;

    /**
     * 创建回收合同
     * @param request 回收合同信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public BaseResponse<RecycleContract> createContract(@RequestBody RecycleContract request) {
        return BaseResponse.success(recycleContractService.createContract(request));
    }

    /**
     * 根据合同编号查询合同
     * @param no 合同编号
     * @return 合同信息（包含受益人列表）
     */
    @GetMapping("/getByNo/{no}")
    public BaseResponse<RecycleContract> getByNo(@PathVariable String no) {
        return BaseResponse.success(recycleContractService.getContractByNo(no));
    }

    /**
     * 根据 ID 查询回收合同
     * @param id 合同 ID
     * @return 合同信息（包含受益人列表）
     */
    @GetMapping("/{id}")
    public BaseResponse<RecycleContract> getContract(@PathVariable String id) {
        return BaseResponse.success(recycleContractService.getContractById(id));
    }

    /**
     * 查询所有回收合同
     * @return 合同列表（包含受益人列表和名称）
     */
    @GetMapping("/list")
    public BaseResponse<List<RecycleContract>> getAllContracts() {
        return BaseResponse.success(recycleContractService.getAllContractsWithBeneficiaries());
    }

    /**
     * 分页查询回收合同
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果（包含受益人列表和名称）
     */
    @GetMapping("/page")
    public BaseResponse<IPage<RecycleContract>> getContractPage(@RequestParam("pageNum") long pageNum, @RequestParam("pageSize") long pageSize) {
        Page<RecycleContract> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(recycleContractService.getContractPageWithBeneficiaries(page));
    }

    /**
     * 更新回收合同信息
     * @param recycleContract 合同信息（包含受益人列表）
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateContract(@RequestBody RecycleContract recycleContract) {
        recycleContractService.updateContract(recycleContract);
        return BaseResponse.success();
    }

    /**
     * 更新合同状态
     * @param id 合同ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public BaseResponse<Void> updateContractStatus(@PathVariable String id, @RequestParam String status) {
        recycleContractService.updateContractStatus(id, status);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 删除回收合同
     * @param id 合同 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteContract(@PathVariable String id) {
        recycleContractService.removeById(id);
        return BaseResponse.success();
    }
} 