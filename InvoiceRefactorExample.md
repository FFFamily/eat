# 发票接口重构示例

## 重构说明

### 重构前
- 根据ID获取发票只返回发票基本信息（RecycleInvoice）
- 需要额外调用获取明细接口才能获得完整信息

### 重构后
- 根据ID获取发票返回完整信息（CreateInvoiceRequest格式）
- 包含发票基本信息和所有明细信息
- 与创建发票接口使用相同的数据结构

## 接口对比

### 1. 创建发票接口
```http
POST /recycle/invoice/create
Content-Type: application/json

{
    "invoice": {
        "invoiceNo": "INV20240101001",
        "invoiceType": "销项",
        "invoiceBank": "中国工商银行",
        "plannedInvoiceTime": "2024-01-15 10:00:00",
        "processor": "张三",
        "totalAmount": 1000.00,
        "taxAmount": 130.00,
        "amountWithoutTax": 870.00
    },
    "details": [
        {
            "orderNo": "RO20240101001",
            "orderTotalAmount": 500.00,
            "orderActualInvoice": 500.00,
            "orderShouldInvoice": 500.00
        },
        {
            "orderNo": "RO20240101002",
            "orderTotalAmount": 500.00,
            "orderActualInvoice": 500.00,
            "orderShouldInvoice": 500.00
        }
    ]
}
```

### 2. 获取发票接口（重构后）
```http
GET /recycle/invoice/get/invoice001
```

**响应格式（InvoiceDetailResponse）**：
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "invoice": {
            "id": "invoice001",
            "invoiceNo": "INV20240101001",
            "invoiceType": "销项",
            "invoiceBank": "中国工商银行",
            "plannedInvoiceTime": "2024-01-15 10:00:00",
            "status": "pending",
            "processor": "张三",
            "invoiceTime": null,
            "totalAmount": 1000.00,
            "taxAmount": 130.00,
            "amountWithoutTax": 870.00,
            "createTime": "2024-01-01 10:00:00",
            "updateTime": "2024-01-01 10:00:00",
            "createBy": "admin",
            "updateBy": "admin",
            "isDeleted": "0"
        },
        "details": [
            {
                "id": "detail001",
                "invoiceId": "invoice001",
                "orderNo": "RO20240101001",
                "orderTotalAmount": 500.00,
                "orderActualInvoice": 500.00,
                "orderShouldInvoice": 500.00,
                "createTime": "2024-01-01 10:00:00",
                "updateTime": "2024-01-01 10:00:00",
                "createBy": "admin",
                "updateBy": "admin",
                "isDeleted": "0"
            },
            {
                "id": "detail002",
                "invoiceId": "invoice001",
                "orderNo": "RO20240101002",
                "orderTotalAmount": 500.00,
                "orderActualInvoice": 500.00,
                "orderShouldInvoice": 500.00,
                "createTime": "2024-01-01 10:00:00",
                "updateTime": "2024-01-01 10:00:00",
                "createBy": "admin",
                "updateBy": "admin",
                "isDeleted": "0"
            }
        ]
    }
}
```

## 代码变更

### 1. 新增InvoiceDetailResponse DTO
```java
/**
 * 发票详情响应DTO
 */
@Getter
@Setter
public class InvoiceDetailResponse {
    
    /**
     * 发票信息
     */
    private RecycleInvoice invoice;
    
    /**
     * 发票明细列表
     */
    private List<RecycleInvoiceDetail> details;
}
```

### 2. RecycleInvoiceService 新增方法
```java
/**
 * 根据ID获取发票完整信息（包含明细）
 * @param invoiceId 发票ID
 * @return 发票完整信息
 */
public InvoiceDetailResponse getInvoiceWithDetails(String invoiceId) {
    // 获取发票基本信息
    RecycleInvoice invoice = recycleInvoiceMapper.selectById(invoiceId);
    if (invoice == null) {
        return null;
    }
    
    // 获取发票明细
    List<RecycleInvoiceDetail> details = getInvoiceDetails(invoiceId);
    
    // 构建返回对象
    InvoiceDetailResponse response = new InvoiceDetailResponse();
    response.setInvoice(invoice);
    response.setDetails(details);
    
    return response;
}
```

### 3. RecycleInvoiceController 方法修改
```java
/**
 * 根据ID获取发票完整信息（包含明细）
 * @param invoiceId 发票ID
 * @return 发票完整信息
 */
@GetMapping("/get/{invoiceId}")
public BaseResponse<InvoiceDetailResponse> getInvoiceById(@PathVariable String invoiceId) {
    try {
        InvoiceDetailResponse invoiceWithDetails = recycleInvoiceService.getInvoiceWithDetails(invoiceId);
        if (invoiceWithDetails != null) {
            return BaseResponse.success(invoiceWithDetails);
        } else {
            return BaseResponse.error("发票不存在");
        }
    } catch (Exception e) {
        return BaseResponse.error("查询异常：" + e.getMessage());
    }
}
```

## 优势

### 1. 职责分离
- 创建发票使用CreateInvoiceRequest（请求DTO）
- 获取发票使用InvoiceDetailResponse（响应DTO）
- 明确区分请求和响应的数据结构

### 2. 减少接口调用
- 一次调用获取完整信息
- 减少网络请求次数

### 3. 数据完整性
- 确保获取的数据包含所有必要信息
- 避免数据不一致的问题

### 4. 易于维护
- 专门的响应DTO，便于扩展
- 减少代码耦合度

### 5. 语义清晰
- InvoiceDetailResponse明确表示这是发票详情响应
- 代码可读性更好

## 使用场景

### 1. 发票详情页面
- 显示发票基本信息和所有明细
- 支持编辑和更新操作

### 2. 发票审核
- 查看发票的完整信息
- 验证发票数据的准确性

### 3. 数据导出
- 导出发票的完整信息
- 包含所有相关的明细数据

## 注意事项

1. **性能考虑**：如果明细数据很多，可能影响查询性能
2. **数据一致性**：确保发票和明细数据的一致性
3. **错误处理**：妥善处理发票不存在的情况
4. **向后兼容**：保留了原有的getInvoiceById方法，确保兼容性
