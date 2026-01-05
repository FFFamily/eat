package com.tutu.api.controller.wx.point;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.constant.CommonConstant;
import com.tutu.point.entity.AccountPointDetail;
import com.tutu.point.entity.AccountPointUseDetail;
import com.tutu.point.entity.PointGoods;
import com.tutu.point.entity.PointGoodsType;
import com.tutu.point.service.AccountPointDetailService;
import com.tutu.point.service.AccountPointUseDetailService;
import com.tutu.point.service.PointGoodsService;
import com.tutu.point.service.PointGoodsTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import com.tutu.common.Response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/wx/point")
public class WxPointController {
   @Resource
   private AccountPointDetailService accountPointService;
   @Resource
   private AccountPointDetailService accountPointDetailService;
   @Resource
   private PointGoodsTypeService pointGoodsTypeService;
   @Resource
   private PointGoodsService pointGoodsService;
   @Resource
   private AccountPointUseDetailService accountPointUseDetailService;
   /**
    * 获取当前登录人的积分
    */
   @PostMapping("/get")
   public BaseResponse<Long> getPoint() {
       String accountId = StpUtil.getLoginIdAsString();
       long point = accountPointService.getPointByAccountId(accountId);
       return BaseResponse.success(point);
   }
   /**
    * 获取当前登录人的积分明细
    */
   @PostMapping("/detail")
   public BaseResponse<List<AccountPointDetail>> getPointDetail() {
       String accountId = StpUtil.getLoginIdAsString();
       List<AccountPointDetail> pointDetail = accountPointDetailService.getByAccountId(accountId);
       return BaseResponse.success(pointDetail);
   }

    /**
     * 获取商品分类列表
     */
     @PostMapping("/goods/category")
     public BaseResponse<List<PointGoodsType>> getGoodsCategory() {
         List<PointGoodsType> categoryList = pointGoodsTypeService.list(
                 new LambdaQueryWrapper<PointGoodsType>()
                         .eq(PointGoodsType::getStatus, CommonConstant.YES_STR)
                         .orderByAsc(PointGoodsType::getSortNum)
         );
         return BaseResponse.success(categoryList);
     }

    /**
     * 获取指定类型的商品列表
     */
     @PostMapping("/goods/list/{typeId}")
     public BaseResponse<List<PointGoods>> getGoodsList(@PathVariable String typeId) {
         List<PointGoods> goodsList = pointGoodsService.getGoodsByTypeId(typeId);
         return BaseResponse.success(goodsList);
     }

    /**
     * 获取商品兑换历史
     */
     @PostMapping("/goods/history")
     public BaseResponse<List<AccountPointUseDetail>> getGoodsHistory() {
         String accountId = StpUtil.getLoginIdAsString();
         List<AccountPointUseDetail> historyList = accountPointUseDetailService.getByAccountId(accountId);
         return BaseResponse.success(historyList);
     }
    /**
     * 兑换商品
     */
    @PostMapping("/goods/exchange")
    public BaseResponse<AccountPointUseDetail> exchangeGoods(@RequestBody AccountPointUseDetail useDetail) {
        String accountId = StpUtil.getLoginIdAsString();
        useDetail.setAccountId(accountId);
        return BaseResponse.success(accountPointUseDetailService.exchangeGoods(useDetail));
    }

    /**
     * 消耗积分，和商品无关逻辑
     * @param useDetail
     * @return
     */
    @PostMapping("/exchange")
    public BaseResponse<Void> exchange(@RequestBody AccountPointDetail useDetail) {
        String accountId = StpUtil.getLoginIdAsString();
        useDetail.setAccountId(accountId);
        accountPointService.exchangePoint(useDetail);
        return BaseResponse.success();
    }

    /**
     * 获取对应活动的消耗积分
     * @return
     */
    @PostMapping("/getAccountActivityPoint")
    public BaseResponse<Long> getActivityPoint(@RequestBody AccountPointDetail useDetail) {
        String accountId = StpUtil.getLoginIdAsString();
        return BaseResponse.success( accountPointService.getAccountActivityPoint(accountId,useDetail.getMark()));
    }



}
