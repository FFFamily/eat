-- 库存管理模块数据库表结构

-- 1. 仓库表
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `warehouse_no` VARCHAR(50) NOT NULL COMMENT '仓库编号',
    `warehouse_name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
    `warehouse_address` VARCHAR(255) DEFAULT NULL COMMENT '仓库地址',
    `warehouse_type` VARCHAR(50) DEFAULT NULL COMMENT '仓库类型（raw_material:原料仓, finished_product:成品仓, transit:中转仓）',
    `manager_id` VARCHAR(64) DEFAULT NULL COMMENT '负责人ID',
    `manager_name` VARCHAR(50) DEFAULT NULL COMMENT '负责人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态（active:启用, inactive:停用）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_no` (`warehouse_no`),
    KEY `idx_warehouse_type` (`warehouse_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 2. 库存主表
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `warehouse_id` VARCHAR(64) NOT NULL COMMENT '仓库ID',
    `good_no` VARCHAR(50) NOT NULL COMMENT '货物编号',
    `good_name` VARCHAR(100) NOT NULL COMMENT '货物名称',
    `good_type` VARCHAR(50) DEFAULT NULL COMMENT '货物类型',
    `good_model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `current_stock` DECIMAL(18, 4) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    `available_stock` DECIMAL(18, 4) NOT NULL DEFAULT 0 COMMENT '可用库存数量',
    `locked_stock` DECIMAL(18, 4) NOT NULL DEFAULT 0 COMMENT '锁定库存数量',
    `min_stock` DECIMAL(18, 4) DEFAULT NULL COMMENT '最小库存（安全库存）',
    `max_stock` DECIMAL(18, 4) DEFAULT NULL COMMENT '最大库存',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `last_in_time` DATETIME DEFAULT NULL COMMENT '最后入库时间',
    `last_out_time` DATETIME DEFAULT NULL COMMENT '最后出库时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_good` (`warehouse_id`, `good_no`),
    KEY `idx_good_no` (`good_no`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存主表';

-- 3. 入库单表
DROP TABLE IF EXISTS `inventory_in`;
CREATE TABLE `inventory_in` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `in_no` VARCHAR(50) NOT NULL COMMENT '入库单号',
    `warehouse_id` VARCHAR(64) NOT NULL COMMENT '仓库ID',
    `in_type` VARCHAR(50) NOT NULL COMMENT '入库类型（purchase:采购入库, return:退货入库, transfer:调拨入库, other:其他入库）',
    `source_order_id` VARCHAR(64) DEFAULT NULL COMMENT '来源订单ID',
    `source_order_no` VARCHAR(50) DEFAULT NULL COMMENT '来源订单号',
    `total_quantity` DECIMAL(18, 4) NOT NULL DEFAULT 0 COMMENT '总数量',
    `in_time` DATETIME DEFAULT NULL COMMENT '入库时间',
    `operator_id` VARCHAR(64) DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '单据状态（pending:待入库, completed:已入库, cancelled:已取消）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_in_no` (`in_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_in_type` (`in_type`),
    KEY `idx_status` (`status`),
    KEY `idx_source_order_id` (`source_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 4. 入库明细表
DROP TABLE IF EXISTS `inventory_in_item`;
CREATE TABLE `inventory_in_item` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `in_id` VARCHAR(64) NOT NULL COMMENT '入库单ID',
    `good_no` VARCHAR(50) NOT NULL COMMENT '货物编号',
    `good_name` VARCHAR(100) NOT NULL COMMENT '货物名称',
    `good_type` VARCHAR(50) DEFAULT NULL COMMENT '货物类型',
    `good_model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `in_quantity` DECIMAL(18, 4) NOT NULL COMMENT '入库数量',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_in_id` (`in_id`),
    KEY `idx_good_no` (`good_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细表';

-- 5. 出库单表
DROP TABLE IF EXISTS `inventory_out`;
CREATE TABLE `inventory_out` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `out_no` VARCHAR(50) NOT NULL COMMENT '出库单号',
    `warehouse_id` VARCHAR(64) NOT NULL COMMENT '仓库ID',
    `out_type` VARCHAR(50) NOT NULL COMMENT '出库类型（sale:销售出库, loss:报损出库, transfer:调拨出库, other:其他出库）',
    `target_order_id` VARCHAR(64) DEFAULT NULL COMMENT '目标订单ID',
    `target_order_no` VARCHAR(50) DEFAULT NULL COMMENT '目标订单号',
    `total_quantity` DECIMAL(18, 4) NOT NULL DEFAULT 0 COMMENT '总数量',
    `out_time` DATETIME DEFAULT NULL COMMENT '出库时间',
    `operator_id` VARCHAR(64) DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '单据状态（pending:待出库, completed:已出库, cancelled:已取消）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_out_no` (`out_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_out_type` (`out_type`),
    KEY `idx_status` (`status`),
    KEY `idx_target_order_id` (`target_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 6. 出库明细表
DROP TABLE IF EXISTS `inventory_out_item`;
CREATE TABLE `inventory_out_item` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `out_id` VARCHAR(64) NOT NULL COMMENT '出库单ID',
    `good_no` VARCHAR(50) NOT NULL COMMENT '货物编号',
    `good_name` VARCHAR(100) NOT NULL COMMENT '货物名称',
    `good_type` VARCHAR(50) DEFAULT NULL COMMENT '货物类型',
    `good_model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `out_quantity` DECIMAL(18, 4) NOT NULL COMMENT '出库数量',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_out_id` (`out_id`),
    KEY `idx_good_no` (`good_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细表';

-- 7. 库存流水表
DROP TABLE IF EXISTS `inventory_transaction`;
CREATE TABLE `inventory_transaction` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `transaction_no` VARCHAR(50) NOT NULL COMMENT '流水号',
    `warehouse_id` VARCHAR(64) NOT NULL COMMENT '仓库ID',
    `good_no` VARCHAR(50) NOT NULL COMMENT '货物编号',
    `good_name` VARCHAR(100) NOT NULL COMMENT '货物名称',
    `transaction_type` VARCHAR(20) NOT NULL COMMENT '交易类型（in:入库, out:出库）',
    `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型（purchase:采购, sale:销售, transfer:调拨, loss:报损, return:退货, other:其他）',
    `quantity` DECIMAL(18, 4) NOT NULL COMMENT '数量（正数为入库，负数为出库）',
    `before_stock` DECIMAL(18, 4) NOT NULL COMMENT '变动前库存',
    `after_stock` DECIMAL(18, 4) NOT NULL COMMENT '变动后库存',
    `related_no` VARCHAR(50) DEFAULT NULL COMMENT '关联单号（入库单号或出库单号）',
    `transaction_time` DATETIME NOT NULL COMMENT '交易时间',
    `operator_id` VARCHAR(64) DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_no` (`transaction_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_good_no` (`good_no`),
    KEY `idx_transaction_type` (`transaction_type`),
    KEY `idx_business_type` (`business_type`),
    KEY `idx_transaction_time` (`transaction_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

