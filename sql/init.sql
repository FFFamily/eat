create database eat;
use eat;
DROP TABLE if exists user;
CREATE TABLE user (
  id VARCHAR(32) NOT NULL COMMENT '主键ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
  password VARCHAR(255) NOT NULL COMMENT '账号密码',
  status VARCHAR(50) COMMENT '用户状态',
  nickname VARCHAR(100) COMMENT '用户昵称',
-- 以下字段为假设的BaseEntity公共字段，请按需调整
  create_time DATETIME COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间',
  create_by varchar(32) comment '创建人',
  update_by varchar(32) comment '更新人',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
insert into user (id, username, password, status, nickname) VALUE ('1','admin','96e79218965eb72c92a549dd5a330112','use','管理员');
DROP TABLE if exists food;
CREATE TABLE food (
  id VARCHAR(64) NOT NULL COMMENT 'ID',
  name VARCHAR(255)  NULL COMMENT '食物名称',
  food_type_id INT  NULL COMMENT '食物类型ID',
  status INT  NULL COMMENT '状态',
  description TEXT COMMENT '描述',
  eat_count INT  NULL COMMENT '吃的次数',
  create_time DATETIME  NULL COMMENT '创建时间',
  update_time DATETIME  NULL COMMENT '更新时间',
  create_by VARCHAR(255)  NULL COMMENT '创建人',
  update_by VARCHAR(255)  NULL COMMENT '更新人',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食物表';

drop  table if exists food_type;
CREATE TABLE food_type (
   id VARCHAR(32) not null PRIMARY KEY,  -- ASSIGN_ID 通常是雪花算法 ID，适合 VARCHAR
   name VARCHAR(100)  NULL,  -- 类型名称
   status INT default 1  NULL,          -- 状态
   create_time DATETIME NOT NULL COMMENT '创建时间',
   update_time DATETIME NOT NULL COMMENT '更新时间',
   create_by VARCHAR(255) NOT NULL COMMENT '创建人',
   update_by VARCHAR(255) NOT NULL COMMENT '更新人'
);

