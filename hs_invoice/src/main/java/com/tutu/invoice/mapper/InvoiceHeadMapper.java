package com.tutu.invoice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.invoice.entity.InvoiceHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InvoiceHeadMapper extends BaseMapper<InvoiceHead> {

    /**
     * 分页查询发票抬头
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param invoiceHead 发票抬头
     * @return 发票抬头列表
     */
    List<InvoiceHead> findPage(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("invoiceHead") InvoiceHead invoiceHead);

    /**
     * 查询发票抬头数量
     * @param invoiceHead 发票抬头
     * @return 发票抬头数量
     */
    long findPageCount(@Param("invoiceHead") InvoiceHead invoiceHead);
    
    /**
     * 根据账号ID查询发票抬头列表
     * @param accountId 账号ID
     * @return 发票抬头列表
     */
    List<InvoiceHead> selectByAccountId(@Param("accountId") String accountId);
    
    /**
     * 根据账号ID查询默认发票抬头
     * @param accountId 账号ID
     * @return 默认发票抬头
     */
    InvoiceHead selectDefaultByAccountId(@Param("accountId") String accountId);
    
    /**
     * 取消账号下所有发票抬头的默认状态
     * @param accountId 账号ID
     * @return 影响行数
     */
    @Update("UPDATE invoice_head SET is_default = false WHERE account_id = #{accountId}")
    int cancelAllDefault(@Param("accountId") String accountId);
    
    /**
     * 设置指定发票抬头为默认
     * @param id 发票抬头ID
     * @return 影响行数
     */
    @Update("UPDATE invoice_head SET is_default = true WHERE id = #{id}")
    int setAsDefault(@Param("id") String id);
}
