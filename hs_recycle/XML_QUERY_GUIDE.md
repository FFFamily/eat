# XML查询改造指南

## 概述

本次改造将 `RecycleFundController` 中的分页查询方法从使用 MyBatis-Plus 的 `QueryWrapper` 改为使用 XML 构建 SQL 的形式。这样可以提供更灵活的查询条件和更好的性能。

## 改造内容

### 1. 新增 XML 映射文件

创建了 `RecycleFundMapper.xml` 文件，包含以下内容：

- **基础结果映射** (`BaseResultMap`)：映射所有基础字段
- **带合同信息的结果映射** (`FundWithContractResultMap`)：继承基础映射，增加合同开始和结束时间
- **分页查询SQL** (`selectPageWithContract`)：使用 LEFT JOIN 关联合同表
- **统计总数SQL** (`selectCountWithContract`)：统计符合条件的记录总数

### 2. 修改 Mapper 接口

在 `RecycleFundMapper` 中添加了新的查询方法：

```java
/**
 * 分页查询走款记录（带合同信息）
 */
Page<RecycleFund> selectPageWithContract(Page<RecycleFund> page, @Param("query") RecycleFund query);

/**
 * 统计符合条件的记录总数
 */
long selectCountWithContract(@Param("query") RecycleFund query);
```

### 3. 修改 Service 层

在 `RecycleFundService` 中添加了新的分页查询方法：

```java
/**
 * 分页查询走款记录（带合同信息）
 */
public Page<RecycleFund> pageWithContract(Page<RecycleFund> page, RecycleFund query) {
    return baseMapper.selectPageWithContract(page, query);
}
```

### 4. 改造 Controller 层

将原来的复杂查询逻辑简化为：

```java
@PostMapping("/page")
public BaseResponse<Page<RecycleFund>> page(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestBody(required = false) RecycleFund query) {
    // 创建分页对象
    Page<RecycleFund> pageParam = new Page<>(page, size);
    
    // 使用XML查询，自动包含合同信息
    Page<RecycleFund> result = recycleFundService.pageWithContract(pageParam, query);
    
    return BaseResponse.success(result);
}
```

## 优势对比

### 改造前（使用 QueryWrapper）

```java
// 1. 先查询走款记录
Page<RecycleFund> result = recycleFundService.page(ipage, queryWrapper);

// 2. 再查询合同信息
List<String> ids = records.stream().map(RecycleFund::getContractNo).collect(Collectors.toList());
Map<String, RecycleContract> contractMap = recycleContractService.list(...);

// 3. 手动组装数据
result.getRecords().forEach(i -> {
    RecycleContract contract = contractMap.get(i.getContractNo());
    if (contract != null) {
        i.setContractStartTime(contract.getStartTime());
        i.setContractEndTime(contract.getEndTime());
    }
});
```

**问题：**
- 需要两次数据库查询
- 手动组装数据，代码复杂
- 无法利用数据库的 JOIN 优化
- 内存中处理数据，性能较差

### 改造后（使用 XML）

```java
// 一次查询，自动包含合同信息
Page<RecycleFund> result = recycleFundService.pageWithContract(pageParam, query);
```

**优势：**
- 只需要一次数据库查询
- 利用数据库 JOIN 优化
- 代码简洁，易于维护
- 性能更好，特别是数据量大时

## XML SQL 详解

### 1. 分页查询 SQL

```xml
<select id="selectPageWithContract" resultMap="FundWithContractResultMap">
    SELECT 
        f.*,
        c.start_time as contract_start_time,
        c.end_time as contract_end_time
    FROM recycle_fund f
    LEFT JOIN recycle_contract c ON f.contract_no = c.no
    <where>
        f.deleted = 0
        <if test="query.contractNo != null and query.contractNo != ''">
            AND f.contract_no LIKE CONCAT('%', #{query.contractNo}, '%')
        </if>
        <if test="query.orderNo != null and query.orderNo != ''">
            AND f.order_no LIKE CONCAT('%', #{query.orderNo}, '%')
        </if>
        <if test="query.partner != null and query.partner != ''">
            AND f.partner LIKE CONCAT('%', #{query.partner}, '%')
        </if>
        <if test="query.status != null">
            AND f.status = #{query.status}
        </if>
        <if test="query.fundDirection != null and query.fundDirection != ''">
            AND f.fund_direction = #{query.fundDirection}
        </if>
    </where>
    ORDER BY f.create_time DESC
</select>
```

**特点：**
- 使用 LEFT JOIN 关联合同表
- 动态 SQL，根据查询条件构建 WHERE 子句
- 自动排序，按创建时间倒序
- 支持模糊查询和精确查询

### 2. 查询条件支持

当前支持的查询条件：

| 字段 | 类型 | 说明 |
|------|------|------|
| contractNo | String | 合同编号（模糊查询） |
| orderNo | String | 订单编号（模糊查询） |
| partner | String | 合作方（模糊查询） |
| status | Integer | 状态（精确查询） |
| fundDirection | String | 资金方向（精确查询） |

### 3. 扩展查询条件

如需添加更多查询条件，可以在 XML 中添加：

```xml
<if test="query.processor != null and query.processor != ''">
    AND f.processor LIKE CONCAT('%', #{query.processor}, '%')
</if>
<if test="query.planPayTimeStart != null">
    AND f.plan_pay_time >= #{query.planPayTimeStart}
</if>
<if test="query.planPayTimeEnd != null">
    AND f.plan_pay_time <= #{query.planPayTimeEnd}
</if>
```

## 使用示例

### 1. 基础分页查询

```java
// 查询第1页，每页10条
Page<RecycleFund> page = new Page<>(1, 10);
Page<RecycleFund> result = recycleFundService.pageWithContract(page, null);
```

### 2. 带条件查询

```java
// 创建查询条件
RecycleFund query = new RecycleFund();
query.setContractNo("CONTRACT_001");
query.setStatus(1);

// 分页查询
Page<RecycleFund> page = new Page<>(1, 10);
Page<RecycleFund> result = recycleFundService.pageWithContract(page, query);
```

### 3. 前端调用示例

```javascript
// 分页查询走款记录
const response = await fetch('/recycle/fund/page?page=1&size=10', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        contractNo: 'CONTRACT_001',
        status: 1
    })
});

const result = await response.json();
console.log('查询结果：', result.data);
```

## 注意事项

### 1. 性能优化

- 确保 `contract_no` 字段有索引
- 避免在查询条件中使用过多的模糊查询
- 合理设置分页大小，避免一次性查询过多数据

### 2. 数据一致性

- 使用 LEFT JOIN 确保即使没有合同信息也能查询到走款记录
- 在业务逻辑中处理合同信息为空的情况

### 3. 扩展性

- 新增查询条件时，只需修改 XML 文件
- 新增关联表时，可以扩展 resultMap 和 SQL

## 总结

通过这次改造，我们实现了：

1. **性能提升**：从两次查询改为一次查询
2. **代码简化**：移除了复杂的数据组装逻辑
3. **维护性提升**：查询逻辑集中在 XML 文件中
4. **扩展性增强**：易于添加新的查询条件和关联表

这种改造方式特别适合需要关联查询的分页场景，是 MyBatis 的最佳实践之一。 