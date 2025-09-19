package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.entity.RecycleInvoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回收发票Mapper
 */
@Mapper
public interface RecycleInvoiceMapper extends BaseMapper<RecycleInvoice> {
}