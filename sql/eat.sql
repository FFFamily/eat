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
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
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
-- Records of role
-- ----------------------------
BEGIN;
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

SET FOREIGN_KEY_CHECKS = 1;
