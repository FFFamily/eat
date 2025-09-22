package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 回收发票明细Mapper
 */
@Mapper
public interface RecycleInvoiceDetailMapper extends BaseMapper<RecycleInvoiceDetail> {
    /**
     * 根据发票ID查询发票明细
     * @param invoiceId 发票ID
     * @return 发票明细列表
     */
    @Select("SELECT d.*, o.no as order_no,o.contract_partner as order_partner, o.contract_partner_name as order_partner_name, o.type as order_type FROM recycle_invoice_detail d left join recycle_order o on d.order_id = o.id WHERE d.invoice_id = #{invoiceId} and d.is_deleted = '0'")
    public List<RecycleInvoiceDetail> selectByInvoiceId(String invoiceId);
}