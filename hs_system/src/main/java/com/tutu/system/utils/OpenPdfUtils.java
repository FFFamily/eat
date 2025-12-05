package com.tutu.system.utils;

import lombok.extern.slf4j.Slf4j;
import org.openpdf.text.*;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 纯OpenPDF工具类
 * 直接使用OpenPDF API构建PDF，不使用HTML模板
 */
@Slf4j
public class OpenPdfUtils {

    // 中文字体支持 - 多个备选路径
    private static final String[] FONT_PATHS_MAC = {
        "/System/Library/Fonts/PingFang.ttc",
        "/System/Library/Fonts/Supplemental/PingFang.ttc",
        "/Library/Fonts/Microsoft/Microsoft YaHei.ttf",
        "/System/Library/Fonts/STHeiti Light.ttc",
        "/System/Library/Fonts/STHeiti Medium.ttc"
    };
    private static final String[] FONT_PATHS_WIN = {
        "C:/Windows/Fonts/simsun.ttc",
        "C:/Windows/Fonts/simhei.ttf",
        "C:/Windows/Fonts/msyh.ttf",
        "C:/Windows/Fonts/simkai.ttf"
    };
    private static final String[] FONT_PATHS_LINUX = {
        "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc",
        "/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc",
        "/usr/share/fonts/truetype/arphic/uming.ttc",
        "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc"
    };

    /**
     * 获取中文字体
     */
    private static BaseFont getChineseFont() {
        String osName = System.getProperty("os.name").toLowerCase();
        String[] fontPaths;
        int ttcIndex = 0;
        
        // 根据操作系统选择字体路径数组
        if (osName.contains("mac")) {
            fontPaths = FONT_PATHS_MAC;
            ttcIndex = 0; // macOS PingFang.ttc 使用索引0
        } else if (osName.contains("win")) {
            fontPaths = FONT_PATHS_WIN;
            ttcIndex = 1; // Windows simsun.ttc 使用索引1
        } else {
            fontPaths = FONT_PATHS_LINUX;
            ttcIndex = 0;
        }
        
        // 尝试加载字体，按优先级顺序
        Exception lastException = null;
        for (String fontPath : fontPaths) {
            try {
                java.io.File fontFile = new java.io.File(fontPath);
                if (!fontFile.exists()) {
                    log.debug("字体文件不存在，跳过: {}", fontPath);
                    continue;
                }
                
                String fontPathWithIndex = fontPath;
                if (fontPath.endsWith(".ttc")) {
                    fontPathWithIndex = fontPath + "," + ttcIndex;
                }
                
                log.info("尝试加载中文字体: {}", fontPathWithIndex);
                BaseFont font = BaseFont.createFont(fontPathWithIndex, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                log.info("成功加载中文字体: {}", fontPath);
                return font;
            } catch (Exception e) {
                log.debug("加载字体失败: {}, 错误: {}", fontPath, e.getMessage());
                lastException = e;
                // 继续尝试下一个字体
            }
        }
        
        // 所有字体都加载失败，记录警告并使用备用方案
        log.warn("无法加载任何中文字体，尝试使用STSong-Light: {}", lastException != null ? lastException.getMessage() : "未知错误");
        try {
            // 尝试使用OpenPDF内置的中文字体（如果支持）
            return BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
        } catch (Exception e) {
            log.error("无法加载STSong-Light字体", e);
            // 最后尝试使用支持Unicode的字体
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ex) {
                log.error("创建备用字体失败", ex);
                throw new RuntimeException("无法创建中文字体，请确保系统已安装中文字体", ex);
            }
        }
    }

    /**
     * 创建中文字体
     */
    public static Font createChineseFont(float size, int style) {
        BaseFont baseFont = getChineseFont();
        return new Font(baseFont, size, style);
    }

    /**
     * 创建中文字体（默认样式）
     */
    public static Font createChineseFont(float size) {
        return createChineseFont(size, Font.NORMAL);
    }

