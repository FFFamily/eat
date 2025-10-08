package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.annotation.FieldPropert;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息实体类
 */
@Getter
@Setter
@TableName("sys_message")
public class Message extends BaseEntity {
    /**
     * 消息ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 标题
     */
    private String title;
    /**
     * 来源
     */
    private String origin;
    /**
     * 内容
     */
    private String content;
    
    /**
     * 标签
     */
    private String tags;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 状态：0-未读，1-已读
     */
    private String status;
}