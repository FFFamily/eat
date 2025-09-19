package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import com.tutu.recycle.mapper.RecycleInvoiceDetailMapper;
import com.tutu.recycle.mapper.RecycleInvoiceMapper;
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
public class RecycleInvoiceService {
    
    @Autowired
    private RecycleInvoiceMapper recycleInvoiceMapper;
    
    @Autowired
    private RecycleInvoiceDetailMapper recycleInvoiceDetailMapper;
    
    /**
     * 创建发票
     * @param invoice 发票信息
     * @param details 发票明细列表
     * @return 是否成功
     */
    @Transactional
    public boolean createInvoice(RecycleInvoice invoice, List<RecycleInvoiceDetail> details) {
        // 计算总金额
        BigDecimal totalAmount = details.stream()
                .map(RecycleInvoiceDetail::getOrderTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算税额（假设税率为13%）
        BigDecimal taxRate = new BigDecimal("0.13");
        BigDecimal taxAmount = totalAmount.multiply(taxRate);
        BigDecimal amountWithoutTax = totalAmount.subtract(taxAmount);
        
        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setAmountWithoutTax(amountWithoutTax);
        invoice.setCreateTime(new Date());
        
        // 保存发票
        int result = recycleInvoiceMapper.insert(invoice);
        if (result <= 0) {
            return false;
        }
        
        // 保存发票明细
        for (RecycleInvoiceDetail detail : details) {
            detail.setInvoiceId(invoice.getId());
            detail.setCreateTime(new Date());
            recycleInvoiceDetailMapper.insert(detail);
        }
        
        return true;
    }
    
    /**
     * 更新发票
     * @param invoice 发票信息
     * @param details 发票明细列表
     * @return 是否成功
     */
    @Transactional
    public boolean updateInvoice(RecycleInvoice invoice, List<RecycleInvoiceDetail> details) {
        // 删除原有明细
        QueryWrapper<RecycleInvoiceDetail> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("invoice_id", invoice.getId());
        recycleInvoiceDetailMapper.delete(deleteWrapper);
        
        // 重新计算金额
        if (details != null && !details.isEmpty()) {
            BigDecimal totalAmount = details.stream()
                    .map(RecycleInvoiceDetail::getOrderTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal taxRate = new BigDecimal("0.13");
            BigDecimal taxAmount = totalAmount.multiply(taxRate);
            BigDecimal amountWithoutTax = totalAmount.subtract(taxAmount);
            
            invoice.setTotalAmount(totalAmount);
            invoice.setTaxAmount(taxAmount);
            invoice.setAmountWithoutTax(amountWithoutTax);
        }
        
        invoice.setUpdateTime(new Date());
        
        // 更新发票
        int result = recycleInvoiceMapper.updateById(invoice);
        if (result <= 0) {
            return false;
        }
        
        // 保存新明细
        if (details != null && !details.isEmpty()) {
            for (RecycleInvoiceDetail detail : details) {
                detail.setInvoiceId(invoice.getId());
                detail.setCreateTime(new Date());
                recycleInvoiceDetailMapper.insert(detail);
            }
        }
        
        return true;
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
     * 获取发票明细
     * @param invoiceId 发票ID
     * @return 发票明细列表
     */
    public List<RecycleInvoiceDetail> getInvoiceDetails(String invoiceId) {
        QueryWrapper<RecycleInvoiceDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("invoice_id", invoiceId);
        return recycleInvoiceDetailMapper.selectList(wrapper);
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
}