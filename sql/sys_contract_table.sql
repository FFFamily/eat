DROP TABLE IF EXISTS `sys_contract`;
CREATE TABLE `sys_contract` (
  `id` varchar(64) NOT NULL COMMENT '合同ID',
  `name` varchar(255) DEFAULT NULL COMMENT '合同名称',
  `recognition_code` varchar(64) DEFAULT NULL COMMENT '合同识别号',
  `code` varchar(64) DEFAULT NULL COMMENT '合同编码',
  `type` varchar(20) DEFAULT NULL COMMENT '合同类型',
  `start_date` datetime DEFAULT NULL COMMENT '起始日期',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `transport_mode` varchar(20) DEFAULT NULL COMMENT '运输模式',
  `freight_responsibility` varchar(20) DEFAULT NULL COMMENT '运费承担方式',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '付款方式',
  `invoice_method` varchar(20) DEFAULT NULL COMMENT '开票方式',
  `party_a` varchar(255) DEFAULT NULL COMMENT '甲方信息',
  `party_b` varchar(255) DEFAULT NULL COMMENT '乙方信息',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统合同表';