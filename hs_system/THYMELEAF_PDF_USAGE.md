# Thymeleaf模板生成PDF使用说明

## 概述

本项目使用 **Thymeleaf + Flying Saucer** 方案生成PDF，通过HTML模板渲染后转换为PDF，支持复杂的样式和布局。

## 技术栈

- **Thymeleaf**: Spring Boot官方推荐的模板引擎
- **Flying Saucer (xhtmlrenderer)**: 将HTML/CSS转换为PDF
- **OpenPDF**: PDF底层库（Flying Saucer依赖）

## 已实现功能

### 1. 业务申请单PDF生成

基于Vue文件 `ApplicationPDF.vue` 转换而来的Thymeleaf模板，完全还原了前端展示的样式和内容。

## 文件结构

```
hs_system/
├── src/main/java/com/tutu/system/
│   ├── dto/
│   │   └── ApplicationPdfDto.java          # 业务申请单数据DTO
│   ├── service/
│   │   └── PdfGenerateService.java          # PDF生成服务（已扩展）
│   └── utils/
│       └── ThymeleafPdfUtils.java          # Thymeleaf转PDF工具类
│
hs_api/
├── src/main/java/com/tutu/api/controller/
│   └── PdfGenerateController.java          # PDF生成控制器
└── src/main/resources/
    └── templates/
        └── pdf/
            └── application.html             # 业务申请单Thymeleaf模板
```

## API接口

### 1. 生成业务申请单PDF（Mock数据）

**接口**: `GET /api/pdf/application/mock/{orderId}`

**说明**: 使用Mock数据生成PDF，用于测试

**示例**:
```bash
curl http://localhost:8081/api/pdf/application/mock/12345
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": "/files/2024/01/15/application_12345_1705123456789.pdf"
}
```

### 2. 生成业务申请单PDF（实际数据）

**接口**: `POST /api/pdf/application/generate`

**说明**: 使用实际订单数据生成PDF

**请求体**:
```json
{
  "orderId": "12345",
  "orderNo": "RO12345",
  "orderType": "purchase",
  "partyA": "回收管理有限公司",
  "partyB": "合作方12345",
  "contractNo": "CT12345",
  "deliveryAddress": "北京市朝阳区交付地址",
  "warehouseAddress": "北京市朝阳区仓库地址",
  "processor": "经办人",
  "processorPhone": "13800138000",
  "paymentAccount": "6222021234567890123",
  "orderItems": [
    {
      "type": "goods",
      "name": "废纸",
      "specification": "A4纸",
      "remark": "白色废纸",
      "quantity": 1000,
      "unitPrice": 2.5,
      "amount": 2500
    }
  ]
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": "/files/2024/01/15/application_12345_1705123456789.pdf"
}
```

## 使用示例

### 示例1: 在Service中调用

```java
@Autowired
private PdfGenerateService pdfGenerateService;

public void generateApplicationPdf(String orderId) {
    // 方式1: 使用Mock数据（测试用）
    String pdfUrl = pdfGenerateService.generateMockApplicationPdf(orderId);
    
    // 方式2: 使用实际数据
    ApplicationPdfDto orderData = new ApplicationPdfDto();
    orderData.setOrderId(orderId);
    orderData.setOrderNo("RO" + orderId);
    // ... 设置其他数据
    String pdfUrl = pdfGenerateService.generateApplicationPdfFromTemplate(orderData);
    
    // 保存PDF URL到订单
    // ...
}
```

### 示例2: 在Controller中调用

```java
@Autowired
private PdfGenerateService pdfGenerateService;

@GetMapping("/order/{orderId}/application-pdf")
public BaseResponse<String> getApplicationPdf(@PathVariable String orderId) {
    try {
        // 从数据库获取订单数据
        RecycleOrder order = recycleOrderService.getById(orderId);
        
        // 转换为DTO
        ApplicationPdfDto dto = convertToDto(order);
        
        // 生成PDF
        String pdfUrl = pdfGenerateService.generateApplicationPdfFromTemplate(dto);
        
        return BaseResponse.success(pdfUrl);
    } catch (Exception e) {
        return BaseResponse.error("生成PDF失败: " + e.getMessage());
    }
}
```

## 模板变量说明

Thymeleaf模板 `application.html` 使用以下变量：

| 变量名 | 类型 | 说明 |
|--------|------|------|
| `orderData` | ApplicationPdfDto | 订单数据对象 |
| `orderTypeText` | String | 订单类型文本（采购/销售等） |
| `itemTypeMap` | Map<String, String> | 项目类型映射（goods->货物等） |
| `flowDirectionText` | String | 流转方向文本 |
| `startTimeText` | String | 订单申请时间（格式化后） |
| `currentDate` | String | 当前日期（格式化后） |

