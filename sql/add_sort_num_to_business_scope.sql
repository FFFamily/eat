-- =============================================
-- 为经营范围表添加排序字段的迁移脚本
-- =============================================

-- 添加排序字段
ALTER TABLE `business_scope` ADD COLUMN `sort_num` int DEFAULT 0 COMMENT '排序号' AFTER `no`;

-- 添加排序字段索引
ALTER TABLE `business_scope` ADD INDEX `idx_sort_num` (`sort_num`);

-- 添加复合索引
ALTER TABLE `business_scope` ADD INDEX `idx_sort_order` (`sort_num`, `good_type`);

-- 为现有数据设置排序号（按货物类型分组，按创建时间排序）
UPDATE `business_scope` bs1 
SET `sort_num` = (
    SELECT COUNT(*) + 1 
    FROM `business_scope` bs2 
    WHERE bs2.`good_type` = bs1.`good_type` 
    AND bs2.`create_time` < bs1.`create_time`
    AND bs2.`is_deleted` = '0'
)
WHERE bs1.`is_deleted` = '0';

-- 如果某些记录的排序号为NULL，设置为1
UPDATE `business_scope` SET `sort_num` = 1 WHERE `sort_num` IS NULL OR `sort_num` = 0; 