CREATE TABLE `sys_message` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  
  -- ========== 消息基本信息 ==========
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签，多个标签用逗号分隔',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `type` varchar(50) DEFAULT NULL COMMENT '消息类型',
  `status` varchar(2) DEFAULT '0' COMMENT '状态：0-未读，1-已读',
  
  -- ========== 基础字段 ==========
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 创建复合索引优化查询性能
CREATE INDEX `idx_message_user_status` ON `sys_message` (`user_id`, `status`);
CREATE INDEX `idx_message_user_time` ON `sys_message` (`user_id`, `create_time`);
CREATE INDEX `idx_message_user_type` ON `sys_message` (`user_id`, `type`);

-- 插入示例数据（可选）
INSERT INTO `sys_message` (`id`, `title`, `content`, `tags`, `user_id`, `type`, `status`) VALUES 
('1', '系统通知', '您的账户已成功开通，欢迎使用我们的服务！', '系统,通知', 'user001', 'SYSTEM', '0'),
('2', '活动提醒', '新用户专享活动即将开始，敬请期待！', '活动,提醒', 'user001', 'ACTIVITY', '0'),
('3', '业务更新', '您的订单状态已更新，请及时查看。', '订单,更新', 'user002', 'BUSINESS', '0');