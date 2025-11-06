package com.tutu.api.controller.point;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.point.dto.ExchangePointGoodsDto;
import com.tutu.point.entity.AccountPointUseDetail;
import com.tutu.point.service.AccountPointUseDetailService;
import com.tutu.point.vo.AccountPointUseDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户积分使用详情 Controller
 */
@RestController
@RequestMapping("/point/use-detail")
public class AccountPointUseDetailController {
    
    @Autowired
    private AccountPointUseDetailService accountPointUseDetailService;
    
    /**
     * 创建积分使用详情记录（兑换商品）
     */
    @PostMapping("/create")
    public BaseResponse<AccountPointUseDetail> create(@RequestBody ExchangePointGoodsDto useDetail) {
        return BaseResponse.success(accountPointUseDetailService.createUseDetail(useDetail));
    }
    
    /**
     * 根据ID查询积分使用详情
     */
    @GetMapping("/{id}")
    public BaseResponse<AccountPointUseDetail> getById(@PathVariable String id) {

        return BaseResponse.success(accountPointUseDetailService.getInfoById(id));
    }
    
    /**
     * 根据账户ID查询积分使用详情列表
     */
    @GetMapping("/account/{accountId}")
    public BaseResponse<List<AccountPointUseDetail>> getByAccountId(@PathVariable String accountId) {
        return BaseResponse.success(accountPointUseDetailService.getByAccountId(accountId));
    }
    
    /**
     * 根据账户ID查询已使用的积分详情
     */
    @GetMapping("/account/{accountId}/used")
    public BaseResponse<List<AccountPointUseDetail>> getUsedByAccountId(@PathVariable String accountId) {
        return BaseResponse.success(accountPointUseDetailService.getUsedByAccountId(accountId));
    }
    
    /**
     * 根据账户ID查询未使用的积分详情
     */
    @GetMapping("/account/{accountId}/unused")
    public BaseResponse<List<AccountPointUseDetail>> getUnusedByAccountId(@PathVariable String accountId) {
        return BaseResponse.success(accountPointUseDetailService.getUnusedByAccountId(accountId));
    }
    
    /**
     * 根据兑换码查询积分使用详情
     */
    @GetMapping("/exchange-code/{exchangeCode}")
    public BaseResponse<AccountPointUseDetail> getByExchangeCode(@PathVariable String exchangeCode) {
        return BaseResponse.success(accountPointUseDetailService.getByExchangeCode(exchangeCode));
    }
    
    /**
     * 根据积分商品ID查询积分使用详情
     */
    @GetMapping("/goods/{pointGoodsId}")
    public BaseResponse<List<AccountPointUseDetail>> getByPointGoodsId(@PathVariable String pointGoodsId) {
        return BaseResponse.success(accountPointUseDetailService.getByPointGoodsId(pointGoodsId));
    }
    
    /**
     * 分页查询积分使用详情（带用户名和商品名称）
     */
    @GetMapping("/page")
    public BaseResponse<Page<AccountPointUseDetailVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String pointGoodsId,
            @RequestParam(required = false) Boolean isUsed) {
        return BaseResponse.success(accountPointUseDetailService.pageDetail(page, size, accountId, pointGoodsId, isUsed));
    }
    
    /**
     * 使用兑换码（标记为已使用）
     */
    @PutMapping("/use")
    public BaseResponse<Boolean> useExchangeCode(@RequestBody AccountPointUseDetail detail) {
        return BaseResponse.success(accountPointUseDetailService.useExchangeCode(detail));
    }
    
    /**
     * 删除积分使用详情
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(accountPointUseDetailService.removeById(id));
    }
}

