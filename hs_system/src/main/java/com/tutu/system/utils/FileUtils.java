package com.tutu.system.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 文件工具类
 */
public class FileUtils {
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalName) {
        String extension = getFileExtension(originalName);
        String uuid = IdUtil.simpleUUID();
        return StrUtil.isBlank(extension) ? uuid : uuid + "." + extension;
    }
    
    /**
     * 生成文件存储路径
     */
    public static String generateFilePath(String basePath, boolean dateFolder) {
        StringBuilder path = new StringBuilder(basePath);
        
        if (!basePath.endsWith(File.separator)) {
            path.append(File.separator);
        }
        
        if (dateFolder) {
            // 按年月日分目录
            String dateStr = DateUtil.format(new Date(), "yyyy/MM/dd");
            path.append(dateStr).append(File.separator);
        }
        
        return path.toString();
    }
    
    /**
     * 检查文件类型是否允许
     */
    public static boolean isAllowedFileType(String fileName, String[] allowedTypes) {
        if (allowedTypes == null || allowedTypes.length == 0) {
            return true;
        }
        
        String extension = getFileExtension(fileName);
        if (StrUtil.isBlank(extension)) {
            return false;
        }
        
        return Arrays.stream(allowedTypes)
                .anyMatch(type -> type.equalsIgnoreCase(extension));
    }
    
    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    /**
     * 计算文件MD5值
     */
    public static String calculateMD5(InputStream inputStream) {
        try {
            return DigestUtil.md5Hex(inputStream);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * 创建目录
     */
    public static boolean createDirectories(String path) {
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                return directory.mkdirs();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            return FileUtil.del(filePath);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(String filePath) {
        return FileUtil.exist(filePath);
    }
    
    /**
     * 获取文件MIME类型
     */
    public static String getContentType(String fileName) {
        String extension = getFileExtension(fileName);
        
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            default -> "application/octet-stream";
        };
    }
    
    /**
     * 判断是否为图片文件
     */
    public static boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        String[] imageTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        return Arrays.stream(imageTypes)
                .anyMatch(type -> type.equalsIgnoreCase(extension));
    }
}
