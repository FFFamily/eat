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