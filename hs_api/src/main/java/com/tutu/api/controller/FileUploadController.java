package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.OperationLog;
import com.tutu.common.enums.OperationType;
import com.tutu.system.entity.SysFile;
import com.tutu.system.service.SysFileService;
import com.tutu.system.vo.FileUploadVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/system/file")
public class FileUploadController {
    
    @Autowired
    private SysFileService sysFileService;

    /**
     * 单文件上传
     */
    @PostMapping("/noAuth/upload")
    @OperationLog(value = "上传文件", type = OperationType.INSERT, recordParams = false)
    public BaseResponse<FileUploadVO> uploadFileWithOutLogin(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadUserId", required = false) String uploadUserId,
            @RequestParam(value = "uploadUserName", required = false) String uploadUserName) {
        try {
            FileUploadVO result = sysFileService.uploadFile(file, uploadUserId, uploadUserName);
            return BaseResponse.success(result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return BaseResponse.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    @OperationLog(value = "上传文件", type = OperationType.INSERT, recordParams = false)
    public BaseResponse<FileUploadVO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadUserId", required = false) String uploadUserId,
            @RequestParam(value = "uploadUserName", required = false) String uploadUserName) {
        try {
            FileUploadVO result = sysFileService.uploadFile(file, uploadUserId, uploadUserName);
            return BaseResponse.success(result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return BaseResponse.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 多文件上传
     */
    @PostMapping("/upload/batch")
    @OperationLog(value = "批量上传文件", type = OperationType.INSERT, recordParams = false)
    public BaseResponse<List<FileUploadVO>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "uploadUserId", required = false) String uploadUserId,
            @RequestParam(value = "uploadUserName", required = false) String uploadUserName) {
        try {
            if (uploadUserId == null) {
                uploadUserId = "system";
            }
            if (uploadUserName == null) {
                uploadUserName = "系统用户";
            }
            
            List<FileUploadVO> results = sysFileService.uploadFiles(files, uploadUserId, uploadUserName);
            return BaseResponse.success(results);
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return BaseResponse.error("批量文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据文件ID获取文件信息
     */
    @GetMapping("/{fileId}")
    @OperationLog(value = "查询文件信息", type = OperationType.SELECT)
    public BaseResponse<SysFile> getFileInfo(@PathVariable String fileId) {
        try {
            SysFile sysFile = sysFileService.getFileById(fileId);
            if (sysFile == null) {
                return BaseResponse.error("文件不存在");
            }
            return BaseResponse.success(sysFile);
        } catch (Exception e) {
            log.error("查询文件信息失败", e);
            return BaseResponse.error("查询文件信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询文件列表
     */
    @GetMapping("/page")
    @OperationLog(value = "查询文件列表", type = OperationType.SELECT)
    public BaseResponse<IPage<SysFile>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String uploadUserId) {
        try {
            IPage<SysFile> page = sysFileService.getPageList(current, size, fileName, fileType, uploadUserId);
            return BaseResponse.success(page);
        } catch (Exception e) {
            log.error("查询文件列表失败", e);
            return BaseResponse.error("查询文件列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    @OperationLog(value = "删除文件", type = OperationType.DELETE, recordParams = true)
    public BaseResponse<String> deleteFile(@PathVariable String fileId) {
        try {
            boolean result = sysFileService.deleteFile(fileId);
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return BaseResponse.error("删除文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    @OperationLog(value = "批量删除文件", type = OperationType.DELETE, recordParams = true)
    public BaseResponse<String> batchDeleteFiles(@RequestBody List<String> fileIds) {
        try {
            boolean result = sysFileService.batchDeleteFiles(fileIds);
            if (result) {
                return BaseResponse.success("批量删除成功");
            } else {
                return BaseResponse.error("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            return BaseResponse.error("批量删除文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查文件是否存在（秒传功能）
     */
    @GetMapping("/check/{md5}")
    @OperationLog(value = "检查文件MD5", type = OperationType.SELECT)
    public BaseResponse<FileUploadVO> checkFileByMd5(@PathVariable String md5) {
        try {
            SysFile existFile = sysFileService.findByMd5(md5);
            if (existFile != null) {
                FileUploadVO vo = new FileUploadVO();
                vo.setFileId(existFile.getId());
                vo.setOriginalName(existFile.getOriginalName());
                vo.setFileName(existFile.getFileName());
                vo.setFileSize(existFile.getFileSize());
                vo.setFileType(existFile.getFileType());
                vo.setFileExt(existFile.getFileExt());
                vo.setFileUrl(existFile.getFileUrl());
                return BaseResponse.success(vo);
            } else {
                return BaseResponse.error("文件不存在");
            }
        } catch (Exception e) {
            log.error("检查文件MD5失败", e);
            return BaseResponse.error("检查文件MD5失败: " + e.getMessage());
        }
    }
}
