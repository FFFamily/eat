package com.tutu.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 文件上传根路径
     */
    private String basePath = "/opt/upload";
    
    /**
     * 文件访问URL前缀
     */
    private String urlPrefix = "/files";
    
    /**
     * 允许上传的文件类型
     */
    private String[] allowedTypes = {
        "jpg", "jpeg", "png", "gif", "bmp", "webp",  // 图片
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",  // 文档
        "zip", "rar", "7z", "tar", "gz",  // 压缩包
        "mp4", "avi", "mov", "wmv", "flv", "mkv",  // 视频
        "mp3", "wav", "flac", "aac"  // 音频
    };
    
    /**
     * 单个文件最大大小（字节）默认10MB
     */
    private long maxFileSize = 10 * 1024 * 1024;
    
    /**
     * 是否按日期分目录存储
     */
    private boolean dateFolder = true;
    
    /**
     * 是否保留原文件名
     */
    private boolean keepOriginalName = false;
}
