package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import com.tutu.recycle.request.CreateInvoiceRequest;
import com.tutu.recycle.request.UpdateInvoiceRequest;
import com.tutu.recycle.service.RecycleInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 回收发票控制器
 */
@RestController
@RequestMapping("/recycle/invoice")
public class RecycleInvoiceController {
    
    @Autowired
    private RecycleInvoiceService recycleInvoiceService;
    
    /**
     * 创建发票
     * @param request 创建发票请求对象
     * @return 操作结果
     */
    @PostMapping("/create-batch")
    public BaseResponse<Boolean> createInvoice(@RequestBody CreateInvoiceRequest request) {
        try {
            boolean success = recycleInvoiceService.createInvoice(request.getInvoice(), request.getDetails());
            if (success) {
                return BaseResponse.success(true);
            } else {
                return BaseResponse.error("发票创建失败");
            }
        } catch (Exception e) {
            return BaseResponse.error("发票创建异常：" + e.getMessage());
        }
    }
    
    /**
     * 更新发票
     * @param request 更新发票请求对象
     * @return 操作结果
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateInvoice(@RequestBody UpdateInvoiceRequest request) {
        try {
            boolean success = recycleInvoiceService.updateInvoice(request.getInvoice(), request.getDetails());
            if (success) {
                return BaseResponse.success(true);
            } else {
                return BaseResponse.error("发票更新失败");
            }
        } catch (Exception e) {
            return BaseResponse.error("发票更新异常：" + e.getMessage());
        }
    }
    
    /**
     * 删除发票
     * @param invoiceId 发票ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{invoiceId}")
    public BaseResponse<Boolean> deleteInvoice(@PathVariable String invoiceId) {
        try {
            boolean success = recycleInvoiceService.deleteInvoice(invoiceId);
            if (success) {
                return BaseResponse.success(true);
            } else {
                return BaseResponse.error("发票删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error("发票删除异常：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取发票
     * @param invoiceId 发票ID
     * @return 发票信息
     */
    @GetMapping("/get/{invoiceId}")
    public BaseResponse<RecycleInvoice> getInvoiceById(@PathVariable String invoiceId) {
        try {
            RecycleInvoice invoice = recycleInvoiceService.getInvoiceById(invoiceId);
            if (invoice != null) {
                return BaseResponse.success(invoice);
            } else {
                return BaseResponse.error("发票不存在");
            }
        } catch (Exception e) {
            return BaseResponse.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 获取发票明细
     * @param invoiceId 发票ID
     * @return 发票明细列表
     */
    @GetMapping("/details/{invoiceId}")
    public BaseResponse<List<RecycleInvoiceDetail>> getInvoiceDetails(@PathVariable String invoiceId) {
        try {
            List<RecycleInvoiceDetail> details = recycleInvoiceService.getInvoiceDetails(invoiceId);
            return BaseResponse.success(details);
        } catch (Exception e) {
            return BaseResponse.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 分页查询发票
     * @param page 页码
     * @param size 每页大小
     * @param invoiceType 发票类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<RecycleInvoice>> getInvoicePage(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String invoiceType,
                                                       @RequestParam(required = false) String status) {
        try {
            IPage<RecycleInvoice> result = recycleInvoiceService.getInvoicePage(page, size, invoiceType, status);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 根据发票号码查询
     * @param invoiceNo 发票号码
     * @return 发票信息
     */
    @GetMapping("/getByNo/{invoiceNo}")
    public BaseResponse<RecycleInvoice> getInvoiceByNo(@PathVariable String invoiceNo) {
        try {
            RecycleInvoice invoice = recycleInvoiceService.getInvoiceByNo(invoiceNo);
            if (invoice != null) {
                return BaseResponse.success(invoice);
            } else {
                return BaseResponse.error("发票不存在");
            }
        } catch (Exception e) {
            return BaseResponse.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 更新发票状态
     * @param invoiceId 发票ID
     * @param status 新状态
     * @return 操作结果
     */
    @PutMapping("/status/{invoiceId}")
    public BaseResponse<Boolean> updateInvoiceStatus(@PathVariable String invoiceId, 
                                             @RequestParam String status) {
        try {
            boolean success = recycleInvoiceService.updateInvoiceStatus(invoiceId, status);
            if (success) {
                return BaseResponse.success(true);
            } else {
                return BaseResponse.error("状态更新失败");
            }
        } catch (Exception e) {
            return BaseResponse.error("状态更新异常：" + e.getMessage());
        }
    }
    
    /**
     * 设置开票时间
     * @param invoiceId 发票ID
     * @param invoiceTime 开票时间
     * @return 操作结果
     */
    @PutMapping("/setTime/{invoiceId}")
    public BaseResponse<Boolean> setInvoiceTime(@PathVariable String invoiceId, 
                                        @RequestParam Date invoiceTime) {
        try {
            boolean success = recycleInvoiceService.setInvoiceTime(invoiceId, invoiceTime);
            if (success) {
                return BaseResponse.success(true);
            } else {
                return BaseResponse.error("开票时间设置失败");
            }
        } catch (Exception e) {
            return BaseResponse.error("开票时间设置异常：" + e.getMessage());
        }
    }
}