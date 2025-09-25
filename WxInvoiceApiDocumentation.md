# 微信端发票API文档

## 概述
本文档描述了微信端发票相关的API接口，用于查看当前用户的发票列表和详情。

## 重要更新
**v2.0 更新说明：** 发票实体新增了`invoiceAccountId`字段，用于直接关联用户账号。现在发票查询更加简单高效，不再需要通过经办人进行复杂的关联查询。

**v2.2 更新说明：** 所有发票接口改为POST请求，参数通过请求体传递，提供更好的安全性和扩展性。

## 接口列表

### 1. 获取当前用户的发票列表

**接口地址：** `POST /wx/invoice/current/list`

**功能描述：** 获取当前登录用户的发票列表，支持状态过滤

**请求参数：**
```json
{
  "status": "pending",
  "invoiceType": "进项"
}
```

**请求参数说明：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | String | 否 | 发票状态过滤（pending-待开票，invoiced-已开票） |
| invoiceType | String | 否 | 发票类型过滤（进项、销项） |

**请求示例：**
```bash
curl -X POST http://localhost:8080/wx/invoice/current/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "status": "pending"
  }'
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": "123456789",
      "invoiceNo": "INV001",
      "invoiceType": "进项",
      "invoiceBank": "中国银行",
      "plannedInvoiceTime": "2024-01-15 10:00:00",
      "status": "pending",
      "processor": "张三",
      "invoiceTime": null,
      "totalAmount": 10000.00,
      "taxAmount": 1300.00,
      "amountWithoutTax": 8700.00,
      "invoiceAccountId": "user123456",
      "createTime": "2024-01-10 09:00:00",
      "updateTime": "2024-01-10 09:00:00"
    },
    {
      "id": "123456790",
      "invoiceNo": "INV002",
      "invoiceType": "销项",
      "invoiceBank": "工商银行",
      "plannedInvoiceTime": "2024-01-16 14:00:00",
      "status": "invoiced",
      "processor": "李四",
      "invoiceTime": "2024-01-16 14:30:00",
      "totalAmount": 15000.00,
      "taxAmount": 1950.00,
      "amountWithoutTax": 13050.00,
      "invoiceAccountId": "user123456",
      "createTime": "2024-01-11 10:00:00",
      "updateTime": "2024-01-16 14:30:00"
    }
  ]
}
```

### 2. 获取发票详情

**接口地址：** `POST /wx/invoice/detail`

**功能描述：** 根据发票ID获取发票详情，包含发票明细信息

**请求参数：**
```json
{
  "invoiceId": "123456789"
}
```

**请求参数说明：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| invoiceId | String | 是 | 发票ID |

**请求示例：**
```bash
curl -X POST http://localhost:8080/wx/invoice/detail \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "invoiceId": "123456789"
  }'
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "invoice": {
      "id": "123456789",
      "invoiceNo": "INV001",
      "invoiceType": "进项",
      "invoiceBank": "中国银行",
      "plannedInvoiceTime": "2024-01-15 10:00:00",
      "status": "pending",
      "processor": "张三",
      "invoiceTime": null,
      "totalAmount": 10000.00,
      "taxAmount": 1300.00,
      "amountWithoutTax": 8700.00,
      "invoiceAccountId": "user123456",
      "createTime": "2024-01-10 09:00:00",
      "updateTime": "2024-01-10 09:00:00"
    },
    "details": [
      {
        "id": "detail001",
        "invoiceId": "123456789",
        "orderId": "order001",
        "orderNo": "ORD001",
        "orderTotalAmount": 10000.00,
        "orderActualInvoice": 10000.00,
        "orderShouldInvoice": 10000.00,
        "orderPartner": "合作方A",
        "orderPartnerName": "合作方A公司",
        "orderType": "运输"
      }
    ]
  }
}
```

### 3. 获取所有发票状态

**接口地址：** `POST /wx/invoice/status/all`

**功能描述：** 获取所有可用的发票状态枚举

**请求参数：**
```json
{}
```

**请求示例：**
```bash
curl -X POST http://localhost:8080/wx/invoice/status/all \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{}'
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "value": "pending",
      "label": "待开票"
    },
    {
      "value": "invoiced",
      "label": "已开票"
    }
  ]
}
```

## 错误响应

当请求失败时，会返回错误信息：

```json
{
  "code": 500,
  "msg": "查询发票列表失败：具体错误信息",
  "data": null
}
```

## 权限说明

1. 所有接口都需要用户登录，通过Sa-Token进行身份验证
2. 用户只能查看自己的发票（通过`invoiceAccountId`字段直接关联）
3. 发票详情接口会验证发票是否属于当前用户，无权限访问时会返回错误

## 数据关联说明

**v2.0 更新后的关联关系：**
- 发票(RecycleInvoice)通过`invoiceAccountId`字段直接关联到用户(Account)
- 简化了数据关联路径，提高了查询效率
- 不再需要通过经办人进行复杂的关联查询

**字段说明：**
- `invoiceAccountId`: 发票所属用户的账号ID，直接关联用户表
- `processor`: 经办人信息，用于业务处理记录

## 使用示例

### JavaScript/Ajax示例

```javascript
// 获取发票列表
function getInvoiceList(status = null, invoiceType = null) {
    const requestBody = {};
    if (status) {
        requestBody.status = status;
    }
    if (invoiceType) {
        requestBody.invoiceType = invoiceType;
    }
    
    fetch('/wx/invoice/current/list', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + getToken(), // 获取登录token
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            console.log('发票列表：', data.data);
        } else {
            console.error('获取发票列表失败：', data.msg);
        }
    })
    .catch(error => {
        console.error('请求失败：', error);
    });
}

// 获取发票详情
function getInvoiceDetail(invoiceId) {
    fetch('/wx/invoice/detail', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            invoiceId: invoiceId
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            console.log('发票详情：', data.data);
        } else {
            console.error('获取发票详情失败：', data.msg);
        }
    })
    .catch(error => {
        console.error('请求失败：', error);
    });
}
```

## 注意事项

1. 所有金额字段使用BigDecimal类型，确保精度
2. 时间字段格式为：`yyyy-MM-dd HH:mm:ss`
3. 发票列表按创建时间倒序排列
4. 状态过滤支持精确匹配
5. 发票类型过滤支持精确匹配
6. 接口支持跨域请求（CORS）
7. **v2.0重要更新**：发票查询现在直接通过`invoiceAccountId`字段关联用户，查询效率更高
8. 确保在创建发票时正确设置`invoiceAccountId`字段
9. **v2.1更新**：移除了分页功能，直接返回所有符合条件的发票列表
10. **v2.2更新**：所有接口改为POST请求，参数通过请求体传递，提供更好的安全性
