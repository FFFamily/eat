-- 系统字典表结构

-- 字典类型表
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
    `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `dict_sort` INT(4) DEFAULT 0 COMMENT '字典排序',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典键值',
    `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
    `css_class` VARCHAR(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
    `list_class` VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
    `is_default` VARCHAR(1) DEFAULT '0' COMMENT '是否默认：1-是，0-否',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` VARCHAR(1) DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_dict_sort` (`dict_sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典数据表';

-- 插入示例数据
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `remark`, `create_time`, `is_deleted`) VALUES
('1', '用户性别', 'sys_user_sex', 1, '用户性别列表', NOW(), '0'),
('2', '菜单状态', 'sys_show_hide', 1, '菜单状态列表', NOW(), '0'),
('3', '系统开关', 'sys_normal_disable', 1, '系统开关列表', NOW(), '0'),
('4', '系统是否', 'sys_yes_no', 1, '系统是否列表', NOW(), '0');

INSERT INTO `sys_dict_data` (`id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `is_default`, `status`, `remark`, `create_time`, `is_deleted`) VALUES
('1', 1, '男', '0', 'sys_user_sex', '1', 1, '性别男', NOW(), '0'),
('2', 2, '女', '1', 'sys_user_sex', '0', 1, '性别女', NOW(), '0'),
('3', 3, '未知', '2', 'sys_user_sex', '0', 1, '性别未知', NOW(), '0'),
('4', 1, '显示', '0', 'sys_show_hide', '1', 1, '显示菜单', NOW(), '0'),
('5', 2, '隐藏', '1', 'sys_show_hide', '0', 1, '隐藏菜单', NOW(), '0'),
('6', 1, '正常', '0', 'sys_normal_disable', '1', 1, '正常状态', NOW(), '0'),
('7', 2, '停用', '1', 'sys_normal_disable', '0', 1, '停用状态', NOW(), '0'),
('8', 1, '是', 'Y', 'sys_yes_no', '1', 1, '系统默认是', NOW(), '0'),
('9', 2, '否', 'N', 'sys_yes_no', '0', 1, '系统默认否', NOW(), '0');

