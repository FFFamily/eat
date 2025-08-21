# 经营范围排序功能使用示例

## 功能概述
经营范围排序功能允许用户对同一货物类型下的经营范围进行排序，支持上移和下移操作。

## 使用场景
1. 管理员需要调整经营范围在列表中的显示顺序
2. 根据业务重要性调整经营范围的优先级
3. 按照用户习惯重新排列经营范围

## 操作步骤

### 1. 查看当前排序
```bash
# 获取所有经营范围（按排序号排序）
GET /recycle/business-scope/list

# 获取特定类型的经营范围
GET /recycle/business-scope/type/废金属
```

### 2. 上移经营范围
```bash
# 将指定ID的经营范围向上移动一位
PUT /recycle/business-scope/move-up/{id}
```

**示例：**
```bash
curl -X PUT http://localhost:8080/recycle/business-scope/move-up/BS002
```

### 3. 下移经营范围
```bash
# 将指定ID的经营范围向下移动一位
PUT /recycle/business-scope/move-down/{id}
```

**示例：**
```bash
curl -X PUT http://localhost:8080/recycle/business-scope/move-down/BS001
```

## 前端集成示例

### Vue.js 示例
```vue
<template>
  <div>
    <el-table :data="businessScopeList" style="width: 100%">
      <el-table-column prop="goodName" label="货物名称"></el-table-column>
      <el-table-column prop="goodType" label="货物类型"></el-table-column>
      <el-table-column prop="sortNum" label="排序号"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button 
            size="small" 
            @click="moveUp(scope.row.id)"
            :disabled="scope.$index === 0"
          >
            上移
          </el-button>
          <el-button 
            size="small" 
            @click="moveDown(scope.row.id)"
            :disabled="scope.$index === businessScopeList.length - 1"
          >
            下移
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  setup() {
    const businessScopeList = ref([])

    // 获取经营范围列表
    const getBusinessScopeList = async () => {
      try {
        const response = await fetch('/recycle/business-scope/list')
        const result = await response.json()
        if (result.code === 200) {
          businessScopeList.value = result.data
        }
      } catch (error) {
        ElMessage.error('获取数据失败')
      }
    }

    // 上移操作
    const moveUp = async (id) => {
      try {
        const response = await fetch(`/recycle/business-scope/move-up/${id}`, {
          method: 'PUT'
        })
        const result = await response.json()
        if (result.code === 200) {
          ElMessage.success('上移成功')
          await getBusinessScopeList()
        } else {
          ElMessage.error(result.message)
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    // 下移操作
    const moveDown = async (id) => {
      try {
        const response = await fetch(`/recycle/business-scope/move-down/${id}`, {
          method: 'PUT'
        })
        const result = await response.json()
        if (result.code === 200) {
          ElMessage.success('下移成功')
          await getBusinessScopeList()
        } else {
          ElMessage.error(result.message)
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    onMounted(() => {
      getBusinessScopeList()
    })

    return {
      businessScopeList,
      moveUp,
      moveDown
    }
  }
}
</script>
```

### React 示例
```jsx
import React, { useState, useEffect } from 'react'
import { Table, Button, message } from 'antd'

const BusinessScopeList = () => {
  const [businessScopeList, setBusinessScopeList] = useState([])
  const [loading, setLoading] = useState(false)

  // 获取经营范围列表
  const getBusinessScopeList = async () => {
    setLoading(true)
    try {
      const response = await fetch('/recycle/business-scope/list')
      const result = await response.json()
      if (result.code === 200) {
        setBusinessScopeList(result.data)
      }
    } catch (error) {
      message.error('获取数据失败')
    } finally {
      setLoading(false)
    }
  }

  // 上移操作
  const moveUp = async (id) => {
    try {
      const response = await fetch(`/recycle/business-scope/move-up/${id}`, {
        method: 'PUT'
      })
      const result = await response.json()
      if (result.code === 200) {
        message.success('上移成功')
        await getBusinessScopeList()
      } else {
        message.error(result.message)
      }
    } catch (error) {
      message.error('操作失败')
    }
  }

  // 下移操作
  const moveDown = async (id) => {
    try {
      const response = await fetch(`/recycle/business-scope/move-down/${id}`, {
        method: 'PUT'
      })
      const result = await response.json()
      if (result.code === 200) {
        message.success('下移成功')
        await getBusinessScopeList()
      } else {
        message.error(result.message)
      }
    } catch (error) {
      message.error('操作失败')
    }
  }

  const columns = [
    {
      title: '货物名称',
      dataIndex: 'goodName',
      key: 'goodName',
    },
    {
      title: '货物类型',
      dataIndex: 'goodType',
      key: 'goodType',
    },
    {
      title: '排序号',
      dataIndex: 'sortNum',
      key: 'sortNum',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record, index) => (
        <div>
          <Button 
            size="small" 
            onClick={() => moveUp(record.id)}
            disabled={index === 0}
            style={{ marginRight: 8 }}
          >
            上移
          </Button>
          <Button 
            size="small" 
            onClick={() => moveDown(record.id)}
            disabled={index === businessScopeList.length - 1}
          >
            下移
          </Button>
        </div>
      ),
    },
  ]

  useEffect(() => {
    getBusinessScopeList()
  }, [])

  return (
    <Table 
      columns={columns} 
      dataSource={businessScopeList} 
      loading={loading}
      rowKey="id"
    />
  )
}

export default BusinessScopeList
```

## 注意事项

1. **分组排序**：排序功能只在同一货物类型内有效，不同货物类型之间不会相互影响
2. **边界检查**：第一个项目无法上移，最后一个项目无法下移
3. **自动刷新**：操作成功后建议刷新列表以显示最新排序
4. **错误处理**：需要处理网络错误和业务逻辑错误
5. **用户体验**：建议在操作按钮上添加loading状态，防止重复点击

## 数据库迁移

如果您的数据库已经存在数据，请执行迁移脚本：

```sql
-- 执行迁移脚本
source sql/add_sort_num_to_business_scope.sql
```

或者手动执行：

```sql
-- 添加排序字段
ALTER TABLE `business_scope` ADD COLUMN `sort_num` int DEFAULT 0 COMMENT '排序号' AFTER `no`;

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
``` 