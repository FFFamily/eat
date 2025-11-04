package com.tutu.api.controller.point;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.point.entity.AccountPointDetail;
import com.tutu.point.service.AccountPointDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户积分详情 Controller
 */
@RestController
@RequestMapping("/point/detail")
public class AccountPointDetailController {
    
    @Autowired
    private AccountPointDetailService accountPointDetailService;
    
    /**
     * 创建积分详情记录
     */
    @PostMapping("/create")
    public BaseResponse<AccountPointDetail> create(@RequestBody AccountPointDetail detail) {
        return BaseResponse.success(accountPointDetailService.createDetail(detail));
    }
    
    /**
     * 根据ID查询积分详情
     */
    @GetMapping("/get/{id}")
    public BaseResponse<AccountPointDetail> getById(@PathVariable String id) {
        return BaseResponse.success(accountPointDetailService.getById(id));
    }
    
    /**
     * 根据账户ID查询积分详情列表
     */
    @GetMapping("/account/{accountId}")
    public BaseResponse<List<AccountPointDetail>> getByAccountId(@PathVariable String accountId) {
        return BaseResponse.success(accountPointDetailService.getByAccountId(accountId));
    }
    
    /**
     * 根据账户ID和变更类型查询积分详情
     */
    @GetMapping("/account/{accountId}/type/{changeType}")
    public BaseResponse<List<AccountPointDetail>> getByAccountIdAndChangeType(
            @PathVariable String accountId,
            @PathVariable String changeType) {
        return BaseResponse.success(accountPointDetailService.getByAccountIdAndChangeType(accountId, changeType));
    }
    
    /**
     * 分页查询积分详情
     */
    @GetMapping("/page")
    public BaseResponse<Page<AccountPointDetail>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String changeDirection) {
        return BaseResponse.success(accountPointDetailService.pageDetail(page, size, accountId, changeType, changeDirection));
    }
    
    /**
     * 删除积分详情
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(accountPointDetailService.removeById(id));
    }
}

