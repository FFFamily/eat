
# PDF生成功能使用说明

## 概述

本项目基于 **OpenPDF** 实现了PDF生成功能，支持中文显示，可以生成各种类型的PDF文档。

## 技术方案

- **库**: OpenPDF 1.3.30（开源免费，支持中文）
- **优势**: 
  - 完全开源，无需许可证
  - 支持中文字体
  - API简洁易用
  - 性能良好
  - 与Spring Boot完美集成

## 核心类说明

### 1. PdfUtils（工具类）
提供基础的PDF生成方法：
- `generateSimplePdf()` - 生成简单文本PDF
- `generatePdfToBytes()` - 生成PDF到字节数组
- `generateTablePdf()` - 生成带表格的PDF
- `generateTablePdfToBytes()` - 生成表格PDF到字节数组
- `savePdfToFile()` - 保存PDF字节数组到文件

### 2. PdfGenerateService（服务类）
提供业务相关的PDF生成方法：
- `generateDeliveryNotePdf()` - 生成交付单PDF
- `generateSettlementPdf()` - 生成结算单PDF
- `generateApplicationPdf()` - 生成申请单PDF
- `generateTablePdf()` - 生成带表格的PDF（通用）
- `generateCustomPdf()` - 生成自定义PDF（通用）

## 使用示例

### 示例1: 在Service中使用PDF生成服务

```java
@Autowired
private PdfGenerateService pdfGenerateService;

// 生成交付单PDF
public void generateDeliveryNote(String orderId) {
    RecycleOrder order = recycleOrderService.getById(orderId);
    
    Map<String, Object> orderData = new HashMap<>();
    orderData.put("orderNo", order.getOrderNo());
    orderData.put("deliveryDate", DateUtil.format(order.getDeliveryTime(), "yyyy-MM-dd"));
    orderData.put("customerName", order.getCustomerName());
    orderData.put("deliveryAddress", order.getDeliveryAddress());
    orderData.put("contactPhone", order.getContactPhone());
    orderData.put("remark", order.getRemark());
    
    // 生成PDF并获取URL
    String pdfUrl = pdfGenerateService.generateDeliveryNotePdf(orderId, orderData);
    
    // 保存PDF URL到订单
    order.setDeliveryNotePdfUrl(pdfUrl);
    recycleOrderService.updateById(order);
}
```

### 示例2: 生成带表格的PDF

```java
@Autowired
private PdfGenerateService pdfGenerateService;

public void generateOrderListPdf() {
    // 准备表头
    String[] headers = {"订单编号", "客户名称", "订单金额", "创建时间"};
    
    // 准备数据
    List<String[]> data = new ArrayList<>();
    List<RecycleOrder> orders = recycleOrderService.list();
    for (RecycleOrder order : orders) {
        String[] row = {
            order.getOrderNo(),
            order.getCustomerName(),
            order.getTotalAmount().toString(),
            DateUtil.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss")
        };
        data.add(row);
    }
    
    // 生成PDF
    String pdfUrl = pdfGenerateService.generateTablePdf(
        "订单列表", 
        headers, 
        data, 
        "order_list"
    );
    
    log.info("订单列表PDF生成成功: {}", pdfUrl);
}
```

### 示例3: 生成自定义PDF

```java
@Autowired
private PdfGenerateService pdfGenerateService;

public void generateCustomPdf() {
    List<String> content = new ArrayList<>();
    content.add("标题: 自定义文档");
    content.add("内容1: 这是第一段内容");
    content.add("内容2: 这是第二段内容");
    content.add("内容3: 这是第三段内容");
    
    String pdfUrl = pdfGenerateService.generateCustomPdf(
        "自定义文档标题",
        content,
        "custom_document"
    );
    
    log.info("自定义PDF生成成功: {}", pdfUrl);
}
```

### 示例4: 直接使用PdfUtils工具类

```java
import com.tutu.system.utils.PdfUtils;

// 生成简单PDF
List<String> content = new ArrayList<>();
content.add("第一行内容");
content.add("第二行内容");
PdfUtils.generateSimplePdf("文档标题", content, "/path/to/output.pdf");

// 生成PDF到字节数组（用于返回给前端）
byte[] pdfBytes = PdfUtils.generatePdfToBytes("文档标题", content);
response.setContentType("application/pdf");
response.getOutputStream().write(pdfBytes);
```

## 中文字体支持

工具类已自动处理中文字体：
- **macOS**: 使用 PingFang 字体
- **Windows**: 使用 SimSun（宋体）
- **Linux**: 使用 DejaVu Sans 字体

如果系统字体加载失败，会自动降级使用默认字体。

## 注意事项

1. **文件路径**: PDF文件会保存到 `FileUploadConfig` 配置的 `basePath` 目录下
2. **文件命名**: 自动添加时间戳，避免文件名冲突
3. **URL生成**: 自动生成访问URL，基于 `FileUploadConfig` 的 `urlPrefix` 配置
4. **异常处理**: 所有方法都有异常处理，失败会记录日志并抛出运行时异常

## 扩展建议

如果需要更复杂的PDF布局，可以考虑：

1. **使用Thymeleaf模板引擎**：
   - 添加 `spring-boot-starter-thymeleaf` 依赖
   - 创建HTML模板
   - 使用 `ITextRenderer` 将HTML转换为PDF

2. **使用iText（商业项目需购买许可证）**：
   - 功能更强大
   - 支持更复杂的布局和样式

3. **使用JasperReports**：
   - 适合报表类PDF
   - 支持可视化设计工具

## 依赖配置

已在 `hs_system/pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.30</version>
</dependency>
```

## 相关文件

- `hs_system/src/main/java/com/tutu/system/utils/PdfUtils.java` - PDF工具类
- `hs_system/src/main/java/com/tutu/system/service/PdfGenerateService.java` - PDF生成服务
- `hs_system/pom.xml` - 依赖配置

