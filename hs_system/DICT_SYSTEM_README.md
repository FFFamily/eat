# 系统字典模块使用说明

## 概述

系统字典模块提供了统一的字典管理功能，包括字典类型和字典数据的管理。

## 目录结构

```
hs_system/
├── sql/
│   └── sys_dict_tables.sql                    # 数据库建表SQL
├── src/main/java/com/tutu/system/
│   ├── entity/dict/
│   │   ├── SysDictType.java                   # 字典类型实体
│   │   └── SysDictData.java                   # 字典数据实体
│   ├── dto/
│   │   ├── SysDictTypeDTO.java                # 字典类型DTO
│   │   └── SysDictDataDTO.java                # 字典数据DTO
│   ├── mapper/
│   │   ├── SysDictTypeMapper.java             # 字典类型Mapper
│   │   └── SysDictDataMapper.java             # 字典数据Mapper
│   └── service/
│       ├── SysDictTypeService.java            # 字典类型服务
│       └── SysDictDataService.java            # 字典数据服务

hs_api/
└── src/main/java/com/tutu/api/controller/dict/
    ├── SysDictTypeController.java             # 字典类型控制器
    └── SysDictDataController.java             # 字典数据控制器
```

## 数据库初始化

1. 执行建表SQL：
```bash
mysql -u username -p database_name < hs_system/sql/sys_dict_tables.sql
```

2. 表结构说明：
   - `sys_dict_type`：字典类型表，存储字典分类信息
   - `sys_dict_data`：字典数据表，存储具体的字典项

## API 接口说明

### 字典类型接口

**基础路径**: `/system/dict/type`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/page` | 分页查询字典类型列表 |
| GET | `/all` | 查询所有启用的字典类型 |
| GET | `/{id}` | 根据ID查询字典类型详情 |
| GET | `/info/{dictType}` | 根据字典类型查询详情 |
| POST | `/` | 创建字典类型 |
| PUT | `/` | 更新字典类型 |
| DELETE | `/{id}` | 删除字典类型 |
| DELETE | `/batch` | 批量删除字典类型 |

### 字典数据接口

**基础路径**: `/system/dict/data`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/page` | 分页查询字典数据列表 |
| GET | `/type/{dictType}` | 根据字典类型查询数据 |
| GET | `/{id}` | 根据ID查询字典数据详情 |
| GET | `/label` | 根据字典类型和值查询标签 |
| GET | `/value` | 根据字典类型和标签查询值 |
| POST | `/` | 创建字典数据 |
| PUT | `/` | 更新字典数据 |
| DELETE | `/{id}` | 删除字典数据 |
| DELETE | `/batch` | 批量删除字典数据 |
| PUT | `/{id}/default` | 设置默认字典数据 |

## 使用示例

### 1. 创建字典类型

```bash
POST /system/dict/type
Content-Type: application/json

{
  "dictName": "用户性别",
  "dictType": "sys_user_sex",
  "status": 1,
  "remark": "用户性别列表"
}
```

### 2. 创建字典数据

```bash
POST /system/dict/data
Content-Type: application/json

{
  "dictSort": 1,
  "dictLabel": "男",
  "dictValue": "0",
  "dictType": "sys_user_sex",
  "status": 1,
  "isDefault": "1",
  "remark": "性别男"
}
```

### 3. 根据字典类型查询数据

```bash
GET /system/dict/data/type/sys_user_sex
```

### 4. 分页查询

```bash
GET /system/dict/type/page?current=1&size=10&keyword=用户
```

## 功能特性

1. **基础 CRUD 操作**
   - 支持字典类型和字典数据的增删改查
   - 支持分页查询和条件搜索

2. **数据校验**
   - 创建时检查字典类型/值是否已存在
   - 删除字典类型时检查是否有关联的字典数据
   - 支持批量操作

3. **字典数据管理**
   - 支持设置默认值
   - 支持排序
   - 支持启用/禁用状态
   - 支持样式属性扩展

4. **逻辑删除**
   - 所有删除操作采用逻辑删除
   - 数据可恢复

## 注意事项

1. 删除字典类型前需要先删除该类型下的所有字典数据
2. 字典类型的 `dictType` 字段在同一系统中必须唯一
3. 同一字典类型下的 `dictValue` 必须唯一
4. 建议使用英文下划线命名字典类型，如：`sys_user_sex`

## 技术栈

- MyBatis-Plus：数据库操作
- Spring Boot：Web 框架
- Jakarta Validation：参数校验
- Lombok：简化代码

## 扩展建议

如需要扩展功能，可以考虑：
1. 添加字典缓存（Redis）
2. 添加字典数据导入导出功能
3. 添加字典数据国际化支持
4. 添加字典使用统计功能

