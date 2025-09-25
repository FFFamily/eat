package com.tutu.api.controller.wx;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.enums.RecycleInvoiceStatusEnum;
import com.tutu.recycle.request.InvoiceDetailResponse;
import com.tutu.recycle.request.InvoiceListRequest;
import com.tutu.recycle.request.InvoiceDetailRequest;
import com.tutu.recycle.mapper.RecycleInvoiceMapper;
import com.tutu.recycle.service.RecycleInvoiceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信端发票控制器
 */
@RestController
@RequestMapping("/wx/invoice")
public class WxInvoiceController {
    
    @Resource
    private RecycleInvoiceService recycleInvoiceService;
    
    @Resource
    private RecycleInvoiceMapper recycleInvoiceMapper;
    
    /**
     * 获取当前用户的发票列表（支持状态过滤）
     * @param request 发票列表查询请求
     * @return 发票列表
     */
    @PostMapping("/current/list")
    public BaseResponse<List<RecycleInvoice>> getCurrentUserInvoiceList(@RequestBody InvoiceListRequest request) {
        
        try {
            // 获取当前登录用户ID
            String userId = StpUtil.getLoginIdAsString();
            
            // 查询发票
            LambdaQueryWrapper<RecycleInvoice> wrapper = new LambdaQueryWrapper<>();
            
            // 根据用户ID过滤发票（使用新增的invoiceAccountId字段）
            wrapper.eq(RecycleInvoice::getInvoiceAccountId, userId);
            
            // 状态过滤
            if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
                wrapper.eq(RecycleInvoice::getStatus, request.getStatus());
            }
            
            // 发票类型过滤
            if (request.getInvoiceType() != null && !request.getInvoiceType().trim().isEmpty()) {
                wrapper.eq(RecycleInvoice::getInvoiceType, request.getInvoiceType());
            }
            
            // 按创建时间倒序排列
            wrapper.orderByDesc(RecycleInvoice::getCreateTime);
            
            List<RecycleInvoice> result = recycleInvoiceMapper.selectList(wrapper);
            
            return BaseResponse.success(result);
            
        } catch (Exception e) {
            return BaseResponse.error("查询发票列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取发票详情（包含明细）
     * @param request 发票详情查询请求
     * @return 发票详情
     */
    @PostMapping("/detail")
    public BaseResponse<InvoiceDetailResponse> getInvoiceDetail(@RequestBody InvoiceDetailRequest request) {
        try {
            // 获取当前登录用户ID
            String userId = StpUtil.getLoginIdAsString();
            
            // 验证发票是否属于当前用户
            if (!isInvoiceBelongsToUser(request.getInvoiceId(), userId)) {
                return BaseResponse.error("无权限访问该发票");
            }
            
            // 获取发票详情
            InvoiceDetailResponse invoiceDetail = recycleInvoiceService.getInvoiceWithDetails(request.getInvoiceId());
            if (invoiceDetail == null) {
                return BaseResponse.error("发票不存在");
            }
            
            return BaseResponse.success(invoiceDetail);
            
        } catch (Exception e) {
            return BaseResponse.error("查询发票详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有发票状态枚举
     * @return 状态列表
     */
    @PostMapping("/status/all")
    public BaseResponse<List<Map<String, String>>> getAllInvoiceStatus() {
        try {
            RecycleInvoiceStatusEnum[] values = RecycleInvoiceStatusEnum.values();
            List<Map<String, String>> statusList = new ArrayList<>();
            
            for (RecycleInvoiceStatusEnum status : values) {
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put("value", status.getCode());
                statusMap.put("label", status.getDescription());
                statusList.add(statusMap);
            }
            
            return BaseResponse.success(statusList);
            
        } catch (Exception e) {
            return BaseResponse.error("获取发票状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证发票是否属于当前用户
     * @param invoiceId 发票ID
     * @param userId 用户ID
     * @return 是否属于当前用户
     */
    private boolean isInvoiceBelongsToUser(String invoiceId, String userId) {
        try {
            // 获取发票信息
            RecycleInvoice invoice = recycleInvoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                return false;
            }
            
            // 直接比较发票的invoiceAccountId与当前用户ID
            return userId.equals(invoice.getInvoiceAccountId());
            
        } catch (Exception e) {
            return false;
        }
    }
}
