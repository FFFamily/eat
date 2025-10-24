-- 为入库明细、出库明细、库存流水添加货物ID字段
-- 执行时间：2025-10-24

-- 1. 为入库明细表添加货物ID字段
ALTER TABLE inventory_in_item 
ADD COLUMN good_id VARCHAR(64) COMMENT '货物ID' AFTER in_id;

-- 为货物ID创建索引
CREATE INDEX idx_good_id ON inventory_in_item(good_id);

-- 2. 为出库明细表添加货物ID字段
ALTER TABLE inventory_out_item 
ADD COLUMN good_id VARCHAR(64) COMMENT '货物ID' AFTER out_id;

-- 为货物ID创建索引
CREATE INDEX idx_good_id ON inventory_out_item(good_id);

-- 3. 为库存流水表添加货物ID字段
ALTER TABLE inventory_transaction 
ADD COLUMN good_id VARCHAR(64) COMMENT '货物ID' AFTER warehouse_id;

-- 为货物ID创建索引
CREATE INDEX idx_good_id ON inventory_transaction(good_id);

-- 注意：
-- 1. 执行此SQL前请先备份数据库
-- 2. 对于已存在的数据，需要根据实际业务逻辑补充货物ID
-- 3. 如果系统中有货物表，可以通过 good_no 关联更新 good_id

