package com.tutu.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.lease.dto.LeaseOrderDto;
import com.tutu.lease.entity.LeaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租赁订单Mapper接口
 */
@Mapper
public interface LeaseOrderMapper extends BaseMapper<LeaseOrder> {
    
    /**
     * 查询用户订单详情列表
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单详情列表
     */
    List<LeaseOrderDto> selectOrderDetailsByUserId(@Param("userId") String userId, @Param("status") String status);
    
    /**
     * 分页查询用户订单详情
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 订单状态
     * @return 分页订单详情
     */
    IPage<LeaseOrderDto> selectOrderDetailsPage(Page<LeaseOrderDto> page, @Param("userId") String userId, @Param("status") String status);
    
    /**
     * 根据订单ID查询订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    LeaseOrderDto selectOrderDetailById(@Param("orderId") String orderId);
    
    /**
     * 统计用户各状态订单数量
     * @param userId 用户ID
     * @return 状态统计
     */
    List<java.util.Map<String, Object>> selectOrderStatusCount(@Param("userId") String userId);
}
