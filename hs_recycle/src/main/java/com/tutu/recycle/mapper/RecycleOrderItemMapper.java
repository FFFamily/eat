package com.tutu.recycle.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.dto.InventoryReportDto;
import com.tutu.recycle.dto.InventoryReportItemDto;
import com.tutu.recycle.entity.RecycleOrderItem;
import org.apache.ibatis.annotations.Param;


/**
 * 回收订单项Mapper
 */
@Mapper
public interface RecycleOrderItemMapper extends BaseMapper<RecycleOrderItem> {
    /**
     * 分页查询库存报表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 库存报表列表
     */
    java.util.List<InventoryReportDto> selectInventoryReport(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 获取库存报表总数
     * @return 库存报表记录总数
     */
    long selectInventoryReportCount();

    /**
     * 获取库存明细
     * @param no 货物编号
     * @return 库存明细
     */
    List<InventoryReportItemDto> selectInventoryReportDetail(@Param("no") String no);
}
