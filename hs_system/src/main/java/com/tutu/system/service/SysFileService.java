package com.tutu.system.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.system.config.FileUploadConfig;
import com.tutu.system.entity.SysFile;
import com.tutu.system.mapper.SysFileMapper;
import com.tutu.system.utils.FileUtils;
import com.tutu.system.vo.FileUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 系统文件服务实现类 - 使用MyBatis Plus方式
 */
@Slf4j
@Service
public class SysFileService extends ServiceImpl<SysFileMapper, SysFile> {
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    /**
     * 上传文件
     */
    public FileUploadVO uploadFile(MultipartFile file, String uploadUserId, String uploadUserName) {
        try {
            // 1. 验证文件
            validateFile(file);
            // 2. 生成文件信息
            String originalName = file.getOriginalFilename();
            String fileName = fileUploadConfig.isKeepOriginalName() ? originalName : FileUtils.generateFileName(originalName);
            String fileExt = FileUtils.getFileExtension(originalName);
            String fileType = FileUtils.getContentType(originalName);
            long fileSize = file.getSize();
            // 3. 生成存储路径
            String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), fileUploadConfig.isDateFolder());
            FileUtils.createDirectories(storePath);
            // 4. 保存文件
            String fullPath = storePath + fileName;
            File destFile = new File(fullPath);
            file.transferTo(destFile);
            // 5. 计算文件MD5
            String fileMd5;
            try (FileInputStream fis = new FileInputStream(destFile)) {
                fileMd5 = FileUtils.calculateMD5(fis);
            }
            // 6. 生成访问URL
            String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            String fileUrl = fileUploadConfig.getUrlPrefix() + relativePath;
            // 7. 保存文件信息到数据库
            SysFile sysFile = new SysFile();
            sysFile.setOriginalName(originalName);
            sysFile.setFileName(fileName);
            sysFile.setFilePath(fullPath);
            sysFile.setFileSize(fileSize);
            sysFile.setFileType(fileType);
            sysFile.setFileExt(fileExt);
            sysFile.setFileMd5(fileMd5);
            sysFile.setFileUrl(fileUrl);
            sysFile.setUploadUserId(uploadUserId);
            sysFile.setUploadUserName(uploadUserName);
            sysFile.setStatus(1);
            sysFile.setCreateTime(new Date());
            sysFile.setUpdateTime(new Date());
            save(sysFile);
            // 8. 构建返回结果
            return buildFileUploadVO(sysFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传文件
     */
    public List<FileUploadVO> uploadFiles(MultipartFile[] files, String uploadUserId, String uploadUserName) {
        return List.of(files).stream()
                .map(file -> uploadFile(file, uploadUserId, uploadUserName))
                .toList();
    }
    
    /**
     * 根据文件ID获取文件信息
     */
    public SysFile getFileById(String fileId) {
        return getById(fileId);
    }
    
    /**
     * 根据文件MD5查找文件（秒传功能）
     */
    public SysFile findByMd5(String md5) {
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysFile::getFileMd5, md5)
                   .eq(SysFile::getStatus, 1)
                   .eq(SysFile::getIsDeleted, CommonConstant.NO_STR)
                   .last("LIMIT 1");
        return getOne(queryWrapper);
    }
    
    /**
     * 分页查询文件列表
     */
    public IPage<SysFile> getPageList(int current, int size, String fileName, String fileType, String uploadUserId) {
        Page<SysFile> page = new Page<>(current, size);
        LambdaQueryWrapper<SysFile> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(SysFile::getIsDeleted, CommonConstant.NO_STR);
        
        if (StrUtil.isNotBlank(fileName)) {
            queryWrapper.and(wrapper -> wrapper
                .like(SysFile::getOriginalName, fileName)
                .or()
                .like(SysFile::getFileName, fileName)
            );
        }
        
        if (StrUtil.isNotBlank(fileType)) {
            queryWrapper.like(SysFile::getFileType, fileType);
        }
        
        if (StrUtil.isNotBlank(uploadUserId)) {
            queryWrapper.eq(SysFile::getUploadUserId, uploadUserId);
        }
        
        queryWrapper.orderByDesc(SysFile::getCreateTime);
        
        return page(page, queryWrapper);
    }
    
    /**
     * 删除文件
     */
    public boolean deleteFile(String fileId) {
        SysFile sysFile = getById(fileId);
        if (sysFile == null) {
            throw new RuntimeException("文件不存在");
        }
        
        // 删除物理文件
        boolean physicalDeleted = FileUtils.deleteFile(sysFile.getFilePath());
        if (!physicalDeleted) {
            log.warn("物理文件删除失败: {}", sysFile.getFilePath());
        }
        
        // 删除数据库记录
        return removeById(fileId);
    }
    
    /**
     * 批量删除文件
     */
    public boolean batchDeleteFiles(List<String> fileIds) {
        List<SysFile> files = listByIds(fileIds);
        
        // 删除物理文件
        files.forEach(file -> {
            boolean deleted = FileUtils.deleteFile(file.getFilePath());
            if (!deleted) {
                log.warn("物理文件删除失败: {}", file.getFilePath());
            }
        });
        
        // 删除数据库记录
        return removeByIds(fileIds);
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        String originalName = file.getOriginalFilename();
        if (StrUtil.isBlank(originalName)) {
            throw new RuntimeException("文件名不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > fileUploadConfig.getMaxFileSize()) {
            throw new RuntimeException("文件大小超过限制: " + FileUtils.formatFileSize(fileUploadConfig.getMaxFileSize()));
        }
        
        // 检查文件类型
        if (!FileUtils.isAllowedFileType(originalName, fileUploadConfig.getAllowedTypes())) {
            throw new RuntimeException("不支持的文件类型: " + FileUtils.getFileExtension(originalName));
        }
    }
    
    /**
     * 构建文件上传结果VO
     */
    private FileUploadVO buildFileUploadVO(SysFile sysFile) {
        FileUploadVO vo = new FileUploadVO();
        vo.setFileId(sysFile.getId());
        vo.setOriginalName(sysFile.getOriginalName());
        vo.setFileName(sysFile.getFileName());
        vo.setFileSize(sysFile.getFileSize());
        vo.setFileType(sysFile.getFileType());
        vo.setFileExt(sysFile.getFileExt());
        vo.setFileUrl(sysFile.getFileUrl());
        vo.setUploadTime(DateUtil.formatDateTime(sysFile.getCreateTime()));
        vo.setFileSizeFormat(FileUtils.formatFileSize(sysFile.getFileSize()));
        
        return vo;
    }
}
