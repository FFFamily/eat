# 合同更新日志使用说明

## 概述
合同更新日志模块用于记录合同的所有操作历史，包括字段变更、操作类型、操作人等信息，提供完整的审计追踪功能。

## 核心功能

### 1. 记录字段变更日志
当合同字段发生变化时，自动记录变更前后的值。

### 2. 记录操作日志
记录合同的各种操作类型，如创建、审核、签署等。

### 3. 查询审计历史
支持按合同ID、操作类型、操作人等条件查询操作历史。

## 使用方法

### 1. 在合同Service中注入日志Service

```java
@Service
public class ContractService {
    
    @Autowired
    private SysContractUpdateLogService contractUpdateLogService;
    
    @Autowired
    private HttpServletRequest request;
    
    // 更新合同时记录日志
    public boolean updateContract(Contract contract, String operatorId, String operatorName, String changeReason) {
        // 获取更新前的合同数据
        Contract oldContract = getById(contract.getId());
        
        // 执行更新操作
        boolean result = updateById(contract);
        
        if (result) {
            // 记录字段变更日志
            recordFieldChanges(oldContract, contract, operatorId, operatorName, changeReason);
        }
        
        return result;
    }
    
    // 记录字段变更
    private void recordFieldChanges(Contract oldContract, Contract newContract, 
                                  String operatorId, String operatorName, String changeReason) {
        // 记录合同名称变更
        if (!Objects.equals(oldContract.getContractName(), newContract.getContractName())) {
            contractUpdateLogService.recordContractUpdate(
                newContract.getId(),
                newContract.getContractNo(),
                "contractName",
                "合同名称",
                oldContract.getContractName(),
                newContract.getContractName(),
                operatorId,
                operatorName,
                changeReason,
                request
            );
        }
        
        // 记录合同金额变更
        if (!Objects.equals(oldContract.getContractAmount(), newContract.getContractAmount())) {
            contractUpdateLogService.recordContractUpdate(
                newContract.getId(),
                newContract.getContractNo(),
                "contractAmount",
                "合同金额",
                oldContract.getContractAmount() != null ? oldContract.getContractAmount().toString() : "",
                newContract.getContractAmount() != null ? newContract.getContractAmount().toString() : "",
                operatorId,
                operatorName,
                changeReason,
                request
            );
        }
        
        // 记录其他字段变更...
    }
    
    // 记录操作日志（不涉及字段变更）
    public boolean approveContract(String contractId, String operatorId, String operatorName, String reason) {
        // 执行审核操作
        boolean result = doApprove(contractId);
        
        if (result) {
            // 记录审核操作日志
            contractUpdateLogService.recordContractOperation(
                contractId,
                getContractNo(contractId),
                ContractOperationTypeEnum.APPROVE,
                operatorId,
                operatorName,
                reason,
                request
            );
        }
        
        return result;
    }
}
```

### 2. 查询合同操作历史

```java
// 查询指定合同的所有操作历史
List<SysContractUpdateLog> logs = contractUpdateLogService.getByContractId("contract123");

// 分页查询操作日志
Page<SysContractUpdateLog> page = new Page<>(1, 10);
SysContractUpdateLog queryLog = new SysContractUpdateLog();
queryLog.setContractId("contract123");
queryLog.setOperationType("UPDATE");
IPage<SysContractUpdateLog> result = contractUpdateLogService.getPage(page, queryLog);

// 统计操作次数
long count = contractUpdateLogService.countByContractId("contract123");
```

## API接口

### 基础路径: `/system/contract-update-log`

### 主要接口:

#### 1. 查询合同操作历史
```
GET /system/contract-update-log/list/contract/{contractId}
```

#### 2. 分页查询日志
```
GET /system/contract-update-log/page?pageNum=1&pageSize=10&contractId=xxx&operationType=UPDATE
```

#### 3. 统计操作次数
```
GET /system/contract-update-log/count/contract/{contractId}
```

#### 4. 查询日志详情
```
GET /system/contract-update-log/get/{id}
```

## 字段说明

### 操作类型 (operationType)
- `CREATE` - 创建
- `UPDATE` - 更新
- `DELETE` - 删除
- `APPROVE` - 审核
- `REJECT` - 驳回
- `SIGN` - 签署
- `ACTIVATE` - 生效
- `TERMINATE` - 终止

### 日志字段
- `contractId` - 合同ID
- `contractNo` - 合同编号
- `fieldName` - 变更字段名
- `fieldLabel` - 字段中文名
- `oldValue` - 变更前值
- `newValue` - 变更后值
- `changeReason` - 变更原因
- `operatorName` - 操作人姓名
- `operationTime` - 操作时间
- `operationIp` - 操作IP

## 最佳实践

### 1. 字段变更记录
- 只记录业务相关字段的变更
- 跳过系统字段（如createTime、updateTime等）
- 提供清晰的字段中文标签

### 2. 操作原因记录
- 每次操作都要记录变更原因
- 原因描述要具体明确
- 便于后续审计和问题追踪

### 3. 性能优化
- 日志记录使用异步方式（可选）
- 定期清理过期日志数据
- 合理设置日志保留期限

### 4. 安全考虑
- 记录操作人IP地址
- 记录用户代理信息
- 敏感信息脱敏处理

## 注意事项

1. **日志记录不影响主业务流程**：日志记录失败不应影响合同的正常操作
2. **数据一致性**：确保日志数据与合同数据的一致性
3. **存储空间**：定期清理过期日志，避免占用过多存储空间
4. **查询性能**：合理设置索引，优化查询性能
5. **隐私保护**：注意敏感信息的保护，避免泄露 