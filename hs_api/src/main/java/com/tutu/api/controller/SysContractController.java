package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.system.entity.SysContract;
import com.tutu.system.service.SysContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/contract")
public class SysContractController {

    @Autowired
    private SysContractService sysContractService;

    /**
     * 添加系统合同
     * @param sysContract 系统合同信息
     * @return 添加结果
     */
    @PostMapping
    public BaseResponse<Void> addSysContract(@RequestBody SysContract sysContract) {
        sysContractService.save(sysContract);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 删除系统合同
     * @param id 系统合同 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteSysContract(@PathVariable String id) {
        sysContractService.removeById(id);
        return BaseResponse.success();
    }

    /**
     * 更新系统合同信息
     * @param sysContract 系统合同信息
     * @return 更新结果
     */
    @PutMapping
    public  BaseResponse<Void> updateSysContract(@RequestBody SysContract sysContract) {
        sysContractService.updateById(sysContract);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 查询系统合同
     * @param id 系统合同 ID
     * @return 系统合同信息
     */
    @GetMapping("/{id}")
    public BaseResponse<SysContract> getSysContract(@PathVariable String id) {
        return BaseResponse.success(sysContractService.getById(id));
    }

    /**
     * 查询所有系统合同
     * @return 系统合同列表
     */
    @GetMapping
    public BaseResponse<List<SysContract>>  getAllSysContracts() {
        return BaseResponse.success(sysContractService.list());
    }

    /**
     * 分页查询系统合同
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<SysContract>> getSysContractsByPage(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysContract> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(sysContractService.page(page, new QueryWrapper<>()));
    }
}
