package com.tutu.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.lease.dto.LeaseCartDto;
import com.tutu.lease.entity.LeaseCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租赁购物车Mapper接口
 */
@Mapper
public interface LeaseCartMapper extends BaseMapper<LeaseCart> {
    
    /**
     * 查询用户购物车详情列表
     * @param userId 用户ID
     * @param status 状态
     * @return 购物车详情列表
     */
    List<LeaseCartDto> selectCartDetailsByUserId(@Param("userId") String userId, @Param("status") Integer status);
    
    /**
     * 查询用户购物车商品总数量
     * @param userId 用户ID
     * @param status 状态
     * @return 总数量
     */
    Integer selectCartItemCountByUserId(@Param("userId") String userId, @Param("status") Integer status);
}
