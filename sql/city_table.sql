-- 城市表
CREATE TABLE `sys_city` (
  `id` varchar(32) NOT NULL COMMENT '城市ID',
  `code` varchar(20) NOT NULL COMMENT '城市编码',
  `name` varchar(100) NOT NULL COMMENT '城市名称',
  `p_code` varchar(20) DEFAULT NULL COMMENT '父级城市编码',
  `chain` varchar(500) DEFAULT NULL COMMENT '城市层级链',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '逻辑删除标识（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_p_code` (`p_code`),
  KEY `idx_name` (`name`),
  KEY `idx_chain` (`chain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市表';

-- 插入示例数据
INSERT INTO `sys_city` (`id`, `code`, `name`, `p_code`, `chain`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES
('1', '110000', '北京市', NULL, '北京市', NOW(), NOW(), 'system', 'system', '0'),
('2', '120000', '天津市', NULL, '天津市', NOW(), NOW(), 'system', 'system', '0'),
('3', '130000', '河北省', NULL, '河北省', NOW(), NOW(), 'system', 'system', '0'),
('4', '310000', '上海市', NULL, '上海市', NOW(), NOW(), 'system', 'system', '0'),
('5', '320000', '江苏省', NULL, '江苏省', NOW(), NOW(), 'system', 'system', '0'),
('6', '330000', '浙江省', NULL, '浙江省', NOW(), NOW(), 'system', 'system', '0'),
('7', '440000', '广东省', NULL, '广东省', NOW(), NOW(), 'system', 'system', '0'),
('8', '110100', '北京市', '110000', '北京市_北京市', NOW(), NOW(), 'system', 'system', '0'),
('9', '120100', '天津市', '120000', '天津市_天津市', NOW(), NOW(), 'system', 'system', '0'),
('10', '130100', '石家庄市', '130000', '河北省_石家庄市', NOW(), NOW(), 'system', 'system', '0'),
('11', '310100', '上海市', '310000', '上海市_上海市', NOW(), NOW(), 'system', 'system', '0'),
('12', '320100', '南京市', '320000', '江苏省_南京市', NOW(), NOW(), 'system', 'system', '0'),
('13', '320200', '无锡市', '320000', '江苏省_无锡市', NOW(), NOW(), 'system', 'system', '0'),
('14', '320300', '徐州市', '320000', '江苏省_徐州市', NOW(), NOW(), 'system', 'system', '0'),
('15', '330100', '杭州市', '330000', '浙江省_杭州市', NOW(), NOW(), 'system', 'system', '0'),
('16', '330200', '宁波市', '330000', '浙江省_宁波市', NOW(), NOW(), 'system', 'system', '0'),
('17', '440100', '广州市', '440000', '广东省_广州市', NOW(), NOW(), 'system', 'system', '0'),
('18', '440300', '深圳市', '440000', '广东省_深圳市', NOW(), NOW(), 'system', 'system', '0'),
('19', '440400', '珠海市', '440000', '广东省_珠海市', NOW(), NOW(), 'system', 'system', '0'),
('20', '440500', '汕头市', '440000', '广东省_汕头市', NOW(), NOW(), 'system', 'system', '0');
