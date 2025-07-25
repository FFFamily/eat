package com.tutu.api.controller;

import cn.hutool.core.io.IoUtil;
import com.tutu.system.config.FileUploadConfig;
import com.tutu.system.utils.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件访问控制器
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileAccessController {
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    /**
     * 文件访问接口
     */
    @GetMapping("/**")
    public void accessFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取文件相对路径
            String requestURI = request.getRequestURI();
            String relativePath = requestURI.substring("/files".length());
            
            // 构建完整文件路径
            String fullPath = fileUploadConfig.getBasePath() + relativePath;
            File file = new File(fullPath);
            
            // 检查文件是否存在
            if (!file.exists() || !file.isFile()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found");
                return;
            }
            
            // 获取文件名和MIME类型
            String fileName = file.getName();
            String contentType = FileUtils.getContentType(fileName);
            
            // 设置响应头
            response.setContentType(contentType);
            response.setContentLengthLong(file.length());
            
            // 判断是否为下载请求
            String download = request.getParameter("download");
            if ("true".equals(download) || "1".equals(download)) {
                // 设置下载响应头
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            } else {
                // 设置在线预览响应头
                if (FileUtils.isImageFile(fileName)) {
                    response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                } else {
                    response.setHeader("Content-Disposition", "inline");
                }
            }
            
            // 设置缓存控制
            response.setHeader("Cache-Control", "public, max-age=3600");
            response.setHeader("ETag", "\"" + file.lastModified() + "\"");
            
            // 检查If-None-Match头（ETag缓存）
            String ifNoneMatch = request.getHeader("If-None-Match");
            if (ifNoneMatch != null && ifNoneMatch.equals("\"" + file.lastModified() + "\"")) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            
            // 支持断点续传
            String rangeHeader = request.getHeader("Range");
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                handleRangeRequest(file, rangeHeader, response);
            } else {
                // 普通文件传输
                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = response.getOutputStream()) {
                    IoUtil.copy(fis, os);
                }
            }
            
        } catch (Exception e) {
            log.error("文件访问失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Internal server error");
            } catch (IOException ioException) {
                log.error("写入错误响应失败", ioException);
            }
        }
    }
    
    /**
     * 处理断点续传请求
     */
    private void handleRangeRequest(File file, String rangeHeader, HttpServletResponse response) throws IOException {
        long fileLength = file.length();
        
        // 解析Range头
        String range = rangeHeader.substring(6); // 去掉"bytes="
        String[] ranges = range.split("-");
        
        long start = 0;
        long end = fileLength - 1;
        
        if (ranges.length > 0 && !ranges[0].isEmpty()) {
            start = Long.parseLong(ranges[0]);
        }
        if (ranges.length > 1 && !ranges[1].isEmpty()) {
            end = Long.parseLong(ranges[1]);
        }
        
        // 验证范围
        if (start > end || start >= fileLength) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader("Content-Range", "bytes */" + fileLength);
            return;
        }
        
        // 设置响应头
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        response.setContentLengthLong(end - start + 1);
        response.setHeader("Accept-Ranges", "bytes");
        
        // 传输指定范围的文件内容
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             OutputStream os = response.getOutputStream()) {
            
            raf.seek(start);
            byte[] buffer = new byte[8192];
            long remaining = end - start + 1;
            
            while (remaining > 0) {
                int bytesToRead = (int) Math.min(buffer.length, remaining);
                int bytesRead = raf.read(buffer, 0, bytesToRead);
                
                if (bytesRead == -1) {
                    break;
                }
                
                os.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
        }
    }
    
    /**
     * 获取文件信息（不下载文件内容）
     */
    @GetMapping("/info/**")
    public ResponseEntity<String> getFileInfo(HttpServletRequest request) {
        try {
            String requestURI = request.getRequestURI();
            String relativePath = requestURI.substring("/files/info".length());
            String fullPath = fileUploadConfig.getBasePath() + relativePath;
            File file = new File(fullPath);
            
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            String fileInfo = String.format(
                "{\n" +
                "  \"name\": \"%s\",\n" +
                "  \"size\": %d,\n" +
                "  \"lastModified\": %d,\n" +
                "  \"contentType\": \"%s\"\n" +
                "}",
                file.getName(),
                file.length(),
                file.lastModified(),
                FileUtils.getContentType(file.getName())
            );
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fileInfo);
                    
        } catch (Exception e) {
            log.error("获取文件信息失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
