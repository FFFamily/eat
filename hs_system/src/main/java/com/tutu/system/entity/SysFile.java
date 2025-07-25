package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统文件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 文件原名
     */
    private String originalName;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件扩展名
     */
    private String fileExt;
    
    /**
     * 文件MD5值
     */
    private String fileMd5;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 上传用户ID
     */
    private String uploadUserId;
    
    /**
     * 上传用户名
     */
    private String uploadUserName;
    
    /**
     * 文件状态：1-正常，0-删除
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
