package com.tutu.system.service;

import com.tutu.system.config.FileUploadConfig;
import com.tutu.system.dto.ApplicationPdfDto;
import com.tutu.system.utils.FileUtils;
import com.tutu.system.utils.OpenPdfUtils;
import com.tutu.system.utils.PdfUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * PDF生成服务
 */
@Slf4j
@Service
public class PdfGenerateService {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * 生成交付单PDF
     * 
     * @param orderId 订单ID
     * @param orderData 订单数据
     * @return PDF文件URL
     */
    public String generateDeliveryNotePdf(String orderId, Map<String, Object> orderData) {
        try {
            // 构建PDF内容
            List<String> content = new ArrayList<>();
            content.add("交付单编号: " + orderData.getOrDefault("orderNo", orderId));
            content.add("交付日期: " + orderData.getOrDefault("deliveryDate", ""));
            content.add("客户名称: " + orderData.getOrDefault("customerName", ""));
            content.add("交付地址: " + orderData.getOrDefault("deliveryAddress", ""));
            content.add("联系电话: " + orderData.getOrDefault("contactPhone", ""));
            content.add("备注: " + orderData.getOrDefault("remark", ""));

            // 生成PDF字节数组
            byte[] pdfBytes = PdfUtils.generatePdfToBytes("交付单", content);

            // 保存PDF文件
            String fileName = "delivery_note_" + orderId + "_" + System.currentTimeMillis() + ".pdf";
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
            FileUtils.createDirectories(storePath);
            String fullPath = storePath + fileName;

            if (PdfUtils.savePdfToFile(pdfBytes, fullPath)) {
                // 生成访问URL
                String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                return fileUploadConfig.getUrlPrefix() + relativePath;
            }

            throw new RuntimeException("PDF文件保存失败");
        } catch (Exception e) {
            log.error("生成交付单PDF失败, orderId: {}", orderId, e);
            throw new RuntimeException("生成交付单PDF失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成结算单PDF
     * 
     * @param orderId 订单ID
     * @param orderData 订单数据
     * @return PDF文件URL
     */
    public String generateSettlementPdf(String orderId, Map<String, Object> orderData) {
        try {
            // 构建PDF内容
            List<String> content = new ArrayList<>();
            content.add("结算单编号: " + orderData.getOrDefault("orderNo", orderId));
            content.add("结算日期: " + orderData.getOrDefault("settlementDate", ""));
            content.add("客户名称: " + orderData.getOrDefault("customerName", ""));
            content.add("结算金额: ¥" + orderData.getOrDefault("totalAmount", "0.00"));
            content.add("支付方式: " + orderData.getOrDefault("paymentMethod", ""));
            content.add("备注: " + orderData.getOrDefault("remark", ""));

            // 生成PDF字节数组
            byte[] pdfBytes = PdfUtils.generatePdfToBytes("结算单", content);

            // 保存PDF文件
            String fileName = "settlement_" + orderId + "_" + System.currentTimeMillis() + ".pdf";
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
            FileUtils.createDirectories(storePath);
            String fullPath = storePath + fileName;

            if (PdfUtils.savePdfToFile(pdfBytes, fullPath)) {
                // 生成访问URL
                String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                return fileUploadConfig.getUrlPrefix() + relativePath;
            }

            throw new RuntimeException("PDF文件保存失败");
        } catch (Exception e) {
            log.error("生成结算单PDF失败, orderId: {}", orderId, e);
            throw new RuntimeException("生成结算单PDF失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成申请单PDF
     * 
     * @param orderId 订单ID
     * @param orderData 订单数据
     * @return PDF文件URL
     */
    public String generateApplicationPdf(String orderId, Map<String, Object> orderData) {
        try {
            // 构建PDF内容
            List<String> content = new ArrayList<>();
            content.add("申请单编号: " + orderData.getOrDefault("orderNo", orderId));
            content.add("申请日期: " + orderData.getOrDefault("applicationDate", ""));
            content.add("申请人: " + orderData.getOrDefault("applicantName", ""));
            content.add("申请内容: " + orderData.getOrDefault("applicationContent", ""));
            content.add("备注: " + orderData.getOrDefault("remark", ""));

            // 生成PDF字节数组
            byte[] pdfBytes = PdfUtils.generatePdfToBytes("申请单", content);

            // 保存PDF文件
            String fileName = "application_" + orderId + "_" + System.currentTimeMillis() + ".pdf";
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
            FileUtils.createDirectories(storePath);
            String fullPath = storePath + fileName;

            if (PdfUtils.savePdfToFile(pdfBytes, fullPath)) {
                // 生成访问URL
                String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                return fileUploadConfig.getUrlPrefix() + relativePath;
            }

            throw new RuntimeException("PDF文件保存失败");
        } catch (Exception e) {
            log.error("生成申请单PDF失败, orderId: {}", orderId, e);
            throw new RuntimeException("生成申请单PDF失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成带表格的PDF（通用方法）
     * 
     * @param title PDF标题
     * @param headers 表头
     * @param data 表格数据
     * @param fileName 文件名（不含扩展名）
     * @return PDF文件URL
     */
    public String generateTablePdf(String title, String[] headers, List<String[]> data, String fileName) {
        try {
            // 生成PDF字节数组
            byte[] pdfBytes = PdfUtils.generateTablePdfToBytes(title, headers, data);

            // 保存PDF文件
            String pdfFileName = fileName + "_" + System.currentTimeMillis() + ".pdf";
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
            FileUtils.createDirectories(storePath);
            String fullPath = storePath + pdfFileName;

            if (PdfUtils.savePdfToFile(pdfBytes, fullPath)) {
                // 生成访问URL
                String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                return fileUploadConfig.getUrlPrefix() + relativePath;
            }

            throw new RuntimeException("PDF文件保存失败");
        } catch (Exception e) {
            log.error("生成表格PDF失败, title: {}", title, e);
            throw new RuntimeException("生成表格PDF失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成自定义PDF（通用方法）
     * 
     * @param title PDF标题
     * @param content 内容列表
     * @param fileName 文件名（不含扩展名）
     * @return PDF文件URL
     */
    public String generateCustomPdf(String title, List<String> content, String fileName) {
        try {
            // 生成PDF字节数组
            byte[] pdfBytes = PdfUtils.generatePdfToBytes(title, content);

            // 保存PDF文件
            String pdfFileName = fileName + "_" + System.currentTimeMillis() + ".pdf";
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
            FileUtils.createDirectories(storePath);
            String fullPath = storePath + pdfFileName;

            if (PdfUtils.savePdfToFile(pdfBytes, fullPath)) {
                // 生成访问URL
                String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                return fileUploadConfig.getUrlPrefix() + relativePath;
            }

            throw new RuntimeException("PDF文件保存失败");
        } catch (Exception e) {
            log.error("生成自定义PDF失败, title: {}", title, e);
            throw new RuntimeException("生成自定义PDF失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建Mock订单数据
     */
    private ApplicationPdfDto createMockOrderData(String orderId) {
        ApplicationPdfDto dto = new ApplicationPdfDto();
        dto.setOrderId(orderId);
        dto.setOrderNo("RO" + orderId);
        dto.setOrderType("purchase");
        dto.setPartyA("回收管理有限公司");
        dto.setPartyB("合作方" + orderId);
        dto.setContractNo("CT" + orderId);
        dto.setContractName("回收合同" + orderId);
        dto.setDeliveryAddress("北京市朝阳区交付地址");
        dto.setWarehouseAddress("北京市朝阳区仓库地址");
        dto.setProcessor("经办人");
        dto.setProcessorPhone("13800138000");
        dto.setStartTime(new Date());
        dto.setPaymentAccount("6222021234567890123");
        dto.setTotalAmount(new BigDecimal("5210.00"));
        
        // 创建Mock订单明细
        List<ApplicationPdfDto.OrderItemDto> items = new ArrayList<>();
        
        ApplicationPdfDto.OrderItemDto item1 = new ApplicationPdfDto.OrderItemDto();
        item1.setType("goods");
        item1.setName("废纸");
        item1.setSpecification("A4纸");
        item1.setRemark("白色废纸");
        item1.setQuantity(new BigDecimal("1000"));
        item1.setUnitPrice(new BigDecimal("2.5"));
        item1.setAmount(new BigDecimal("2500"));
        items.add(item1);
        
        ApplicationPdfDto.OrderItemDto item2 = new ApplicationPdfDto.OrderItemDto();
        item2.setType("goods");
        item2.setName("废塑料");
        item2.setSpecification("PET瓶");
        item2.setRemark("透明塑料瓶");
        item2.setQuantity(new BigDecimal("500"));
        item2.setUnitPrice(new BigDecimal("3.2"));
        item2.setAmount(new BigDecimal("1600"));
        items.add(item2);
        
        ApplicationPdfDto.OrderItemDto item3 = new ApplicationPdfDto.OrderItemDto();
        item3.setType("transport");
        item3.setName("运输服务");
        item3.setSpecification("市内运输");
        item3.setRemark("10公里内");
        item3.setQuantity(new BigDecimal("50"));
        item3.setUnitPrice(new BigDecimal("15"));
        item3.setAmount(new BigDecimal("750"));
        items.add(item3);
        
        ApplicationPdfDto.OrderItemDto item4 = new ApplicationPdfDto.OrderItemDto();
        item4.setType("goods");
        item4.setName("废金属");
        item4.setSpecification("废铁");
        item4.setRemark("废旧钢材");
        item4.setQuantity(new BigDecimal("200"));
        item4.setUnitPrice(new BigDecimal("1.8"));
        item4.setAmount(new BigDecimal("360"));
        items.add(item4);
        
        dto.setOrderItems(items);
        
        return dto;
    }

    /**
     * 使用纯OpenPDF API生成业务申请单PDF
     * 
     * @param orderData 订单数据
     * @return PDF文件URL
     */
    public String generateApplicationPdfWithOpenPdf(ApplicationPdfDto orderData) {
        try {
            // 转换为OpenPDF数据格式
            OpenPdfUtils.ApplicationPdfData pdfData = convertToOpenPdfData(orderData);
            
            // 生成PDF字节数组
            byte[] pdfBytes = OpenPdfUtils.generateApplicationPdf(pdfData);
            
            // 保存PDF文件
            String fileName = "application_openpdf_" + (orderData.getOrderId() != null ? orderData.getOrderId() : System.currentTimeMillis()) 
                    + "_" + System.currentTimeMillis() + ".pdf";
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
            FileUtils.createDirectories(storePath);
            String fullPath = storePath + fileName;
            
            if (OpenPdfUtils.savePdfToFile(pdfBytes, fullPath)) {
                // 生成访问URL
                String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
                return fileUploadConfig.getUrlPrefix() + relativePath;
            }
            
            throw new RuntimeException("PDF文件保存失败");
        } catch (Exception e) {
            log.error("使用OpenPDF生成业务申请单PDF失败, orderId: {}", orderData.getOrderId(), e);
            throw new RuntimeException("生成PDF失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成Mock业务申请单PDF（使用纯OpenPDF）
     * 
     * @param orderId 订单ID
     * @return PDF文件URL
     */
    public String generateMockApplicationPdfWithOpenPdf(String orderId) {
        ApplicationPdfDto mockData = createMockOrderData(orderId);
        return generateApplicationPdfWithOpenPdf(mockData);
    }
    
    /**
     * 将ApplicationPdfDto转换为OpenPdfUtils.ApplicationPdfData
     */
    private OpenPdfUtils.ApplicationPdfData convertToOpenPdfData(ApplicationPdfDto dto) {
        OpenPdfUtils.ApplicationPdfData data = new OpenPdfUtils.ApplicationPdfData();
        data.setOrderId(dto.getOrderId());
        data.setOrderNo(dto.getOrderNo());
        data.setOrderType(dto.getOrderType());
        data.setPartyA(dto.getPartyA());
        data.setPartyB(dto.getPartyB());
        data.setContractNo(dto.getContractNo());
        data.setDeliveryAddress(dto.getDeliveryAddress());
        data.setWarehouseAddress(dto.getWarehouseAddress());
        data.setProcessor(dto.getProcessor());
        data.setProcessorPhone(dto.getProcessorPhone());
        data.setStartTime(dto.getStartTime());
        data.setPaymentAccount(dto.getPaymentAccount());
        
        // 转换订单明细
        if (dto.getOrderItems() != null) {
            List<OpenPdfUtils.ApplicationPdfData.OrderItem> items = new ArrayList<>();
            for (ApplicationPdfDto.OrderItemDto itemDto : dto.getOrderItems()) {
                OpenPdfUtils.ApplicationPdfData.OrderItem item = new OpenPdfUtils.ApplicationPdfData.OrderItem();
                item.setType(itemDto.getType());
                item.setName(itemDto.getName());
                item.setSpecification(itemDto.getSpecification());
                item.setRemark(itemDto.getRemark());
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setAmount(itemDto.getAmount());
                items.add(item);
            }
            data.setOrderItems(items);
        }
        
        return data;
    }
}

