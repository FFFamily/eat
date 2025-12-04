package com.tutu.api.controller;

import com.tutu.common.Response.BaseResponse;
import com.tutu.system.dto.ApplicationPdfDto;
import com.tutu.system.service.PdfGenerateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * PDF生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pdf")
public class PdfGenerateController {
    
    @Autowired
    private PdfGenerateService pdfGenerateService;
    
    /**
     * 生成业务申请单PDF（使用Mock数据）
     * 
     * @param orderId 订单ID
     * @return PDF文件URL
     */
    @GetMapping("/application/mock/{orderId}")
    public BaseResponse<String> generateMockApplicationPdf(@PathVariable String orderId) {
        try {
            // 方案2：直接走纯 OpenPDF 实现，保持原有URL不变
            String pdfUrl = pdfGenerateService.generateMockApplicationPdfWithOpenPdf(orderId);
            return BaseResponse.success(pdfUrl);
        } catch (Exception e) {
            log.error("生成业务申请单PDF失败, orderId: {}", orderId, e);
            return BaseResponse.error("生成PDF失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成业务申请单PDF（使用实际数据，纯OpenPDF方式）
     * 
     * @param orderData 订单数据
     * @return PDF文件URL
     */
    @PostMapping("/application/generate")
    public BaseResponse<String> generateApplicationPdf(@RequestBody ApplicationPdfDto orderData) {
        try {
            // 方案2：直接走纯 OpenPDF 实现，保持原有URL不变
            String pdfUrl = pdfGenerateService.generateApplicationPdfWithOpenPdf(orderData);
            return BaseResponse.success(pdfUrl);
        } catch (Exception e) {
            log.error("生成业务申请单PDF失败, orderId: {}", orderData.getOrderId(), e);
            return BaseResponse.error("生成PDF失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成业务申请单PDF（使用Mock数据，纯OpenPDF方式）
     * 
     * @param orderId 订单ID
     * @return PDF文件URL
     */
    @GetMapping("/application/openpdf/mock/{orderId}")
    public BaseResponse<String> generateMockApplicationPdfWithOpenPdf(@PathVariable String orderId) {
        try {
            String pdfUrl = pdfGenerateService.generateMockApplicationPdfWithOpenPdf(orderId);
            return BaseResponse.success(pdfUrl);
        } catch (Exception e) {
            log.error("使用OpenPDF生成业务申请单PDF失败, orderId: {}", orderId, e);
            return BaseResponse.error("生成PDF失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成业务申请单PDF（使用实际数据，纯OpenPDF方式）
     * 
     * @param orderData 订单数据
     * @return PDF文件URL
     */
    @PostMapping("/application/openpdf/generate")
    public BaseResponse<String> generateApplicationPdfWithOpenPdf(@RequestBody ApplicationPdfDto orderData) {
        try {
            String pdfUrl = pdfGenerateService.generateApplicationPdfWithOpenPdf(orderData);
            return BaseResponse.success(pdfUrl);
        } catch (Exception e) {
            log.error("使用OpenPDF生成业务申请单PDF失败, orderId: {}", orderData.getOrderId(), e);
            return BaseResponse.error("生成PDF失败: " + e.getMessage());
        }
    }
}

