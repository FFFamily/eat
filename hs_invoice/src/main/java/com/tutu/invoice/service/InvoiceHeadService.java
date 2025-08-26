package com.tutu.invoice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.util.PageUtil;
import com.tutu.invoice.entity.InvoiceHead;
import com.tutu.invoice.mapper.InvoiceHeadMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceHeadService extends ServiceImpl<InvoiceHeadMapper, InvoiceHead> implements IService<InvoiceHead> {
    
    /**
     * 分页查询发票抬头
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param invoiceHead 发票抬头
     * @return 发票抬头列表
     */
    public List<InvoiceHead> findPage(Integer pageNum, Integer pageSize, InvoiceHead invoiceHead) {
        
        return this.baseMapper.findPage(PageUtil.calculateStartPage(pageNum, pageSize), pageSize, invoiceHead);
    }

    /**
     * 查询发票抬头数量
     * @param invoiceHead 发票抬头
     * @return 发票抬头数量
     */
    public long findPageCount(InvoiceHead invoiceHead) {
        return this.baseMapper.findPageCount(invoiceHead);
    }

    /**
     * 根据账号ID查询发票抬头列表
     * @param accountId 账号ID
     * @return 发票抬头列表
     */
    public List<InvoiceHead> getByAccountId(String accountId) {
        QueryWrapper<InvoiceHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId)
                   .orderByDesc("is_default")
                   .orderByDesc("create_time");
        return this.list(queryWrapper);
    }
    
    /**
     * 根据账号ID查询默认发票抬头
     * @param accountId 账号ID
     * @return 默认发票抬头
     */
    public InvoiceHead getDefaultByAccountId(String accountId) {
        QueryWrapper<InvoiceHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_id", accountId)
                   .eq("is_default", true);
        return this.getOne(queryWrapper);
    }
    
    /**
     * 设置默认发票抬头
     * @param accountId 账号ID
     * @param invoiceHeadId 发票抬头ID
     * @return 是否设置成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultInvoiceHead(String accountId, String invoiceHeadId) {
        // 先取消该账号下所有发票抬头的默认状态
        baseMapper.cancelAllDefault(accountId);
        
        // 设置指定发票抬头为默认
        return baseMapper.setAsDefault(invoiceHeadId) > 0;
    }
    
    /**
     * 添加发票抬头（如果是第一个，自动设为默认）
     * @param invoiceHead 发票抬头信息
     * @return 是否添加成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addInvoiceHead(InvoiceHead invoiceHead) {
        // 检查该账号下是否已有发票抬头
        List<InvoiceHead> existingHeads = getByAccountId(invoiceHead.getAccountId());
        
        // 如果是第一个发票抬头，自动设为默认
        if (existingHeads.isEmpty()) {
            invoiceHead.setIsDefault(CommonConstant.YES_STR);
        } else if (invoiceHead.getIsDefault() != null && invoiceHead.getIsDefault().equals(CommonConstant.YES_STR)) {
            // 如果设置为默认，需要取消其他默认状态
            baseMapper.cancelAllDefault(invoiceHead.getAccountId());
        }
        
        return this.save(invoiceHead);
    }
    
    /**
     * 更新发票抬头
     * @param invoiceHead 发票抬头信息
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInvoiceHead(InvoiceHead invoiceHead) {
        // 如果设置为默认，需要取消其他默认状态
        if (invoiceHead.getIsDefault() != null && invoiceHead.getIsDefault().equals(CommonConstant.YES_STR)) {
            baseMapper.cancelAllDefault(invoiceHead.getAccountId());
        }
        
        return this.updateById(invoiceHead);
    }
    
    /**
     * 删除发票抬头
     * @param id 发票抬头ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInvoiceHead(String id) {
        InvoiceHead invoiceHead = this.getById(id);
        if (invoiceHead == null) {
            return false;
        }
        
        boolean isDeleted = this.removeById(id);
        
        // 如果删除的是默认发票抬头，需要设置其他发票抬头为默认
        if (isDeleted && invoiceHead.getIsDefault().equals(CommonConstant.YES_STR)) {
            List<InvoiceHead> remainingHeads = getByAccountId(invoiceHead.getAccountId());
            if (!remainingHeads.isEmpty()) {
                // 设置第一个为默认
                InvoiceHead firstHead = remainingHeads.getFirst();
                firstHead.setIsDefault(CommonConstant.YES_STR);
                this.updateById(firstHead);
            }
        }
        
        return isDeleted;
    }
}
