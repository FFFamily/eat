package com.tutu.lease.example;

import com.tutu.lease.dto.CreateOrderFromGoodsRequest;
import com.tutu.lease.dto.CreateOrderFromGoodsRequest.OrderGoodsInfo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 创建订单示例
 * 展示如何使用新的API通过商品信息直接创建订单
 */
public class CreateOrderExample {

    public static void main(String[] args) {
        // 创建商品信息
        OrderGoodsInfo goodsInfo1 = new OrderGoodsInfo();
        goodsInfo1.setGoodId("GOOD001");
        goodsInfo1.setGoodName("iPhone 15 Pro");
        goodsInfo1.setGoodPrice(new BigDecimal("100.00"));
        goodsInfo1.setQuantity(1);
        goodsInfo1.setLeaseStartTime(new Date()); // 今天开始
        goodsInfo1.setLeaseEndTime(new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L)); // 5天后结束
        goodsInfo1.setLeaseDays(5);
        goodsInfo1.setSubtotal(new BigDecimal("500.00")); // 100 * 1 * 5
        goodsInfo1.setRemark("全新iPhone，无划痕");

        OrderGoodsInfo goodsInfo2 = new OrderGoodsInfo();
        goodsInfo2.setGoodId("GOOD002");
        goodsInfo2.setGoodName("MacBook Pro");
        goodsInfo2.setGoodPrice(new BigDecimal("200.00"));
        goodsInfo2.setQuantity(1);
        goodsInfo2.setLeaseStartTime(new Date());
        goodsInfo2.setLeaseEndTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)); // 7天后结束
        goodsInfo2.setLeaseDays(7);
        goodsInfo2.setSubtotal(new BigDecimal("1400.00")); // 200 * 1 * 7
        goodsInfo2.setRemark("高性能笔记本");

        // 创建订单请求
        CreateOrderFromGoodsRequest request = new CreateOrderFromGoodsRequest();
        request.setGoodsList(Arrays.asList(goodsInfo1, goodsInfo2));
        request.setReceiverName("张三");
        request.setReceiverPhone("13800138000");
        request.setReceiverAddress("北京市朝阳区xxx街道xxx号");
        request.setReturnAddress("北京市朝阳区xxx街道xxx号");
        request.setRemark("请尽快发货");

        // 调用API创建订单
        // LeaseOrderService leaseOrderService = ...; // 注入服务
        // leaseOrderService.createOrderFromGoods(request);
        
        System.out.println("订单创建请求已准备完成");
        System.out.println("总金额: " + request.getGoodsList().stream()
                .map(OrderGoodsInfo::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
} 