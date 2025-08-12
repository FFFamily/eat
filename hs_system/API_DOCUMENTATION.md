# 合同子项API文档

## 概述
合同子项管理模块，提供合同子项的完整CRUD操作，支持回收货物和租赁设备的管理。

## 基础信息
- **基础路径**: `/system/contract-item`
- **实体类**: `SysContractItem`
- **Service**: `SysContractItemService`
- **Mapper**: `SysContractItemMapper`

## API接口

### 1. 基础CRUD操作

#### 1.1 新增合同子项
- **接口**: `POST /system/contract-item/add`
- **参数**: `SysContractItem` 对象
- **返回**: `BaseResponse<Boolean>`

#### 1.2 修改合同子项
- **接口**: `PUT /system/contract-item/update`
- **参数**: `SysContractItem` 对象（必须包含ID）
- **返回**: `BaseResponse<Boolean>`

#### 1.3 删除合同子项
- **接口**: `DELETE /system/contract-item/delete/{id}`
- **参数**: `id` - 合同子项ID
- **返回**: `BaseResponse<Boolean>`

#### 1.4 根据ID查询
- **接口**: `GET /system/contract-item/get/{id}`
- **参数**: `id` - 合同子项ID
- **返回**: `BaseResponse<SysContractItem>`

#### 1.5 分页查询
- **接口**: `GET /system/contract-item/page`
- **参数**: 
  - `pageNum` - 页码（默认1）
  - `pageSize` - 每页大小（默认10）
  - `contractId` - 合同ID（可选）
  - `recycleGoodName` - 回收货物名称（可选，模糊查询）
  - `leaseGoodName` - 租赁设备名称（可选，模糊查询）
  - `recycleGoodId` - 回收货物ID（可选）
  - `leaseGoodId` - 租赁设备ID（可选）
- **返回**: `BaseResponse<IPage<SysContractItem>>`

### 2. 业务查询操作

#### 2.1 根据合同ID查询列表
- **接口**: `GET /system/contract-item/list/contract/{contractId}`
- **参数**: `contractId` - 合同ID
- **返回**: `BaseResponse<List<SysContractItem>>`

#### 2.2 根据回收货物ID查询
- **接口**: `GET /system/contract-item/list/recycle-good/{recycleGoodId}`
- **参数**: `recycleGoodId` - 回收货物ID
- **返回**: `BaseResponse<List<SysContractItem>>`

#### 2.3 根据租赁设备ID查询
- **接口**: `GET /system/contract-item/list/lease-good/{leaseGoodId}`
- **参数**: `leaseGoodId` - 租赁设备ID
- **返回**: `BaseResponse<List<SysContractItem>>`

#### 2.4 根据合同ID统计数量
- **接口**: `GET /system/contract-item/count/contract/{contractId}`
- **参数**: `contractId` - 合同ID
- **返回**: `BaseResponse<Long>`

#### 2.5 根据合同ID获取回收货物列表
- **接口**: `GET /system/contract-item/recycle-goods/contract/{contractId}`
- **参数**: `contractId` - 合同ID
- **返回**: `BaseResponse<List<SysContractItem>>`

#### 2.6 根据合同ID获取租赁设备列表
- **接口**: `GET /system/contract-item/lease-goods/contract/{contractId}`
- **参数**: `contractId` - 合同ID
- **返回**: `BaseResponse<List<SysContractItem>>`

### 3. 批量操作

#### 3.1 批量保存
- **接口**: `POST /system/contract-item/batch-save`
- **参数**: `List<SysContractItem>` 合同子项列表
- **返回**: `BaseResponse<Boolean>`

#### 3.2 根据合同ID批量删除
- **接口**: `DELETE /system/contract-item/delete/contract/{contractId}`
- **参数**: `contractId` - 合同ID
- **返回**: `BaseResponse<Boolean>`

### 4. 其他操作

#### 4.1 获取所有列表
- **接口**: `GET /system/contract-item/list`
- **参数**: 无
- **返回**: `BaseResponse<List<SysContractItem>>`

## 实体字段说明

### 基础字段
- `id` - 主键ID
- `contractId` - 合同ID

### 回收货物信息
- `recycleGoodId` - 回收货物ID
- `recycleGoodName` - 回收货物名称
- `recycleGoodSpecificationModel` - 回收货物规格型号
- `recycleGoodTransportMode` - 回收货物运输模式
- `recycleGoodSubtotal` - 货物金额

### 租赁设备信息
- `leaseGoodId` - 租赁设备ID
- `leaseGoodName` - 租赁设备名称
- `leaseGoodDeposit` - 租赁设备押金
- `leaseGoodSubtotal` - 租赁设备金额
- `leaseGoodInstallDate` - 租赁设备安装日期

## 使用示例

### 新增合同子项
```json
POST /system/contract-item/add
{
  "contractId": "contract123",
  "recycleGoodId": "recycle456",
  "recycleGoodName": "废旧钢材",
  "recycleGoodSpecificationModel": "Q235",
  "recycleGoodTransportMode": "公路运输",
  "recycleGoodSubtotal": 5000.00
}
```

### 分页查询
```
GET /system/contract-item/page?pageNum=1&pageSize=10&contractId=contract123&recycleGoodName=钢材
```

### 根据合同ID查询
```
GET /system/contract-item/list/contract/contract123
```

## 注意事项

1. 所有接口都使用统一的响应格式 `BaseResponse<T>`
2. 分页查询支持多条件组合查询
3. 批量操作支持事务回滚
4. 查询结果按ID倒序排列
5. 支持逻辑删除（通过 `isDeleted` 字段） 