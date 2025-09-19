# 库存统计报表分页调试指南

## 问题分析

当前的SQL查询确实没有显式实现分页，但MyBatis-Plus的分页插件应该能够自动处理。让我们验证分页是否正常工作。

## 调试步骤

### 1. 检查MyBatis-Plus分页插件配置

确认`MybatisPlusConfig.java`中分页插件已正确配置：

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}
```

### 2. 验证分页查询

MyBatis-Plus分页插件会自动将以下查询：
```sql
SELECT ... FROM ... GROUP BY ... ORDER BY ...
```

转换为：
```sql
SELECT ... FROM ... GROUP BY ... ORDER BY ... LIMIT ?, ?
```

### 3. 测试分页功能

#### 测试用例1：基本分页
```bash
# 第1页，每页5条
GET /inventory/report/list?page=1&size=5

# 第2页，每页5条  
GET /inventory/report/list?page=2&size=5
```

#### 测试用例2：验证分页信息
检查响应中的分页信息：
```json
{
  "data": {
    "records": [...],  // 当前页数据
    "total": 25,       // 总记录数
    "size": 5,         // 每页大小
    "current": 1,      // 当前页码
    "pages": 5         // 总页数
  }
}
```

### 4. 如果分页不工作

如果MyBatis-Plus分页插件没有自动添加LIMIT子句，我们需要手动实现分页。

#### 方案A：使用子查询分页
```xml
<select id="selectInventoryReport" resultMap="InventoryReportMap">
    SELECT * FROM (
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
    ) AS inventory_summary
</select>
```

#### 方案B：使用参数化分页
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

### 5. 验证方法

1. **查看SQL日志**：启用MyBatis SQL日志，查看实际执行的SQL是否包含LIMIT子句
2. **测试不同页码**：确保不同页码返回不同的数据
3. **验证总数**：确保total字段正确反映总记录数

### 6. 配置SQL日志

在`application.yml`中添加：
```yaml
logging:
  level:
    com.tutu.recycle.mapper: debug
```

或者在`application.properties`中添加：
```properties
logging.level.com.tutu.recycle.mapper=debug
```

## 结论

MyBatis-Plus分页插件应该能够自动处理XML查询的分页，但需要确保：
1. 分页插件正确配置
2. 使用Page对象作为参数
3. 查询结构支持分页

如果自动分页不工作，我们可以使用手动分页方案。
