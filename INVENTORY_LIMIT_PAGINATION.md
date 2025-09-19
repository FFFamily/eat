# 库存统计报表LIMIT分页实现

## 实现方案

您说得完全正确！我直接在SQL中使用LIMIT子句来实现分页，这是最直接和高效的方法。

## 核心实现

### 1. SQL查询直接使用LIMIT

```xml
<select id="selectInventoryReport" resultMap="InventoryReportMap">
    SELECT
        o.warehouse_address,
        i.good_no,
        i.good_name,
        i.good_type,
        i.good_model,
        SUM(i.good_count) AS total_quantity,
        SUM(i.good_total_price) AS total_amount,
        COUNT(DISTINCT o.id) AS order_count,
        AVG(i.good_price) AS average_price,
        MAX(o.create_time) AS last_in_time
    FROM recycle_order_item i
    LEFT JOIN recycle_order o ON o.id = i.recycle_order_id 
        AND o.is_deleted = 0 
        AND o.type = 'storage' 
        AND o.flow_direction = 'IN'
    WHERE i.is_deleted = 0
        AND o.id IS NOT NULL
    GROUP BY o.warehouse_address, i.good_no, i.good_name, i.good_type, i.good_model
    ORDER BY o.warehouse_address, i.good_no
    LIMIT #{offset}, #{limit}
</select>
```

### 2. Mapper接口使用参数

```java
List<InventoryReportDto> selectInventoryReport(@Param("offset") int offset, @Param("limit") int limit);
```

### 3. Service层手动计算分页

```java
public IPage<InventoryReportDto> getInventoryReport(int page, int size) {
    // 参数验证
    if (page < 1) page = 1;
    if (size < 1) size = 10;
    if (size > 100) size = 100;
    
    // 计算偏移量
    int offset = (page - 1) * size;
    
    // 获取总数
    long total = recycleOrderItemMapper.selectInventoryReportCount();
    
    // 使用LIMIT查询分页数据
    List<InventoryReportDto> records = recycleOrderItemMapper.selectInventoryReport(offset, size);
    
    // 创建分页对象
    Page<InventoryReportDto> resultPage = new Page<>(page, size);
    resultPage.setTotal(total);
    resultPage.setRecords(records);
    
    return resultPage;
}
```

## 优势

### 1. **直接高效**
- 直接在SQL中使用LIMIT，无需复杂的子查询
- 数据库直接处理分页，性能最优

### 2. **简单明了**
- 代码逻辑清晰，易于理解和维护
- 不依赖MyBatis-Plus分页插件的复杂逻辑

### 3. **完全控制**
- 完全控制分页逻辑
- 可以精确控制查询行为

## 测试验证

### 测试用例1：基本分页
```bash
# 第1页，每页5条
GET /inventory/report/list?page=1&size=5
# SQL: LIMIT 0, 5

# 第2页，每页5条
GET /inventory/report/list?page=2&size=5
# SQL: LIMIT 5, 5

# 第3页，每页5条
GET /inventory/report/list?page=3&size=5
# SQL: LIMIT 10, 5
```

### 测试用例2：参数验证
```bash
# 页码为0（自动调整为1）
GET /inventory/report/list?page=0&size=10
# SQL: LIMIT 0, 10

# 每页大小为0（自动调整为10）
GET /inventory/report/list?page=1&size=0
# SQL: LIMIT 0, 10

# 每页大小超过100（限制为100）
GET /inventory/report/list?page=1&size=200
# SQL: LIMIT 0, 100
```

## 响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "warehouseAddress": "仓库A",
        "goodNo": "GOOD001",
        "goodName": "废旧金属",
        "goodType": "金属类",
        "goodModel": "A型",
        "totalInQuantity": 100,
        "totalInAmount": 10000.00,
        "orderCount": 5,
        "averagePrice": 100.00,
        "lastInTime": "2024-01-15 10:30:00"
      }
    ],
    "total": 25,
    "size": 5,
    "current": 1,
    "pages": 5
  }
}
```

## 总结

通过直接在SQL中使用LIMIT子句，我们实现了：
- ✅ **真正的数据库分页**
- ✅ **简单直接的实现**
- ✅ **高效的查询性能**
- ✅ **完全的控制权**

这是最直接、最高效的分页实现方式！🎉