    /**
     * 生成业务申请单PDF（纯OpenPDF实现）
     * 
     * @param orderData 订单数据
     * @return PDF字节数组
     */
    public static byte[] generateApplicationPdf(ApplicationPdfData orderData) {
        try {
            // 记录输入数据，用于调试
            log.info("开始生成PDF，订单ID: {}, 订单编号: {}, 订单类型: {}", 
                    orderData.getOrderId(), orderData.getOrderNo(), orderData.getOrderType());
            log.debug("订单明细数量: {}", orderData.getOrderItems() != null ? orderData.getOrderItems().size() : 0);
            
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // 设置字体 - 确保字体加载成功
            log.debug("开始创建中文字体");
            Font titleFont = createChineseFont(20, Font.BOLD);
            Font sectionFont = createChineseFont(14, Font.BOLD);
            Font normalFont = createChineseFont(12);
            Font labelFont = createChineseFont(12, Font.BOLD);
            Font smallFont = createChineseFont(10);
            log.debug("中文字体创建完成");
            
            // 1. 标题
            Paragraph title = new Paragraph("业务申请单", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // 2. 合同基本信息
            addContractInfo(document, orderData, labelFont, normalFont);
            
            // 3. 申请说明
            Paragraph description = new Paragraph();
            description.add(new Chunk("按合同约定，我方现就该合同下编号为 ", normalFont));
            description.add(new Chunk(orderData.getOrderNo() != null ? orderData.getOrderNo() : "RO" + orderData.getOrderId(), labelFont));
            description.add(new Chunk(" 的订单申请执行：", normalFont));
            description.setSpacingAfter(15);
            document.add(description);
            
            // 4. 订单信息
            addOrderInfo(document, orderData, sectionFont, labelFont, normalFont, smallFont);
            
            // 5. 其他信息
            addOtherInfo(document, orderData, sectionFont, labelFont, normalFont);
            
            // 6. 最终批注
            Paragraph finalNotes = new Paragraph();
            finalNotes.add(new Chunk("注：", labelFont));
            finalNotes.add(new Chunk("如有疑问请于本单据发出后1个工作日内联系我司企业微信客服或经办人员 ", normalFont));
            finalNotes.add(new Chunk("孟经理（13683335083）", labelFont));
            finalNotes.add(new Chunk("，过时默认同意本申请单内容。", normalFont));
            finalNotes.setSpacingBefore(20);
            finalNotes.setSpacingAfter(20);
            document.add(finalNotes);
            
            // 7. 公司信息和日期
            addCompanyFooter(document, orderData, normalFont, labelFont);
            
            document.close();
            writer.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("生成业务申请单PDF失败", e);
            throw new RuntimeException("PDF生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加合同基本信息
     */
    private static void addContractInfo(Document document, ApplicationPdfData orderData, 
                                        Font labelFont, Font normalFont) throws DocumentException {
        Paragraph partyA = new Paragraph();
        partyA.add(new Chunk("甲方：", labelFont));
        partyA.add(new Chunk(orderData.getPartyA() != null ? orderData.getPartyA() : "回收管理有限公司", normalFont));
        partyA.setSpacingAfter(8);
        document.add(partyA);
        
        Paragraph partyB = new Paragraph();
        partyB.add(new Chunk("乙方：", labelFont));
        partyB.add(new Chunk(orderData.getPartyB() != null ? orderData.getPartyB() : "合作方" + orderData.getOrderId(), normalFont));
        partyB.setSpacingAfter(8);
        document.add(partyB);
        
        Paragraph contractNo = new Paragraph();
        contractNo.add(new Chunk("合同编号：", labelFont));
        contractNo.add(new Chunk(orderData.getContractNo() != null ? orderData.getContractNo() : "CT" + orderData.getOrderId(), normalFont));
        contractNo.setSpacingAfter(15);
        document.add(contractNo);
    }

    /**
     * 添加订单信息
     */
    private static void addOrderInfo(Document document, ApplicationPdfData orderData,
                                     Font sectionFont, Font labelFont, Font normalFont, Font smallFont) 
                                     throws DocumentException {
        // 章节标题
        Paragraph sectionTitle = new Paragraph("一、订单信息", sectionFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        // （1）订单类型
        Paragraph orderType = new Paragraph();
        orderType.add(new Chunk("（1）订单类型（我方）", labelFont));
        orderType.add(new Chunk("  ", normalFont));
        orderType.add(new Chunk(getOrderTypeText(orderData.getOrderType()) + "订单", normalFont));
        orderType.setSpacingAfter(8);
        document.add(orderType);
        
        // （2）订单内容
        Paragraph orderContentLabel = new Paragraph("（2）订单内容", labelFont);
        orderContentLabel.setSpacingAfter(8);
        document.add(orderContentLabel);
        
        // 订单明细表格
        addOrderItemsTable(document, orderData, normalFont, labelFont);
        
        // 批注说明
        Paragraph notes = new Paragraph();
        notes.add(new Chunk("注：", labelFont));
        notes.add(new Chunk("货物类数量单位为kg、运输类数量单位为km，总价单位为元。", smallFont));
        notes.setSpacingBefore(10);
        notes.setSpacingAfter(10);
        document.add(notes);
        
        // （3）订单交付地址
        Paragraph deliveryAddress = new Paragraph();
        deliveryAddress.add(new Chunk("（3）订单交付地址", labelFont));
        deliveryAddress.add(new Chunk("  ", normalFont));
        deliveryAddress.add(new Chunk(orderData.getDeliveryAddress() != null ? orderData.getDeliveryAddress() : "--", normalFont));
        deliveryAddress.setSpacingAfter(8);
        document.add(deliveryAddress);
        
        // （4）交付货物送达我司地址
        Paragraph warehouseAddress = new Paragraph();
        warehouseAddress.add(new Chunk("（4）交付货物送达我司地址（交付服务计入我司成本）", labelFont));
        warehouseAddress.add(new Chunk("  ", normalFont));
        warehouseAddress.add(new Chunk(orderData.getWarehouseAddress() != null ? orderData.getWarehouseAddress() : "--", normalFont));
        warehouseAddress.setSpacingAfter(8);
        document.add(warehouseAddress);
        
        // （5）订单经办人
        Paragraph processor = new Paragraph();
        processor.add(new Chunk("（5）订单经办人", labelFont));
        processor.add(new Chunk("  ", normalFont));
        processor.add(new Chunk("您方：", normalFont));
        processor.add(new Chunk(orderData.getProcessor() != null ? orderData.getProcessor() : "--", normalFont));
        processor.add(new Chunk("（", normalFont));
        processor.add(new Chunk(orderData.getProcessorPhone() != null ? orderData.getProcessorPhone() : "--", normalFont));
        processor.add(new Chunk("）", normalFont));
        processor.setSpacingAfter(8);
        document.add(processor);
    }

    /**
     * 添加订单明细表格
     */
    private static void addOrderItemsTable(Document document, ApplicationPdfData orderData,
                                          Font normalFont, Font labelFont) throws DocumentException {
        if (orderData.getOrderItems() == null || orderData.getOrderItems().isEmpty()) {
            log.warn("订单明细为空，跳过表格生成");
            // 添加提示信息
            Paragraph emptyNote = new Paragraph("（暂无订单明细）", normalFont);
            emptyNote.setSpacingBefore(10);
            emptyNote.setSpacingAfter(10);
            document.add(emptyNote);
            return;
        }
        
        log.debug("开始添加订单明细表格，共 {} 条记录", orderData.getOrderItems().size());
        
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        
        // 设置列宽
        try {
            table.setWidths(new float[]{1.2f, 1.5f, 1.5f, 1.5f, 1f, 1f, 1f});
        } catch (DocumentException e) {
            log.warn("设置表格列宽失败，使用默认宽度", e);
        }
        
        // 表头
        String[] headers = {"分类", "名称", "规格型号", "备注", "数量", "单价", "总价"};
        Font headerFont = createChineseFont(11, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            cell.setBackgroundColor(new Color(245, 245, 245));
            cell.setBorderColor(Color.GRAY);
            table.addCell(cell);
        }
        
        // 表格数据
        Font dataFont = createChineseFont(10);
        int itemIndex = 0;
        for (ApplicationPdfData.OrderItem item : orderData.getOrderItems()) {
            itemIndex++;
            log.debug("添加订单明细第 {} 条: 类型={}, 名称={}, 数量={}", 
                    itemIndex, item.getType(), item.getName(), item.getQuantity());
            
            // 分类
            String typeText = getItemTypeText(item.getType());
            PdfPCell cell1 = new PdfPCell(new Phrase(typeText, dataFont));
            cell1.setPadding(6);
            cell1.setBorderColor(Color.GRAY);
            table.addCell(cell1);
            
            // 名称
            String nameText = item.getName() != null ? item.getName() : "--";
            PdfPCell cell2 = new PdfPCell(new Phrase(nameText, dataFont));
            cell2.setPadding(6);
            cell2.setBorderColor(Color.GRAY);
            table.addCell(cell2);
            
            // 规格型号
            String specText = item.getSpecification() != null ? item.getSpecification() : "--";
            PdfPCell cell3 = new PdfPCell(new Phrase(specText, dataFont));
            cell3.setPadding(6);
            cell3.setBorderColor(Color.GRAY);
            table.addCell(cell3);
            
            // 备注
            String remarkText = item.getRemark() != null ? item.getRemark() : "--";
            PdfPCell cell4 = new PdfPCell(new Phrase(remarkText, dataFont));
            cell4.setPadding(6);
            cell4.setBorderColor(Color.GRAY);
            table.addCell(cell4);
            
            // 数量
            String quantityText = item.getQuantity() != null ? item.getQuantity().toString() : "0";
            PdfPCell cell5 = new PdfPCell(new Phrase(quantityText, dataFont));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setPadding(6);
            cell5.setBorderColor(Color.GRAY);
            table.addCell(cell5);
            
            // 单价
            String unitPriceText = "¥" + formatAmount(item.getUnitPrice());
            PdfPCell cell6 = new PdfPCell(new Phrase(unitPriceText, dataFont));
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setPadding(6);
            cell6.setBorderColor(Color.GRAY);
            table.addCell(cell6);
            
            // 总价
            String amountText = "¥" + formatAmount(item.getAmount());
            PdfPCell cell7 = new PdfPCell(new Phrase(amountText, dataFont));
            cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell7.setPadding(6);
            cell7.setBorderColor(Color.GRAY);
            table.addCell(cell7);
        }
        
        log.debug("订单明细表格添加完成，共添加 {} 条记录", itemIndex);
        document.add(table);
    }

    /**
     * 添加其他信息
     */
    private static void addOtherInfo(Document document, ApplicationPdfData orderData,
                                    Font sectionFont, Font labelFont, Font normalFont) 
                                    throws DocumentException {
        // 章节标题
        Paragraph sectionTitle = new Paragraph("二、其他信息", sectionFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
        
        // 订单申请时间
        Paragraph startTime = new Paragraph();
        startTime.add(new Chunk("订单申请时间：", labelFont));
        startTime.add(new Chunk("  ", normalFont));
        String timeText = orderData.getStartTime() != null 
            ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderData.getStartTime()) 
            : "--";
        startTime.add(new Chunk(timeText + "（我方）", normalFont));
        startTime.setSpacingAfter(8);
        document.add(startTime);
        
        // 订单内容流转方向
        Paragraph flowDirection = new Paragraph();
        flowDirection.add(new Chunk("订单内容流转方向：", labelFont));
        flowDirection.add(new Chunk("  ", normalFont));
        flowDirection.add(new Chunk(getFlowDirectionText(orderData.getOrderType()), normalFont));
        flowDirection.setSpacingAfter(8);
        document.add(flowDirection);
        
        // 合同约定走款账号
        Paragraph paymentAccount = new Paragraph();
        paymentAccount.add(new Chunk("合同约定走款账号：", labelFont));
        paymentAccount.add(new Chunk("  ", normalFont));
        paymentAccount.add(new Chunk(orderData.getPaymentAccount() != null ? orderData.getPaymentAccount() : "6222021234567890123", normalFont));
        paymentAccount.setSpacingAfter(8);
        document.add(paymentAccount);
    }

    /**
     * 添加公司信息和日期
     */
    private static void addCompanyFooter(Document document, ApplicationPdfData orderData,
                                        Font normalFont, Font labelFont) throws DocumentException {
        Paragraph companyName = new Paragraph("云南伏宁再生资源有限公司", labelFont);
        companyName.setAlignment(Element.ALIGN_RIGHT);
        companyName.setSpacingBefore(30);
        companyName.setSpacingAfter(8);
        document.add(companyName);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Paragraph date = new Paragraph("生成申请单日期：" + dateFormat.format(new Date()), normalFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
    }

    /**
     * 获取订单类型文本
     */
    private static String getOrderTypeText(String type) {
        if (type == null) return "未知类型";
        return switch (type) {
            case "purchase" -> "采购";
            case "sales" -> "销售";
            case "transport" -> "运输";
            case "process" -> "加工";
            case "storage" -> "仓储";
            case "other" -> "其他";
            default -> "未知类型";
        };
    }

    /**
     * 获取项目类型文本
     */
    private static String getItemTypeText(String type) {
        if (type == null) return "未知";
        return switch (type) {
            case "goods" -> "货物";
            case "transport" -> "运输";
            case "service" -> "服务";
            case "other" -> "其他";
            default -> type;
        };
    }

    /**
     * 获取流转方向文本
     */
    private static String getFlowDirectionText(String type) {
        if (type == null) return "未知";
        return switch (type) {
            case "purchase" -> "采购";
            case "sales" -> "销售";
            case "process" -> "加工";
            case "storage" -> "入库/出库";
            case "transport" -> "运输";
            case "other" -> "其他";
            default -> "未知";
        };
    }

    /**
     * 格式化金额
     */
    private static String formatAmount(java.math.BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%.2f", amount);
    }

    /**
     * 保存PDF字节数组到文件
     */
    public static boolean savePdfToFile(byte[] pdfBytes, String outputPath) {
        try {
            Path path = Paths.get(outputPath);
            Files.createDirectories(path.getParent());
            Files.write(path, pdfBytes);
            log.info("PDF保存成功: {}", outputPath);
            return true;
        } catch (IOException e) {
            log.error("PDF保存失败: {}", outputPath, e);
            return false;
        }
    }

    /**
     * 业务申请单数据类（简化版，用于OpenPDF）
     */
    public static class ApplicationPdfData {
        private String orderId;
        private String orderNo;
        private String orderType;
        private String partyA;
        private String partyB;
        private String contractNo;
        private String deliveryAddress;
        private String warehouseAddress;
        private String processor;
        private String processorPhone;
        private Date startTime;
        private String paymentAccount;
        private List<OrderItem> orderItems;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public String getOrderType() { return orderType; }
        public void setOrderType(String orderType) { this.orderType = orderType; }
        public String getPartyA() { return partyA; }
        public void setPartyA(String partyA) { this.partyA = partyA; }
        public String getPartyB() { return partyB; }
        public void setPartyB(String partyB) { this.partyB = partyB; }
        public String getContractNo() { return contractNo; }
        public void setContractNo(String contractNo) { this.contractNo = contractNo; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public String getWarehouseAddress() { return warehouseAddress; }
        public void setWarehouseAddress(String warehouseAddress) { this.warehouseAddress = warehouseAddress; }
        public String getProcessor() { return processor; }
        public void setProcessor(String processor) { this.processor = processor; }
        public String getProcessorPhone() { return processorPhone; }
        public void setProcessorPhone(String processorPhone) { this.processorPhone = processorPhone; }
        public Date getStartTime() { return startTime; }
        public void setStartTime(Date startTime) { this.startTime = startTime; }
        public String getPaymentAccount() { return paymentAccount; }
        public void setPaymentAccount(String paymentAccount) { this.paymentAccount = paymentAccount; }
        public List<OrderItem> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

        public static class OrderItem {
            private String type;
            private String name;
            private String specification;
            private String remark;
            private java.math.BigDecimal quantity;
            private java.math.BigDecimal unitPrice;
            private java.math.BigDecimal amount;

            // Getters and Setters
            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getSpecification() { return specification; }
            public void setSpecification(String specification) { this.specification = specification; }
            public String getRemark() { return remark; }
            public void setRemark(String remark) { this.remark = remark; }
            public java.math.BigDecimal getQuantity() { return quantity; }
            public void setQuantity(java.math.BigDecimal quantity) { this.quantity = quantity; }
            public java.math.BigDecimal getUnitPrice() { return unitPrice; }
            public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
            public java.math.BigDecimal getAmount() { return amount; }
            public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        }
    }
}

