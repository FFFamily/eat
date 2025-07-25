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
    public BaseResponse<Void> addInvoiceHead(@RequestBody InvoiceHead invoiceHead) {
        invoiceHeadService.save(invoiceHead);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 删除发票抬头
     * @param id 发票抬头 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public boolean deleteInvoiceHead(@PathVariable String id) {
        return invoiceHeadService.removeById(id);
    }

    /**
     * 更新发票抬头信息
     * @param invoiceHead 发票抬头信息
     * @return 更新结果
     */
    @PutMapping
    public boolean updateInvoiceHead(@RequestBody InvoiceHead invoiceHead) {
        return invoiceHeadService.updateById(invoiceHead);
    }

    /**
     * 根据 ID 查询发票抬头
     * @param id 发票抬头 ID
     * @return 发票抬头信息
     */
    @GetMapping("/{id}")
    public InvoiceHead getInvoiceHead(@PathVariable String id) {
        return invoiceHeadService.getById(id);
    }

    /**
     * 查询所有发票抬头
     * @return 发票抬头列表
     */
    @GetMapping
    public List<InvoiceHead> getAllInvoiceHeads() {
        return invoiceHeadService.list();
    }

    /**
     * 分页查询发票抬头
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/page")
    public IPage<InvoiceHead> getInvoiceHeadsByPage(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        Page<InvoiceHead> page = new Page<>(pageNum, pageSize);
        return invoiceHeadService.page(page, new QueryWrapper<>());
    }
}
