package com.tutu.system.vo;

import lombok.Data;

/**
 * 文件上传结果VO
 */
@Data
public class FileUploadVO {
    
    /**
     * 文件ID
     */
    private String fileId;
    
    /**
     * 文件原名
     */
    private String originalName;
    
    /**
     * 文件名
     */
    private String fileName;
    
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
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件完整访问路径
     */
    private String fullUrl;
    
    /**
     * 上传时间
     */
    private String uploadTime;
    
    /**
     * 文件大小（格式化）
     */
    private String fileSizeFormat;
}
