package com.tutu.api.controller.invoice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.invoice.entity.InvoiceHead;
import com.tutu.invoice.service.InvoiceHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice-head")
public class InvoiceHeadController {

    @Autowired
    private InvoiceHeadService invoiceHeadService;

    /**
     * 添加发票抬头
     * @param invoiceHead 发票抬头信息
     * @return 添加结果
     */
    @PostMapping
    public BaseResponse<Boolean> addInvoiceHead(@RequestBody InvoiceHead invoiceHead) {
        boolean result = invoiceHeadService.addInvoiceHead(invoiceHead);
        return result ? BaseResponse.success(true) : BaseResponse.error("添加发票抬头失败");
    }

    /**
     * 根据 ID 删除发票抬头
     * @param id 发票抬头 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteInvoiceHead(@PathVariable String id) {
        boolean result = invoiceHeadService.deleteInvoiceHead(id);
        return result ? BaseResponse.success(true) : BaseResponse.error("删除发票抬头失败");
    }

    /**
     * 更新发票抬头信息
     * @param invoiceHead 发票抬头信息
     * @return 更新结果
     */
    @PutMapping
    public BaseResponse<Boolean> updateInvoiceHead(@RequestBody InvoiceHead invoiceHead) {
        boolean result = invoiceHeadService.updateInvoiceHead(invoiceHead);
        return result ? BaseResponse.success(true) : BaseResponse.error("更新发票抬头失败");
    }

    /**
     * 根据 ID 查询发票抬头
     * @param id 发票抬头 ID
     * @return 发票抬头信息
     */
    @GetMapping("/{id}")
    public BaseResponse<InvoiceHead> getInvoiceHead(@PathVariable String id) {
        InvoiceHead invoiceHead = invoiceHeadService.getById(id);
        return invoiceHead != null ? BaseResponse.success(invoiceHead) : BaseResponse.error("发票抬头不存在");
    }

    /**
     * 根据账号ID查询发票抬头列表
     * @param accountId 账号ID
     * @return 发票抬头列表
     */
    @GetMapping("/account/{accountId}")
    public BaseResponse<List<InvoiceHead>> getInvoiceHeadsByAccount(@PathVariable String accountId) {
        List<InvoiceHead> invoiceHeads = invoiceHeadService.getByAccountId(accountId);
        return BaseResponse.success(invoiceHeads);
    }

    /**
     * 根据账号ID查询默认发票抬头
     * @param accountId 账号ID
     * @return 默认发票抬头
     */
    @GetMapping("/account/{accountId}/default")
    public BaseResponse<InvoiceHead> getDefaultInvoiceHead(@PathVariable String accountId) {
        InvoiceHead defaultHead = invoiceHeadService.getDefaultByAccountId(accountId);
        return defaultHead != null ? BaseResponse.success(defaultHead) : BaseResponse.error("未找到默认发票抬头");
    }

    /**
     * 设置默认发票抬头
     * @param accountId 账号ID
     * @param invoiceHeadId 发票抬头ID
     * @return 设置结果
     */
    @PutMapping("/account/{accountId}/default/{invoiceHeadId}")
    public BaseResponse<Boolean> setDefaultInvoiceHead(@PathVariable String accountId, 
                                                      @PathVariable String invoiceHeadId) {
        boolean result = invoiceHeadService.setDefaultInvoiceHead(accountId, invoiceHeadId);
        return result ? BaseResponse.success(true) : BaseResponse.error("设置默认发票抬头失败");
    }

    /**
     * 查询所有发票抬头
     * @return 发票抬头列表
     */
    @GetMapping
    public BaseResponse<List<InvoiceHead>> getAllInvoiceHeads() {
        List<InvoiceHead> invoiceHeads = invoiceHeadService.list();
        return BaseResponse.success(invoiceHeads);
    }

    /**
     * 分页查询发票抬头
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<InvoiceHead>> getInvoiceHeadsByPage(@RequestParam(defaultValue = "1") int pageNum,
                                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                                  InvoiceHead invoiceHead) {
        List<InvoiceHead> list = invoiceHeadService.findPage(pageNum, pageSize,  invoiceHead);
        IPage<InvoiceHead> result = new Page<>();
        result.setRecords(list);
        result.setTotal(invoiceHeadService.findPageCount(invoiceHead));
        return BaseResponse.success(result);
    }

    /**
     * 根据账号ID分页查询发票抬头
     * @param accountId 账号ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/account/{accountId}/page")
    public BaseResponse<IPage<InvoiceHead>> getInvoiceHeadsByAccountAndPage(@PathVariable String accountId,
                                                                           @RequestParam(defaultValue = "1") int pageNum,
                                                                           @RequestParam(defaultValue = "10") int pageSize) {
        Page<InvoiceHead> page = new Page<>(pageNum, pageSize);
        QueryWrapper<InvoiceHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId)
                   .orderByDesc("is_default")
                   .orderByDesc("create_time");
        IPage<InvoiceHead> result = invoiceHeadService.page(page, queryWrapper);
        return BaseResponse.success(result);
    }
}
