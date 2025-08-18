-- =============================================
-- 回收合同管理相关表结构（简化版）
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `recycle_contract_item`;
DROP TABLE IF EXISTS `recycle_contract`;

-- =============================================
-- 回收合同主表
-- =============================================
CREATE TABLE `recycle_contract` (
  `id` varchar(64) NOT NULL COMMENT '合同ID',
  `name` varchar(255) DEFAULT NULL COMMENT '合同名称',
  `type` varchar(50) DEFAULT NULL COMMENT '合同类型',
  `partner` varchar(255) DEFAULT NULL COMMENT '合作方',
  `start_time` datetime DEFAULT NULL COMMENT '起始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `main_bank_card` varchar(50) DEFAULT NULL COMMENT '主银行卡号',
  `invoice_info` text DEFAULT NULL COMMENT '开票信息',
  `pay_node` varchar(100) DEFAULT NULL COMMENT '走款节点',
  `invoice_node` varchar(100) DEFAULT NULL COMMENT '开票节点',
  `total_amount` decimal(15,2) DEFAULT NULL COMMENT '合同总金额',
  `pool` varchar(100) DEFAULT NULL COMMENT '合同资金池',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT '合同状态',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收合同主表';

-- =============================================
-- 回收合同项目明细表
-- =============================================
CREATE TABLE `recycle_contract_item` (
  `id` varchar(64) NOT NULL COMMENT '项目ID',
  `recycle_contract_id` varchar(64) NOT NULL COMMENT '回收合同ID',
  `good_no` varchar(64) DEFAULT NULL COMMENT '货物编号',
  `good_type` varchar(50) DEFAULT NULL COMMENT '货物分类',
  `good_name` varchar(255) DEFAULT NULL COMMENT '货物名称',
  `good_model` varchar(100) DEFAULT NULL COMMENT '货物型号',
  `good_count` int DEFAULT NULL COMMENT '货物数量',
  `good_price` decimal(10,2) DEFAULT NULL COMMENT '货物单价',
  `good_total_price` decimal(15,2) DEFAULT NULL COMMENT '货物总价',
  `good_remark` text DEFAULT NULL COMMENT '货物备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`recycle_contract_id`),
  CONSTRAINT `fk_contract_item_contract` FOREIGN KEY (`recycle_contract_id`) REFERENCES `recycle_contract` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收合同项目明细表'; 