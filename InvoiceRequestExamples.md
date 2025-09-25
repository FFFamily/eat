# 发票API请求对象示例

## 概述
本文档提供了发票API中使用的请求对象的详细示例和说明。

## 请求对象类

### 1. InvoiceListRequest - 发票列表查询请求

**类路径：** `com.tutu.recycle.request.InvoiceListRequest`

**用途：** 用于发票列表查询接口的参数传递

**字段说明：**
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | String | 否 | 发票状态过滤（pending-待开票，invoiced-已开票） |
| invoiceType | String | 否 | 发票类型过滤（进项、销项） |

**请求示例：**

#### 查询所有发票
```json
{
}
```

#### 按状态过滤
```json
{
  "status": "pending"
}
```

#### 按类型过滤
```json
{
  "invoiceType": "进项"
}
```

#### 组合过滤
```json
{
  "status": "invoiced",
  "invoiceType": "销项"
}
```

### 2. InvoiceDetailRequest - 发票详情查询请求

**类路径：** `com.tutu.recycle.request.InvoiceDetailRequest`

**用途：** 用于发票详情查询接口的参数传递

**字段说明：**
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| invoiceId | String | 是 | 发票ID |

**请求示例：**

#### 查询发票详情
```json
{
  "invoiceId": "123456789"
}
```

## 完整的API调用示例

### 1. 获取发票列表

#### JavaScript示例
```javascript
// 查询所有发票
async function getAllInvoices() {
    const response = await fetch('/wx/invoice/current/list', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({})
    });
    return await response.json();
}

// 按状态查询发票
async function getInvoicesByStatus(status) {
    const response = await fetch('/wx/invoice/current/list', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({
            status: status
        })
    });
    return await response.json();
}

// 组合条件查询
async function getInvoicesByCondition(status, invoiceType) {
    const requestBody = {};
    if (status) requestBody.status = status;
    if (invoiceType) requestBody.invoiceType = invoiceType;
    
    const response = await fetch('/wx/invoice/current/list', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify(requestBody)
    });
    return await response.json();
}
```

#### Java示例
```java
// 创建请求对象
InvoiceListRequest request = new InvoiceListRequest();
request.setStatus("pending");
request.setInvoiceType("进项");

// 发送请求
String url = "/wx/invoice/current/list";
String requestBody = JSON.toJSONString(request);

// 使用HttpClient或其他HTTP客户端发送POST请求
```

#### cURL示例
```bash
# 查询所有发票
curl -X POST http://localhost:8080/wx/invoice/current/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{}'

# 按状态查询
curl -X POST http://localhost:8080/wx/invoice/current/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "status": "pending"
  }'

# 组合条件查询
curl -X POST http://localhost:8080/wx/invoice/current/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "status": "invoiced",
    "invoiceType": "销项"
  }'
```

### 2. 获取发票详情

#### JavaScript示例
```javascript
async function getInvoiceDetail(invoiceId) {
    const response = await fetch('/wx/invoice/detail', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({
            invoiceId: invoiceId
        })
    });
    return await response.json();
}
```

#### cURL示例
```bash
curl -X POST http://localhost:8080/wx/invoice/detail \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "invoiceId": "123456789"
  }'
```

### 3. 获取发票状态枚举

#### JavaScript示例
```javascript
async function getInvoiceStatuses() {
    const response = await fetch('/wx/invoice/status/all', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({})
    });
    return await response.json();
}
```

#### cURL示例
```bash
curl -X POST http://localhost:8080/wx/invoice/status/all \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{}'
```

## 错误处理示例

### JavaScript错误处理
```javascript
async function getInvoicesWithErrorHandling() {
    try {
        const response = await fetch('/wx/invoice/current/list', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({
                status: 'pending'
            })
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (data.code === 200) {
            console.log('发票列表：', data.data);
            return data.data;
        } else {
            console.error('业务错误：', data.msg);
            throw new Error(data.msg);
        }
    } catch (error) {
        console.error('请求失败：', error);
        throw error;
    }
}
```

## 注意事项

1. **请求头设置**：所有请求都需要设置 `Content-Type: application/json`
2. **认证信息**：需要在请求头中包含有效的认证token
3. **参数验证**：后端会验证请求参数的合法性
4. **空请求体**：对于不需要参数的接口，可以发送空的JSON对象 `{}`
5. **错误处理**：建议在前端实现完整的错误处理逻辑

## 版本兼容性

- **v2.2+**：使用POST请求和请求体传参
- **v2.1及以下**：使用GET请求和URL参数传参

建议升级到最新版本以获得更好的安全性和扩展性。
