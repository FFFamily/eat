package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回收发票明细Mapper
 */
@Mapper
public interface RecycleInvoiceDetailMapper extends BaseMapper<RecycleInvoiceDetail> {
}