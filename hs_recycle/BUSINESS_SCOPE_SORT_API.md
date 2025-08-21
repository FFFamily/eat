# 经营范围排序功能API文档

## 概述
经营范围模块新增了排序功能，支持对同一货物类型下的经营范围进行上移和下移操作。

## 数据库变更
1. 在 `business_scope` 表中添加了 `sort_num` 字段（排序号）
2. 添加了相关索引以优化查询性能

## API接口

### 1. 获取排序后的经营范围列表
```
GET /recycle/business-scope/list
```
**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "BS001",
      "no": "BS2024001",
      "sortNum": 1,
      "goodType": "废金属",
      "goodName": "废铁",
      "goodModel": "普通废铁",
      "goodRemark": "含杂质不超过5%",
      "publicPrice": 2.50,
      "isShow": "1"
    }
  ]
}
```

### 2. 上移经营范围
```
PUT /recycle/business-scope/move-up/{id}
```
**参数：**
- `id`: 经营范围ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 3. 下移经营范围
```
PUT /recycle/business-scope/move-down/{id}
```
**参数：**
- `id`: 经营范围ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4. 根据货物类型获取排序后的经营范围
```
GET /recycle/business-scope/type/{goodType}
```
**参数：**
- `goodType`: 货物类型

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "BS001",
      "no": "BS2024001",
      "sortNum": 1,
      "goodType": "废金属",
      "goodName": "废铁",
      "goodModel": "普通废铁",
      "goodRemark": "含杂质不超过5%",
      "publicPrice": 2.50,
      "isShow": "1"
    }
  ]
}
```

## 功能特性

### 1. 自动排序
- 创建新经营范围时，如果没有指定排序号，会自动设置为当前类型下的最大排序号+1
- 删除经营范围后，会自动重新排序同类型的其他记录

### 2. 分组排序
- 排序功能按货物类型分组，同一类型内的经营范围可以相互排序
- 不同货物类型之间的排序是独立的

### 3. 边界检查
- 上移操作会检查是否为第一个，如果是则返回错误
- 下移操作会检查是否为最后一个，如果是则返回错误

### 4. 事务安全
- 所有排序操作都在事务中执行，确保数据一致性

## 前端集成建议

### 1. 列表展示
```javascript
// 获取排序后的列表
const getBusinessScopeList = async () => {
  const response = await fetch('/recycle/business-scope/list');
  const data = await response.json();
  return data.data;
};
```

### 2. 上移操作
```javascript
// 上移经营范围
const moveUp = async (id) => {
  const response = await fetch(`/recycle/business-scope/move-up/${id}`, {
    method: 'PUT'
  });
  const result = await response.json();
  if (result.code === 200) {
    // 刷新列表
    await refreshList();
  } else {
    alert(result.message);
  }
};
```

### 3. 下移操作
```javascript
// 下移经营范围
const moveDown = async (id) => {
  const response = await fetch(`/recycle/business-scope/move-down/${id}`, {
    method: 'PUT'
  });
  const result = await response.json();
  if (result.code === 200) {
    // 刷新列表
    await refreshList();
  } else {
    alert(result.message);
  }
};
```

## 数据库迁移

如果您的数据库已经存在 `business_scope` 表，请执行以下SQL脚本：

```sql
-- 添加排序字段
ALTER TABLE `business_scope` ADD COLUMN `sort_num` int DEFAULT 0 COMMENT '排序号' AFTER `no`;

-- 添加索引
ALTER TABLE `business_scope` ADD INDEX `idx_sort_num` (`sort_num`);
ALTER TABLE `business_scope` ADD INDEX `idx_sort_order` (`sort_num`, `good_type`);

-- 为现有数据设置排序号
UPDATE `business_scope` bs1 
SET `sort_num` = (
    SELECT COUNT(*) + 1 
    FROM `business_scope` bs2 
    WHERE bs2.`good_type` = bs1.`good_type` 
    AND bs2.`create_time` < bs1.`create_time`
    AND bs2.`is_deleted` = '0'
)
WHERE bs1.`is_deleted` = '0';

-- 处理NULL值
UPDATE `business_scope` SET `sort_num` = 1 WHERE `sort_num` IS NULL OR `sort_num` = 0;
```

## 注意事项

1. 排序功能只在同一货物类型内有效
2. 删除经营范围后会自动重新排序
3. 创建新经营范围时会自动分配排序号
4. 所有排序操作都有事务保护
5. 边界情况（第一个/最后一个）会有相应的错误提示 