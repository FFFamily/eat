# 资金池查询优化说明

## 优化概述

本次优化主要针对资金池的查询方式进行了改进，将原来使用MyBatis-Plus的简单查询改为使用MyBatis XML配置的关联查询，以支持更复杂的业务需求。

## 主要变更

### 1. 创建了RecycleCapitalPoolMapper.xml文件

**文件位置**: `hs_recycle/src/main/resources/mapper/RecycleCapitalPoolMapper.xml`

**主要功能**:
- 实现了资金池与合同表(`recycle_contract`)的关联查询
- 实现了资金池与账户表(`account`)的关联查询
- 支持多种查询条件：编号、合同编号、合同名称、合作方、合作方名称、资金池方向等

**关键SQL查询**:
```sql
SELECT 
    p.*,
    c.no as contractNo,
    c.name as contractName,
    a.id_card_name as contractPartnerName
FROM recycle_capital_pool p
LEFT JOIN recycle_contract c ON p.contract_id = c.id
LEFT JOIN account a ON p.contract_partner = a.id
```

### 2. 扩展了RecycleCapitalPoolMapper接口

**新增方法**:
- `selectPageWithDetails()`: 分页查询资金池（带关联信息）
- `selectByIdWithDetails()`: 根据ID查询资金池详情
- `selectByContractNoWithDetails()`: 根据合同编号查询资金池详情
- `selectByContractIdWithDetails()`: 根据合同ID查询资金池详情
- `selectCountWithDetails()`: 统计总数

### 3. 优化了RecycleCapitalPoolService服务类

**新增方法**:
- `getByIdWithDetails()`: 根据ID获取资金池详情（带关联信息）
- `pageWithDetails()`: 分页查询资金池（带关联信息）

**优化方法**:
- `getByContractNo()`: 现在返回带合同和账户信息的完整数据
- `getByContractId()`: 现在返回带合同和账户信息的完整数据

### 4. 更新了RecycleCapitalPoolController控制器

**优化接口**:
- `GET /get/{id}`: 现在返回带合同和账户信息的完整数据
- `POST /page`: 现在支持更多查询条件，包括合同名称、合作方名称等

## 查询条件支持

现在分页查询支持以下条件：

| 字段 | 说明 | 查询方式 |
|------|------|----------|
| no | 资金池编号 | 模糊查询 |
| contractNo | 合同编号 | 精确查询 |
| contractName | 合同名称 | 模糊查询 |
| contractPartner | 合作方ID | 精确查询 |
| contractPartnerName | 合作方名称 | 模糊查询 |
| fundPoolDirection | 资金池方向 | 精确查询 |

## 数据关联说明

### 合同信息关联
- **关联字段**: `recycle_capital_pool.contract_id = recycle_contract.id`
- **获取字段**: 
  - `contractNo`: 合同编号
  - `contractName`: 合同名称

### 账户信息关联
- **关联字段**: `recycle_capital_pool.contract_partner = account.id`
- **获取字段**:
  - `contractPartnerName`: 合作方名称（从account.id_card_name获取）

## 性能优化

1. **减少N+1查询问题**: 通过JOIN查询一次性获取所有关联数据
2. **索引优化**: 建议在以下字段上创建索引：
   - `recycle_capital_pool.contract_id`
   - `recycle_capital_pool.contract_partner`
   - `recycle_contract.no`
   - `account.id`

## 向后兼容性

- 所有原有的API接口保持不变
- 返回的数据结构保持一致，只是增加了关联字段
- 原有的业务逻辑不受影响

## 使用示例

### 分页查询示例
```json
POST /recycle/capital-pool/page?page=1&size=10
{
  "contractName": "采购合同",
  "contractPartnerName": "张三",
  "fundPoolDirection": "收款"
}
```

### 响应数据示例
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": "123",
        "no": "CP001",
        "balance": 10000.00,
        "contractId": "456",
        "contractNo": "CT001",
        "contractName": "采购合同",
        "contractPartner": "789",
        "contractPartnerName": "张三",
        "fundPoolDirection": "收款"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  }
}
```

## 注意事项

1. 确保数据库中存在相应的表结构和数据
2. 建议在相关字段上创建适当的索引以提升查询性能
3. 如果合同表或账户表结构发生变化，需要相应更新XML中的查询语句