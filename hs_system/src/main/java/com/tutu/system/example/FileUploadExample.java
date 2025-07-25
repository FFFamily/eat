//package com.tutu.system.example;
//
//import com.tutu.system.service.SysFileService;
//import com.tutu.system.vo.FileUploadVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
///**
// * 文件上传使用示例
// */
//@Slf4j
//@Component
//public class FileUploadExample {
//
//    @Autowired
//    private SysFileService sysFileService;
//
//    /**
//     * 文件上传示例
//     */
//    public void uploadExample() {
//        try {
//            // 模拟文件上传（实际使用中，文件来自前端）
//            String testFilePath = "/path/to/test/file.jpg";
//            FileInputStream fis = new FileInputStream(testFilePath);
//
//            MultipartFile mockFile = new MultipartFile(
//                "file",
//                "test.jpg",
//                "image/jpeg",
//                fis
//            );
//
//            // 上传文件
//            FileUploadVO result = sysFileService.uploadFile(mockFile, "user123", "测试用户");
//
//            log.info("文件上传成功:");
//            log.info("文件ID: {}", result.getFileId());
//            log.info("原文件名: {}", result.getOriginalName());
//            log.info("存储文件名: {}", result.getFileName());
//            log.info("文件大小: {}", result.getFileSizeFormat());
//            log.info("访问URL: {}", result.getFileUrl());
//
//        } catch (IOException e) {
//            log.error("文件上传示例失败", e);
//        }
//    }
//
//    /**
//     * 前端调用示例（JavaScript）
//     */
//    public void frontendExample() {
//        String jsCode = """
//            // 单文件上传示例
//            function uploadSingleFile() {
//                const fileInput = document.getElementById('fileInput');
//                const file = fileInput.files[0];
//
//                if (!file) {
//                    alert('请选择文件');
//                    return;
//                }
//
//                const formData = new FormData();
//                formData.append('file', file);
//                formData.append('uploadUserId', 'user123');
//                formData.append('uploadUserName', '用户名');
//
//                fetch('/system/file/upload', {
//                    method: 'POST',
//                    body: formData
//                })
//                .then(response => response.json())
//                .then(data => {
//                    if (data.success) {
//                        console.log('上传成功:', data.data);
//                        // 显示文件访问链接
//                        const fileUrl = data.data.fileUrl;
//                        console.log('文件访问地址:', fileUrl);
//                    } else {
//                        console.error('上传失败:', data.message);
//                    }
//                })
//                .catch(error => {
//                    console.error('上传错误:', error);
//                });
//            }
//
//            // 多文件上传示例
//            function uploadMultipleFiles() {
//                const fileInput = document.getElementById('multiFileInput');
//                const files = fileInput.files;
//
//                if (files.length === 0) {
//                    alert('请选择文件');
//                    return;
//                }
//
//                const formData = new FormData();
//                for (let i = 0; i < files.length; i++) {
//                    formData.append('files', files[i]);
//                }
//                formData.append('uploadUserId', 'user123');
//                formData.append('uploadUserName', '用户名');
//
//                fetch('/system/file/upload/batch', {
//                    method: 'POST',
//                    body: formData
//                })
//                .then(response => response.json())
//                .then(data => {
//                    if (data.success) {
//                        console.log('批量上传成功:', data.data);
//                        data.data.forEach(file => {
//                            console.log('文件:', file.originalName, '访问地址:', file.fileUrl);
//                        });
//                    } else {
//                        console.error('批量上传失败:', data.message);
//                    }
//                })
//                .catch(error => {
//                    console.error('批量上传错误:', error);
//                });
//            }
//
//            // 文件预览示例
//            function previewFile(fileUrl) {
//                // 在新窗口中预览文件
//                window.open(fileUrl, '_blank');
//            }
//
//            // 文件下载示例
//            function downloadFile(fileUrl, fileName) {
//                const downloadUrl = fileUrl + '?download=true';
//                const link = document.createElement('a');
//                link.href = downloadUrl;
//                link.download = fileName;
//                document.body.appendChild(link);
//                link.click();
//                document.body.removeChild(link);
//            }
//
//            // 检查文件是否存在（秒传功能）
//            function checkFileExists(fileMd5) {
//                fetch(`/system/file/check/${fileMd5}`)
//                .then(response => response.json())
//                .then(data => {
//                    if (data.success) {
//                        console.log('文件已存在，可以秒传:', data.data);
//                        return data.data;
//                    } else {
//                        console.log('文件不存在，需要上传');
//                        return null;
//                    }
//                });
//            }
//            """;
//
//        log.info("前端调用示例代码:\n{}", jsCode);
//    }
//
//    /**
//     * HTML示例
//     */
//    public void htmlExample() {
//        String htmlCode = """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <title>文件上传示例</title>
//            </head>
//            <body>
//                <h2>单文件上传</h2>
//                <input type="file" id="fileInput" />
//                <button onclick="uploadSingleFile()">上传文件</button>
//
//                <h2>多文件上传</h2>
//                <input type="file" id="multiFileInput" multiple />
//                <button onclick="uploadMultipleFiles()">批量上传</button>
//
//                <h2>拖拽上传</h2>
//                <div id="dropZone" style="border: 2px dashed #ccc; padding: 20px; text-align: center;">
//                    拖拽文件到这里上传
//                </div>
//
//                <script>
//                    // 拖拽上传功能
//                    const dropZone = document.getElementById('dropZone');
//
//                    dropZone.addEventListener('dragover', (e) => {
//                        e.preventDefault();
//                        dropZone.style.backgroundColor = '#f0f0f0';
//                    });
//
//                    dropZone.addEventListener('dragleave', (e) => {
//                        e.preventDefault();
//                        dropZone.style.backgroundColor = '';
//                    });
//
//                    dropZone.addEventListener('drop', (e) => {
//                        e.preventDefault();
//                        dropZone.style.backgroundColor = '';
//
//                        const files = e.dataTransfer.files;
//                        if (files.length > 0) {
//                            uploadFiles(files);
//                        }
//                    });
//
//                    function uploadFiles(files) {
//                        const formData = new FormData();
//                        for (let i = 0; i < files.length; i++) {
//                            formData.append('files', files[i]);
//                        }
//
//                        fetch('/system/file/upload/batch', {
//                            method: 'POST',
//                            body: formData
//                        })
//                        .then(response => response.json())
//                        .then(data => {
//                            console.log('上传结果:', data);
//                        });
//                    }
//                </script>
//            </body>
//            </html>
//            """;
//
//        log.info("HTML示例代码:\n{}", htmlCode);
//    }
//}