## 数据转换

### 从订单实体转换为DTO

```java
private ApplicationPdfDto convertToDto(RecycleOrder order) {
    ApplicationPdfDto dto = new ApplicationPdfDto();
    dto.setOrderId(order.getId());
    dto.setOrderNo(order.getOrderNo());
    dto.setOrderType(order.getType());
    dto.setPartyA(order.getPartyA());
    dto.setPartyB(order.getPartyB());
    dto.setContractNo(order.getContractNo());
    dto.setDeliveryAddress(order.getDeliveryAddress());
    dto.setWarehouseAddress(order.getWarehouseAddress());
    dto.setProcessor(order.getProcessor());
    dto.setProcessorPhone(order.getProcessorPhone());
    dto.setStartTime(order.getStartTime());
    dto.setPaymentAccount(order.getPaymentAccount());
    dto.setTotalAmount(order.getTotalAmount());
    
    // 转换订单明细
    List<ApplicationPdfDto.OrderItemDto> items = order.getItems().stream()
        .map(item -> {
            ApplicationPdfDto.OrderItemDto itemDto = new ApplicationPdfDto.OrderItemDto();
            itemDto.setType(item.getGoodType());
            itemDto.setName(item.getGoodName());
            itemDto.setSpecification(item.getGoodModel());
            itemDto.setRemark(item.getRemark());
            itemDto.setQuantity(item.getGoodCount());
            itemDto.setUnitPrice(item.getGoodPrice());
            itemDto.setAmount(item.getGoodTotalPrice());
            return itemDto;
        })
        .collect(Collectors.toList());
    dto.setOrderItems(items);
    
    return dto;
}
```

## 模板自定义

### 修改样式

编辑 `hs_api/src/main/resources/templates/pdf/application.html` 中的 `<style>` 部分即可修改PDF样式。

### 添加新字段

1. 在 `ApplicationPdfDto` 中添加新字段
2. 在模板中添加对应的显示元素
3. 在 `prepareTemplateVariables` 方法中准备数据

## 注意事项

1. **字体支持**: Flying Saucer支持系统字体，中文字体需要确保系统已安装
2. **CSS支持**: 支持大部分CSS2.1标准，但某些CSS3特性可能不支持
3. **图片**: 支持base64编码的图片，外部图片URL需要确保可访问
4. **分页**: 模板会自动处理分页，可通过CSS的 `page-break-*` 属性控制

## 后续扩展

### 替换Mock数据为实际数据

在 `PdfGenerateController` 或相关Service中：

```java
@GetMapping("/application/{orderId}")
public BaseResponse<String> generateApplicationPdf(@PathVariable String orderId) {
    // 1. 从数据库获取订单数据
    RecycleOrder order = recycleOrderService.getById(orderId);
    if (order == null) {
        return BaseResponse.error("订单不存在");
    }
    
    // 2. 获取订单明细
    List<RecycleOrderItem> items = recycleOrderItemService.getByOrderId(orderId);
    
    // 3. 转换为DTO
    ApplicationPdfDto dto = convertOrderToDto(order, items);
    
    // 4. 生成PDF
    String pdfUrl = pdfGenerateService.generateApplicationPdfFromTemplate(dto);
    
    return BaseResponse.success(pdfUrl);
}
```

## 依赖配置

已在 `hs_system/pom.xml` 中添加：

```xml
<!-- Thymeleaf模板引擎 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Flying Saucer - HTML转PDF -->
<dependency>
    <groupId>org.xhtmlrenderer</groupId>
    <artifactId>flying-saucer-pdf-openpdf</artifactId>
    <version>9.1.22</version>
</dependency>
```

## 常见问题

### 1. 中文显示乱码

确保系统已安装中文字体，Flying Saucer会自动使用系统字体。

### 2. PDF样式与HTML不一致

Flying Saucer支持CSS2.1标准，某些CSS3特性可能不支持，建议使用标准CSS属性。

### 3. 图片不显示

- 使用base64编码的图片
- 或确保图片URL可访问
- 检查图片路径是否正确

## 相关文件

- `hs_system/src/main/java/com/tutu/system/dto/ApplicationPdfDto.java` - 数据DTO
- `hs_system/src/main/java/com/tutu/system/utils/ThymeleafPdfUtils.java` - 工具类
- `hs_system/src/main/java/com/tutu/system/service/PdfGenerateService.java` - 服务类
- `hs_api/src/main/java/com/tutu/api/controller/PdfGenerateController.java` - 控制器
- `hs_api/src/main/resources/templates/pdf/application.html` - Thymeleaf模板

