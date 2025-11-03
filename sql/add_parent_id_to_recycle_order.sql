-- 为回收订单表添加父订单ID字段
-- 用于关联用户订单(user_order)和回收订单(recycle_order)

ALTER TABLE `recycle_order` 
ADD COLUMN `parent_id` VARCHAR(64) COMMENT '父订单ID（关联用户订单）' AFTER `id`;

-- 添加父订单ID索引，提高查询性能
ALTER TABLE `recycle_order`
ADD INDEX `idx_parent_id` (`parent_id`);

-- 添加唯一索引，确保一个父订单只对应一个回收订单
-- 注意：如果已有数据存在重复的parent_id，需要先清理数据再创建唯一索引
-- ALTER TABLE `recycle_order`
-- ADD UNIQUE INDEX `uk_parent_id` (`parent_id`);

/*
使用说明:
1. parent_id 字段用于存储父订单（用户订单）的ID
2. 当创建用户订单时，会自动创建一个采购类型的回收订单，并设置parent_id
3. 通过parent_id可以查询到对应的回收订单
4. 同一个parent_id下只应该有一个回收订单
5. 如果查询到多个回收订单，说明数据异常，需要联系管理员修正

业务规则:
- 一个用户订单(UserOrder) -> 一个回收订单(RecycleOrder)
- 通过parent_id建立关联关系
- parent_id为空的回收订单表示独立创建的订单（不是从用户订单自动生成的）

API接口:
- GET /recycle/order/parent/{parentId} - 根据父订单ID查询回收订单
*/

