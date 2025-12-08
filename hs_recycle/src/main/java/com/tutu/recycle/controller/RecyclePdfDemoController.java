package com.tutu.recycle.controller;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.utils.pdf.PdfGenerator;
import com.tutu.system.config.FileUploadConfig;
import com.tutu.system.utils.FileUtils;
import com.tutu.system.utils.OpenPdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recycle/pdf")
public class RecyclePdfDemoController {
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private FileUploadConfig fileUploadConfig;

    @GetMapping("/pdf")
    @ResponseBody
    public BaseResponse<String> generatePdf() throws Exception {
        // 创建Model并添加mock数据
        Model model = new ExtendedModelMap();
        
        // 模拟订单数据
        Map<String, Object> orderData = createMockOrderData();
        model.addAttribute("orderData", orderData);
        
        // 模拟订单明细数据
        List<Map<String, Object>> orderItems = createMockOrderItems();
        model.addAttribute("orderItems", orderItems);
        
        // 订单类型文本
        String orderType = (String) orderData.get("type");
        model.addAttribute("orderTypeText", getOrderTypeText(orderType));
        
        // 流转方向文本
        model.addAttribute("flowDirectionText", getFlowDirectionText(orderType));
        
        // 申请单生成日期
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        model.addAttribute("applicationDate", LocalDateTime.now().format(dateFormatter));
        
        Context context = new Context();
        context.setVariables(model.asMap());

        // 渲染 HTML 模板
        String html = templateEngine.process("report", context);

        // 转 PDF
        byte[] pdfBytes = PdfGenerator.htmlToPdf(html);
        // 保存PDF文件
        String fileName = "application_openpdf_" + System.currentTimeMillis() + ".pdf";
        String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
        FileUtils.createDirectories(storePath);
        String fullPath = storePath + fileName;

        if (OpenPdfUtils.savePdfToFile(pdfBytes, fullPath)) {
            // 生成访问URL
            String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            return BaseResponse.success(fileUploadConfig.getUrlPrefix() + relativePath);
        }
        return BaseResponse.error("生成PDF失败");
    }

    /**
     * 创建模拟订单数据
     */
    private Map<String, Object> createMockOrderData() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("id", "12345");
        orderData.put("no", "RO20240101001");
        orderData.put("type", "purchase"); // purchase, transport, process, sale, storage, other
        orderData.put("contractNo", "CT20240101001");
        orderData.put("partyA", "回收管理有限公司");
        orderData.put("partyB", "合作方12345");
        orderData.put("processor", "李经理");
        orderData.put("processorPhone", "13800138000");
        orderData.put("deliveryAddress", "北京市朝阳区某某街道123号");
        orderData.put("warehouseAddress", "北京市朝阳区仓库园区A区1号仓库");
        orderData.put("paymentAccount", "6222021234567890123");
        
        // 订单申请时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        orderData.put("startTime", LocalDateTime.now().format(formatter) + "（我方）");
        
        return orderData;
    }

    /**
     * 创建模拟订单明细数据
     */
    private List<Map<String, Object>> createMockOrderItems() {
        List<Map<String, Object>> items = new ArrayList<>();
        
        // 货物1：废纸
        Map<String, Object> item1 = new HashMap<>();
        item1.put("type", "goods");
        item1.put("typeText", "货物");
        item1.put("name", "废纸");
        item1.put("specification", "A4纸");
        item1.put("remark", "白色废纸");
        item1.put("quantity", 1000);
        item1.put("unitPrice", new BigDecimal("2.50"));
        item1.put("amount", new BigDecimal("2500.00"));
        items.add(item1);
        
        // 货物2：废塑料
        Map<String, Object> item2 = new HashMap<>();
        item2.put("type", "goods");
        item2.put("typeText", "货物");
        item2.put("name", "废塑料");
        item2.put("specification", "PET瓶");
        item2.put("remark", "透明塑料瓶");
        item2.put("quantity", 500);
        item2.put("unitPrice", new BigDecimal("3.20"));
        item2.put("amount", new BigDecimal("1600.00"));
        items.add(item2);
        
        // 运输服务
        Map<String, Object> item3 = new HashMap<>();
        item3.put("type", "transport");
        item3.put("typeText", "运输");
        item3.put("name", "运输服务");
        item3.put("specification", "市内运输");
        item3.put("remark", "10公里内");
        item3.put("quantity", 50);
        item3.put("unitPrice", new BigDecimal("15.00"));
        item3.put("amount", new BigDecimal("750.00"));
        items.add(item3);
        
        // 货物3：废金属
        Map<String, Object> item4 = new HashMap<>();
        item4.put("type", "goods");
        item4.put("typeText", "货物");
        item4.put("name", "废金属");
        item4.put("specification", "废铁");
        item4.put("remark", "废旧钢材");
        item4.put("quantity", 200);
        item4.put("unitPrice", new BigDecimal("1.80"));
        item4.put("amount", new BigDecimal("360.00"));
        items.add(item4);
        
        return items;
    }

    /**
     * 获取订单类型显示文本
     */
    private String getOrderTypeText(String type) {
        if (type == null) {
            return "未知类型";
        }
        switch (type) {
            case "purchase":
                return "采购";
            case "transport":
                return "运输";
            case "process":
                return "加工";
            case "sale":
                return "销售";
            case "storage":
                return "仓储";
            case "other":
                return "其他";
            default:
                return "未知类型";
        }
    }

    /**
     * 获取流转方向文本
     */
    private String getFlowDirectionText(String orderType) {
        if (orderType == null) {
            return "未知";
        }
        switch (orderType) {
            case "purchase":
                return "采购";
            case "sale":
                return "销售";
            case "process":
                return "加工";
            case "storage":
                return "入库/出库";
            case "transport":
                return "运输";
            case "other":
                return "其他";
            default:
                return "未知";
        }
    }
}
