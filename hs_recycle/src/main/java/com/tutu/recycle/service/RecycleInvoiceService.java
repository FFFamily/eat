package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.enums.RecycleInvoiceStatusEnum;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.mapper.RecycleInvoiceDetailMapper;
import com.tutu.recycle.mapper.RecycleInvoiceMapper;
import com.tutu.recycle.request.ConfirmInvoiceRequest;
import com.tutu.recycle.request.InvoiceDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 回收发票服务
 */
@Service
public class RecycleInvoiceService extends ServiceImpl<RecycleInvoiceMapper, RecycleInvoice> {
    
    @Autowired
    private RecycleInvoiceMapper recycleInvoiceMapper;
    
    @Autowired
    private RecycleInvoiceDetailMapper recycleInvoiceDetailMapper;
    
    @Autowired
    private RecycleOrderService recycleOrderService;
    
    /**
     * 创建发票
     * @param invoice 发票信息
     * @param details 发票明细列表
     * @return 是否成功
     */
    @Transactional
    public void createInvoice(RecycleInvoice invoice, List<RecycleInvoiceDetail> details) {
        // 计算总金额
        BigDecimal totalAmount = details.stream()
                .map(RecycleInvoiceDetail::getOrderTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
     
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus(RecycleInvoiceStatusEnum.PENDING.getCode()); // 设置默认状态为待开票
        invoice.setInvoiceAccountId(details.getFirst().getOrderPartner());
        // 保存发票
        recycleInvoiceMapper.insert(invoice);
        // 保存发票明细
        for (RecycleInvoiceDetail detail : details) {
            detail.setInvoiceId(invoice.getId());
            recycleInvoiceDetailMapper.insert(detail);
        }
    }
    
    /**
     * 更新发票
     * @param invoice 发票信息
     * @param details 发票明细列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoice(RecycleInvoice invoice, List<RecycleInvoiceDetail> details) {
        // 删除原有明细
        QueryWrapper<RecycleInvoiceDetail> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("invoice_id", invoice.getId());
        recycleInvoiceDetailMapper.delete(deleteWrapper);
        // 更新发票
        recycleInvoiceMapper.updateById(invoice);
        invoice.setInvoiceAccountId(details.get(0).getOrderPartner());
        // 保存新明细
        if (details != null && !details.isEmpty()) {
            for (RecycleInvoiceDetail detail : details) {
                detail.setInvoiceId(invoice.getId());
                recycleInvoiceDetailMapper.insert(detail);
            }
        }
    }
    
    /**
     * 删除发票
     * @param invoiceId 发票ID
     * @return 是否成功
     */
    @Transactional
    public boolean deleteInvoice(String invoiceId) {
        // 删除发票明细
        QueryWrapper<RecycleInvoiceDetail> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("invoice_id", invoiceId);
        recycleInvoiceDetailMapper.delete(deleteWrapper);
        
        // 删除发票
        int result = recycleInvoiceMapper.deleteById(invoiceId);
        return result > 0;
    }
    
    /**
     * 根据ID获取发票
     * @param invoiceId 发票ID
     * @return 发票信息
     */
    public RecycleInvoice getInvoiceById(String invoiceId) {
        return recycleInvoiceMapper.selectById(invoiceId);
    }
    
    /**
     * 根据ID获取发票完整信息（包含明细）
     * @param invoiceId 发票ID
     * @return 发票完整信息
     */
    public InvoiceDetailResponse getInvoiceWithDetails(String invoiceId) {
        // 获取发票基本信息
        RecycleInvoice invoice = recycleInvoiceMapper.selectById(invoiceId);
        if (invoice == null) {
            return null;
        }
        
        // 获取发票明细
        List<RecycleInvoiceDetail> details = getInvoiceDetails(invoiceId);
        
        // 构建返回对象
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        response.setInvoice(invoice);
        response.setDetails(details);
        
        return response;
    }
    
    /**
     * 获取发票明细
     * @param invoiceId 发票ID
     * @return 发票明细列表
     */
    public List<RecycleInvoiceDetail> getInvoiceDetails(String invoiceId) {
        return recycleInvoiceDetailMapper.selectByInvoiceId(invoiceId);
    }
    
    /**
     * 分页查询发票
     * @param page 页码
     * @param size 每页大小
     * @param invoiceType 发票类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    public IPage<RecycleInvoice> getInvoicePage(int page, int size, String invoiceType, String status) {
        Page<RecycleInvoice> pageParam = new Page<>(page, size);
        QueryWrapper<RecycleInvoice> wrapper = new QueryWrapper<>();
        
        if (invoiceType != null && !invoiceType.isEmpty()) {
            wrapper.eq("invoice_type", invoiceType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        
        return recycleInvoiceMapper.selectPage(pageParam, wrapper);
    }
    
    /**
     * 根据发票号码查询
     * @param invoiceNo 发票号码
     * @return 发票信息
     */
    public RecycleInvoice getInvoiceByNo(String invoiceNo) {
        QueryWrapper<RecycleInvoice> wrapper = new QueryWrapper<>();
        wrapper.eq("invoice_no", invoiceNo);
        return recycleInvoiceMapper.selectOne(wrapper);
    }
    
    /**
     * 更新发票状态
     * @param invoiceId 发票ID
     * @param status 新状态
     * @return 是否成功
     */
    public boolean updateInvoiceStatus(String invoiceId, String status) {
        RecycleInvoice invoice = new RecycleInvoice();
        invoice.setId(invoiceId);
        invoice.setStatus(status);
        invoice.setUpdateTime(new Date());
        
        int result = recycleInvoiceMapper.updateById(invoice);
        return result > 0;
    }
    
    /**
     * 设置开票时间
     * @param invoiceId 发票ID
     * @param invoiceTime 开票时间
     * @return 是否成功
     */
    public boolean setInvoiceTime(String invoiceId, Date invoiceTime) {
        RecycleInvoice invoice = new RecycleInvoice();
        invoice.setId(invoiceId);
        invoice.setInvoiceTime(invoiceTime);
        invoice.setUpdateTime(new Date());
        
        int result = recycleInvoiceMapper.updateById(invoice);
        return result > 0;
    }
    
    /**
     * 确认发票
     * @param request 确认发票请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmInvoice(ConfirmInvoiceRequest request) {
        // 根据发票ID查询发票信息
        RecycleInvoice invoice = recycleInvoiceMapper.selectById(request.getInvoiceId());
        if (invoice == null) {
            throw new RuntimeException("发票不存在");
        }
        
        // 计算不含税金额 = 总金额 - 税额
        BigDecimal amountWithoutTax = request.getTotalAmount().subtract(request.getTaxAmount());
        
        // 更新发票信息
        invoice.setProcessor(request.getProcessor());
        invoice.setInvoiceNo(request.getInvoiceNo());
        invoice.setInvoiceTime(request.getInvoiceTime());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setTaxAmount(request.getTaxAmount());
        invoice.setAmountWithoutTax(amountWithoutTax);
        invoice.setStatus(RecycleInvoiceStatusEnum.INVOICED.getCode()); // 更新状态为已开票
        invoice.setUpdateTime(new Date());
        
        // 保存发票更新
        int result = recycleInvoiceMapper.updateById(invoice);
        if (result <= 0) {
            throw new RuntimeException("发票更新失败");
        }
        
        // 更新相关订单状态为已开票
        updateRelatedOrdersStatus(invoice.getId());
        
        return true;
    }
    
    /**
     * 更新相关订单状态为已开票
     * @param invoiceId 发票ID
     */
    private void updateRelatedOrdersStatus(String invoiceId) {
        // 获取发票明细，从中获取订单编号
        List<RecycleInvoiceDetail> details = getInvoiceDetails(invoiceId);
        
        for (RecycleInvoiceDetail detail : details) {
            String orderNo = detail.getOrderNo();
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                // 根据订单编号查询订单
                RecycleOrder order = recycleOrderService.getByOrderNo(orderNo);
                if (order != null) {
                    // 更新订单状态为已开票
                    order.setStatus(RecycleOrderStatusEnum.INVOICED.getCode());
                    order.setUpdateTime(new Date());
                    recycleOrderService.updateById(order);
                }
            }
        }
    }
}