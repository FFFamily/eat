-- 用户体系 P0 安全与一致性迁移脚本（非动态版）
-- 说明：
-- 1) 本脚本不做“存在性判断”，请按你当前库的真实表结构选择性执行（可注释掉不适用的语句）。
-- 2) 若新增唯一索引失败，通常是存量数据存在重复；先运行 `2026_02_25_user_auth_data_fix.sql` 排查/修复。

-- 1) 账号编号序列表（MySQL 用表模拟“序列”，避免并发 COUNT 冲突）
CREATE TABLE IF NOT EXISTS `account_username_seq` (
  `account_type_id` varchar(64) NOT NULL COMMENT '账号类型ID',
  `seq` bigint NOT NULL DEFAULT 0 COMMENT '序列值',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`account_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号编号序列表';

-- 2) 密码字段长度（BCrypt 建议 >= 60；这里统一 255，避免误缩短）
ALTER TABLE `account` MODIFY COLUMN `password` varchar(255) DEFAULT NULL COMMENT '密码';
-- 如果你的管理端用户表是 admin_user（sql/eat.sql 使用这个表名）
-- ALTER TABLE `admin_user` MODIFY COLUMN `password` varchar(255) DEFAULT NULL COMMENT '密码';
-- 如果你的管理端用户表是 ad_user（部分环境可能使用此表名）
ALTER TABLE `ad_user` MODIFY COLUMN `password` varchar(255) DEFAULT NULL COMMENT '密码';

-- 3) 唯一索引（按你实际字段存在情况选择执行）
-- 3.1 account.username 唯一
ALTER TABLE `account` ADD UNIQUE KEY `uk_account_username` (`username`);

-- 3.2 account.phone 唯一（仅当 account 表存在 phone 字段时启用）
-- ALTER TABLE `account` ADD UNIQUE KEY `uk_account_phone` (`phone`);

-- 3.3 账号类型编码唯一（根据你实际表名二选一）
-- ALTER TABLE `account_type` ADD UNIQUE KEY `uk_account_type_code` (`code`);
-- ALTER TABLE `sys_account_type` ADD UNIQUE KEY `uk_sys_account_type_code` (`code`);

-- 3.4 业务角色编码唯一（仅当 role 表存在时启用）
-- ALTER TABLE `role` ADD UNIQUE KEY `uk_role_code` (`code`);

-- 3.5 管理端 RBAC 编码唯一（根据你实际表名选择）
-- 下划线风格（若你的表名是 ad_role / ad_permission / ad_department）
-- ALTER TABLE `ad_role` ADD UNIQUE KEY `uk_ad_role_code` (`code`);
-- ALTER TABLE `ad_permission` ADD UNIQUE KEY `uk_ad_permission_code` (`code`);
-- ALTER TABLE `ad_department` ADD UNIQUE KEY `uk_ad_department_code` (`code`);
-- 驼峰风格（sql/eat.sql 使用 adRole / adPermission / adDepartment）
-- ALTER TABLE `adRole` ADD UNIQUE KEY `uk_ad_role_code` (`code`);
-- ALTER TABLE `adPermission` ADD UNIQUE KEY `uk_ad_permission_code` (`code`);
-- ALTER TABLE `adDepartment` ADD UNIQUE KEY `uk_ad_department_code` (`code`);

-- 4) 软删除索引（可选；重复创建会报错，按需执行）
-- CREATE INDEX `idx_account_is_deleted` ON `account` (`is_deleted`);
-- CREATE INDEX `idx_address_is_deleted` ON `address` (`is_deleted`);

-- 5) 外键策略
-- 本次采用“代码层校验阻止删除”，不启用级联删除。
