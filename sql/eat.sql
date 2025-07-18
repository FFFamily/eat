/*
 Navicat Premium Dump SQL

 Source Server         : 阿里云
 Source Server Type    : MySQL
 Source Server Version : 90200 (9.2.0)
 Source Host           : 8.134.152.53:3306
 Source Schema         : eat

 Target Server Type    : MySQL
 Target Server Version : 90200 (9.2.0)
 File Encoding         : 65001

 Date: 26/03/2025 23:21:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `name` varchar(255) DEFAULT NULL COMMENT '食物名称',
  `food_type_id` int DEFAULT NULL COMMENT '食物类型ID',
  `status` int DEFAULT NULL COMMENT '状态',
  `description` text COMMENT '描述',
  `eat_count` int DEFAULT NULL COMMENT '吃的次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='食物表';

-- ----------------------------
-- Records of food
-- ----------------------------
BEGIN;
INSERT INTO `food` (`id`, `name`, `food_type_id`, `status`, `description`, `eat_count`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1904550093523595266', '肯德基', NULL, NULL, NULL, NULL, '2025-03-25 23:05:02', '2025-03-25 23:05:02', '1', '1', '0');
INSERT INTO `food` (`id`, `name`, `food_type_id`, `status`, `description`, `eat_count`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1904550178206593026', '麦当劳', NULL, NULL, NULL, NULL, '2025-03-25 23:05:22', '2025-03-25 23:05:22', '1', '1', '0');
INSERT INTO `food` (`id`, `name`, `food_type_id`, `status`, `description`, `eat_count`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1904550533116014594', '肯基基', NULL, NULL, NULL, NULL, '2025-03-25 23:06:47', '2025-03-25 23:06:47', '1', '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for food_type
-- ----------------------------
DROP TABLE IF EXISTS `food_type`;
CREATE TABLE `food_type` (
  `id` varchar(32) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `status` int DEFAULT '1',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` varchar(255) NOT NULL COMMENT '创建人',
  `update_by` varchar(255) NOT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of food_type
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for adRole
-- ----------------------------
DROP TABLE IF EXISTS `adRole`;
CREATE TABLE `adRole` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名',
  `code` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `remark` varchar(2552) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ----------------------------
-- Records of adRole
-- ----------------------------
BEGIN;
INSERT INTO `adRole` (`id`, `name`, `code`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1', '超级管理员', 'SUPER_ADMIN', '拥有所有权限的超级管理员', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adRole` (`id`, `name`, `code`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('2', '系统管理员', 'SYSTEM_ADMIN', '系统管理员角色', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adRole` (`id`, `name`, `code`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('3', '普通用户', 'USER', '普通用户角色', NOW(), NOW(), '1', '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号密码',
  `status` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户状态',
  `nickname` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户昵称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `password`, `status`, `nickname`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1', 'admin', '96e79218965eb72c92a549dd5a330112', 'use', '管理员', NULL, NULL, NULL, NULL, '0');
COMMIT;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编码',
  `role_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Records of user_role
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号密码',
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像',
  `status` int DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `dept_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员用户表';

-- ----------------------------
-- Records of admin_user
-- ----------------------------
BEGIN;
INSERT INTO `admin_user` (`id`, `username`, `password`, `name`, `email`, `phone`, `avatar`, `status`, `dept_id`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1', 'admin', '96e79218965eb72c92a549dd5a330112', '超级管理员', 'admin@example.com', '13800138000', NULL, 1, '1', NOW(), NOW(), '1', '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for adDepartment
-- ----------------------------
DROP TABLE IF EXISTS `adDepartment`;
CREATE TABLE `adDepartment` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  `code` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门编码',
  `parent_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '父部门ID',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` int DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- ----------------------------
-- Records of adDepartment
-- ----------------------------
BEGIN;
INSERT INTO `adDepartment` (`id`, `name`, `code`, `parent_id`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1', '总公司', 'ROOT', '0', 1, 1, '根部门', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adDepartment` (`id`, `name`, `code`, `parent_id`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('2', '技术部', 'TECH', '1', 1, 1, '技术开发部门', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adDepartment` (`id`, `name`, `code`, `parent_id`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('3', '运营部', 'OPERATION', '1', 2, 1, '运营管理部门', NOW(), NOW(), '1', '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for adPermission
-- ----------------------------
DROP TABLE IF EXISTS `adPermission`;
CREATE TABLE `adPermission` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `code` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
  `type` int NOT NULL COMMENT '权限类型：1-菜单，2-按钮',
  `parent_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '父权限ID',
  `path` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` int DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- ----------------------------
-- Records of adPermission
-- ----------------------------
BEGIN;
INSERT INTO `adPermission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('1', '系统管理', 'system', 1, '0', '/system', NULL, 'system', 1, 1, '系统管理菜单', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adPermission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('2', '用户管理', 'system:user', 1, '1', '/system/user', 'system/user/index', 'user', 1, 1, '用户管理菜单', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adPermission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('3', '角色管理', 'system:adRole', 1, '1', '/system/adRole', 'system/adRole/index', 'adRole', 2, 1, '角色管理菜单', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adPermission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('4', '权限管理', 'system:adPermission', 1, '1', '/system/adPermission', 'system/adPermission/index', 'adPermission', 3, 1, '权限管理菜单', NOW(), NOW(), '1', '1', '0');
INSERT INTO `adPermission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES ('5', '部门管理', 'system:dept', 1, '1', '/system/dept', 'system/dept/index', 'dept', 4, 1, '部门管理菜单', NOW(), NOW(), '1', '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for admin_user_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_user_role`;
CREATE TABLE `admin_user_role` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `role_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员用户角色关联表';

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `role_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `permission_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `user_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作用户名',
  `operation_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `operation_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作名称',
  `method_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '方法名',
  `request_method` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求方式',
  `request_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求URL',
  `request_params` text COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `response_data` text COLLATE utf8mb4_general_ci COMMENT '响应数据',
  `ip_address` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户代理',
  `execution_time` bigint DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` int DEFAULT '1' COMMENT '执行状态：1-成功，0-失败',
  `error_message` text COLLATE utf8mb4_general_ci COMMENT '错误信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

SET FOREIGN_KEY_CHECKS = 1;
