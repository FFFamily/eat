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

create table if not exists eat.food
(
    id           varchar(64)            not null comment 'ID'
        primary key,
    name         varchar(255)           null comment '食物名称',
    food_type_id int                    null comment '食物类型ID',
    status       int                    null comment '状态',
    description  text                   null comment '描述',
    eat_count    int                    null comment '吃的次数',
    create_time  datetime               null comment '创建时间',
    update_time  datetime               null comment '更新时间',
    create_by    varchar(255)           null comment '创建人',
    update_by    varchar(255)           null comment '更新人',
    is_deleted   varchar(1) default '0' not null
)
    comment '食物表';

create table if not exists eat.food_type
(
    id          varchar(32)            not null
        primary key,
    name        varchar(100)           null,
    status      int        default 1   null,
    create_time datetime               not null comment '创建时间',
    update_time datetime               not null comment '更新时间',
    create_by   varchar(255)           not null comment '创建人',
    update_by   varchar(255)           not null comment '更新人',
    is_deleted  varchar(1) default '0' not null
);

create table if not exists eat.user
(
    id          varchar(32)            not null comment '主键ID'
        primary key,
    username    varchar(50)            not null comment '登录账号',
    password    varchar(255)           not null comment '账号密码',
    status      varchar(50)            null comment '用户状态',
    nickname    varchar(100)           null comment '用户昵称',
    create_time datetime               null comment '创建时间',
    update_time datetime               null comment '更新时间',
    create_by   varchar(32)            null comment '创建人',
    update_by   varchar(32)            null comment '更新人',
    is_deleted  varchar(1) default '0' not null,
    constraint username
        unique (username)
)
    comment '用户表' collate = utf8mb4_general_ci;



