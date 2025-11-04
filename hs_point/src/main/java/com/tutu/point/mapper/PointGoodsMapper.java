package com.tutu.point.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.point.entity.PointGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 积分商品 Mapper
 */
@Mapper
public interface PointGoodsMapper extends BaseMapper<PointGoods> {
    
    /**
     * 分页查询商品（带分类名称）
     * @param page 分页对象
     * @param goodsName 商品名称（可选）
     * @param typeId 分类ID（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<PointGoods> pageGoodsWithTypeName(
            Page<PointGoods> page,
            @Param("goodsName") String goodsName,
            @Param("typeId") String typeId,
            @Param("status") String status
    );
}

