DROP TABLE IF EXISTS `recycle_order`;
CREATE TABLE `recycle_order` (
  `id` varchar(64) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `contract_id` varchar(64) DEFAULT NULL COMMENT '合同编号',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  `cargo_img` varchar(255) DEFAULT NULL COMMENT '货物照片',
  `transport_type` varchar(20) DEFAULT NULL COMMENT '运输方式',
  `sign_img` varchar(255) DEFAULT NULL COMMENT '经办人签字照片',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回收订单表';