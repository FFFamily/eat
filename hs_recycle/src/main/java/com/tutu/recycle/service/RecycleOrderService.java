package com.tutu.recycle.service;

import cn.binarywang.wx.miniapp.api.WxMaService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.mapper.RecycleOrderMapper;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.tutu.user.entity.Account;
import com.tutu.user.enums.UserUseTypeEnum;
import com.tutu.user.service.AccountService;
import org.springframework.web.multipart.MultipartFile;
import com.tutu.system.vo.FileUploadVO;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import com.tutu.system.service.SysFileService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RecycleOrderService extends ServiceImpl<RecycleOrderMapper, RecycleOrder> {
    @Resource
    private WxMaService wxMaService;
    @Autowired
    private SysFileService sysFileService;
    @Resource
    private AccountService accountService;
    /**
     * 创建回收订单
     * @param recycleOrder 回收订单信息
     */
    public RecycleOrder createOrder(RecycleOrder recycleOrder) {
        // 生成订单编号
        recycleOrder.setNo(IdUtil.simpleUUID());
        // 状态
        recycleOrder.setStatus(RecycleOrderStatusEnum.PENDING.getCode());
        // 保存订单
        save(recycleOrder);
        return recycleOrder;
    }

    /**
     * 获取订单二维码并上传到服务器
     * @param orderId 订单ID
     * @return 二维码图片URL
     */
    public String createOrderQrcode(String orderId){
        try {
            File wxaCode = wxMaService.getQrcodeService().createWxaCode("/pages/home/home?orderId=" + orderId);
            
            // 将File转换为字节数组
            try (FileInputStream fis = new FileInputStream(wxaCode);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                byte[] bytes = bos.toByteArray();
                
                // 创建自定义MultipartFile实现
                MultipartFile multipartFile = new CustomMultipartFile(
                    "file",
                    "order_qrcode.png",
                    "image/png",
                    bytes
                );
                
                // 上传文件到服务器
                FileUploadVO fileUploadVO = sysFileService.uploadFile(multipartFile, null, null);
                
                return fileUploadVO.getFileUrl();
            }
        } catch (WxErrorException | IOException e) {
            throw new RuntimeException("生成订单二维码失败", e);
        }
    }
    
    /**
     * 自定义MultipartFile实现类，避免依赖外部库
     */
    private static class CustomMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;
        
        public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }
        
        @Override
        public String getContentType() {
            return contentType;
        }
        
        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }
        
        @Override
        public long getSize() {
            return content.length;
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }
        
        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }

    /**
     * 给订单分配专人
     * @param orderId 订单 ID
     * @param processor 处理人 ID
     */
    public void assignProcessor(String orderId, String processor) {
        Account account = accountService.getById(processor);
        if (account == null) {
            throw new RuntimeException("处理人不存在");
        }
        RecycleOrder recycleOrder = getById(orderId);
        if (recycleOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!account.getUseType().equals(UserUseTypeEnum.TRANSPORT.getCode())) {
           throw new RuntimeException("处理人不是司机");
        } 
        recycleOrder.setProcessor(processor);
        recycleOrder.setProcessorPhone(account.getPhone());
        updateById(recycleOrder);
    }

    /**
     * 获取专人负责的订单
     * @param recycleOrder 订单实体
     * @return 订单列表
     */
    public List<RecycleOrder> getOrdersByProcessor(RecycleOrder recycleOrder) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getProcessor, recycleOrder.getProcessor());
        if (StrUtil.isNotBlank(recycleOrder.getStatus()) && !recycleOrder.getStatus().equals("all")) {
            wrapper.eq(RecycleOrder::getStatus, recycleOrder.getStatus());
        }
        return list(wrapper);
    }
}
