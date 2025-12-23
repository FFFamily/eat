package com.tutu.api.controller.wx;

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
@RequestMapping("/wx/system/file")
public class WxFileUploadController {
    
    @Autowired
    private SysFileService sysFileService;

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
}
