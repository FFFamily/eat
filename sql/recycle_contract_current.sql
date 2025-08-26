-- =============================================
-- 回收合同表结构（基于当前实体类）
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `recycle_contract`;

-- =============================================
-- 回收合同主表
-- =============================================
CREATE TABLE `recycle_contract` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '合同名称',
  `no` varchar(100) DEFAULT NULL COMMENT '合同编号',
  `type` varchar(50) DEFAULT NULL COMMENT '合同类型：purchase-采购合同, sale-销售合同, transport-运输合同, process-加工合同, storage-仓储合同, other-其他合同',
  `partner_id` bigint DEFAULT NULL COMMENT '合作方ID',
  `partner_name` varchar(255) DEFAULT NULL COMMENT '合作方名称',
  `start_time` date DEFAULT NULL COMMENT '合同起始时间',
  `end_time` date DEFAULT NULL COMMENT '合同结束时间',
  `total_amount` decimal(15,2) DEFAULT NULL COMMENT '合同总金额',
  `main_bank_card` varchar(50) DEFAULT NULL COMMENT '主银行卡号',
  `invoice_info` text DEFAULT NULL COMMENT '主开票信息',
  `file_path` varchar(500) DEFAULT NULL COMMENT '合同文件路径',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_no` (`no`),
  KEY `idx_type` (`type`),
  KEY `idx_partner_id` (`partner_id`),
  KEY `idx_partner_name` (`partner_name`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收合同表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 合同类型和合作方复合索引
CREATE INDEX `idx_contract_type_partner` ON `recycle_contract` (`type`, `partner_id`);

-- 合同时间范围复合索引
CREATE INDEX `idx_contract_date_range` ON `recycle_contract` (`start_time`, `end_time`);

-- 合同金额和类型复合索引
CREATE INDEX `idx_contract_amount_type` ON `recycle_contract` (`total_amount`, `type`);

-- =============================================
-- 插入示例数据
-- =============================================

-- 插入示例回收合同数据
INSERT INTO `recycle_contract` (`id`, `name`, `no`, `type`, `partner_id`, `partner_name`, `start_time`, `end_time`, `total_amount`, `main_bank_card`, `invoice_info`, `file_path`, `status`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES 
('RC001', '废金属回收采购合同2024-001', 'RC2024001', 'purchase', 1001, 'ABC废料回收公司', '2024-01-01', '2024-12-31', 50000.00, '6222021234567890123', '增值税专用发票，税号：123456789012345678，开户行：中国银行，账号：1234567890123456789', '/contracts/RC001.pdf', NOW(), NOW(), 'system', 'system', '0'),
('RC002', '废纸回收销售合同2024-002', 'RC2024002', 'sale', 1002, 'XYZ纸业回收公司', '2024-02-01', '2024-11-30', 30000.00, '6222021234567890456', '增值税普通发票，税号：123456789012345679，开户行：工商银行，账号：9876543210987654321', '/contracts/RC002.pdf', NOW(), NOW(), 'system', 'system', '0'),
('RC003', '废塑料运输合同2024-003', 'RC2024003', 'transport', 1003, 'DEF塑料回收公司', '2024-03-01', '2024-10-31', 80000.00, '6222021234567890789', '增值税专用发票，税号：123456789012345680，开户行：建设银行，账号：1111222233334444', '/contracts/RC003.pdf', NOW(), NOW(), 'system', 'system', '0'),
('RC004', '废电子产品加工合同2024-004', 'RC2024004', 'process', 1004, 'GHI电子回收公司', '2024-04-01', '2024-09-30', 120000.00, '6222021234567890111', '增值税专用发票，税号：123456789012345681，开户行：农业银行，账号：5555666677778888', '/contracts/RC004.pdf', NOW(), NOW(), 'system', 'system', '0'),
('RC005', '废玻璃仓储合同2024-005', 'RC2024005', 'storage', 1005, 'JKL玻璃回收公司', '2024-05-01', '2024-08-31', 25000.00, '6222021234567890222', '增值税普通发票，税号：123456789012345682，开户行：交通银行，账号：9999000011112222', '/contracts/RC005.pdf', NOW(), NOW(), 'system', 'system', '0');

-- =============================================
-- 表结构说明
-- =============================================
/*
回收合同表 (recycle_contract):
- 存储回收合同的基本信息，包括合同名称、编号、类型、合作方等
- 支持多种合同类型：采购合同、销售合同、运输合同、加工合同、仓储合同、其他合同
- 包含财务相关信息：合同总金额、主银行卡号、开票信息
- 支持合同文件管理：文件路径字段
- 继承BaseEntity的通用字段：创建时间、更新时间、创建人、更新人、逻辑删除

字段说明:
- id: 主键ID，使用varchar(64)类型，支持雪花算法生成的ID
- name: 合同名称，最大255字符
- no: 合同编号，用于业务标识
- type: 合同类型，支持多种回收业务场景
- partner_id: 合作方ID，关联合作方信息
- partner_name: 合作方名称，冗余字段便于查询
- start_time/end_time: 合同时间范围，使用date类型
- total_amount: 合同总金额，使用decimal(15,2)保证精度
- main_bank_card: 主银行卡号
- invoice_info: 开票信息，使用text类型支持长文本
- file_path: 合同文件路径
- is_deleted: 逻辑删除标识，使用varchar(1)类型，0-未删除，1-已删除

索引设计:
- 主键索引：id
- 业务索引：合同名称、编号、类型、合作方等
- 复合索引：优化常用查询场景
- 时间索引：支持按时间范围查询
*/ 