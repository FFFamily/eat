package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统公告
 */
@Getter
@Setter
@TableName("sys_announcement")
public class Announcement extends TenantBaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容（纯文本）
     */
    private String content;

    /**
     * 状态：1-发布，0-下线
     */
    private Integer status;
}
