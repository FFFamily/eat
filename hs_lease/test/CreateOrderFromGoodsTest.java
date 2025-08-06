package com.tutu.lease.test;

import com.tutu.lease.dto.CreateOrderFromGoodsRequest;
import com.tutu.lease.dto.CreateOrderFromGoodsRequest.OrderGoodsInfo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 创建订单测试类
 * 用于验证通过商品信息创建订单的功能
 */
public class CreateOrderFromGoodsTest {

    public static void main(String[] args) {
        testCreateOrderFromGoods();
    }

    /**
     * 测试通过商品信息创建订单
     */
    public static void testCreateOrderFromGoods() {
        try {
            // 创建测试商品信息
            OrderGoodsInfo goodsInfo = createTestGoodsInfo();
            
            // 创建订单请求
            CreateOrderFromGoodsRequest request = createTestOrderRequest(goodsInfo);
            
            // 验证请求数据
            validateRequest(request);
            
            System.out.println("✅ 测试通过：订单请求数据验证成功");
            System.out.println("📦 商品数量: " + request.getGoodsList().size());
            System.out.println("💰 总金额: " + calculateTotalAmount(request));
            System.out.println("📅 租赁时间范围: " + getLeaseTimeRange(request));
            
        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 创建测试商品信息
     */
    private static OrderGoodsInfo createTestGoodsInfo() {
        OrderGoodsInfo goodsInfo = new OrderGoodsInfo();
        goodsInfo.setGoodId("TEST001");
        goodsInfo.setGoodName("测试商品");
        goodsInfo.setGoodPrice(new BigDecimal("100.00"));
        goodsInfo.setQuantity(2);
        
        // 设置租赁时间：今天开始，5天后结束
        Date startTime = new Date();
        Date endTime = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L);
        
        goodsInfo.setLeaseStartTime(startTime);
        goodsInfo.setLeaseEndTime(endTime);
        goodsInfo.setLeaseDays(5);
        
        // 计算小计：单价 * 数量 * 天数
        BigDecimal subtotal = goodsInfo.getGoodPrice()
                .multiply(new BigDecimal(goodsInfo.getQuantity()))
                .multiply(new BigDecimal(goodsInfo.getLeaseDays()));
        goodsInfo.setSubtotal(subtotal);
        
        goodsInfo.setRemark("测试商品备注");
        
        return goodsInfo;
    }

    /**
     * 创建测试订单请求
     */
    private static CreateOrderFromGoodsRequest createTestOrderRequest(OrderGoodsInfo goodsInfo) {
        CreateOrderFromGoodsRequest request = new CreateOrderFromGoodsRequest();
        request.setGoodsList(Arrays.asList(goodsInfo));
        request.setReceiverName("测试用户");
        request.setReceiverPhone("13800138000");
        request.setReceiverAddress("测试地址");
        request.setReturnAddress("测试归还地址");
        request.setRemark("测试订单");
        
        return request;
    }

    /**
     * 验证请求数据
     */
    private static void validateRequest(CreateOrderFromGoodsRequest request) {
        if (request.getGoodsList() == null || request.getGoodsList().isEmpty()) {
            throw new RuntimeException("商品列表不能为空");
        }
        
        for (OrderGoodsInfo goodsInfo : request.getGoodsList()) {
            if (goodsInfo.getGoodId() == null || goodsInfo.getGoodId().trim().isEmpty()) {
                throw new RuntimeException("商品ID不能为空");
            }
            
            if (goodsInfo.getGoodPrice() == null || goodsInfo.getGoodPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("商品单价必须大于0");
            }
            
            if (goodsInfo.getQuantity() == null || goodsInfo.getQuantity() <= 0) {
                throw new RuntimeException("商品数量必须大于0");
            }
            
            if (goodsInfo.getLeaseEndTime().before(goodsInfo.getLeaseStartTime())) {
                throw new RuntimeException("租赁结束时间不能早于开始时间");
            }
        }
    }

    /**
     * 计算总金额
     */
    private static BigDecimal calculateTotalAmount(CreateOrderFromGoodsRequest request) {
        return request.getGoodsList().stream()
                .map(OrderGoodsInfo::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取租赁时间范围
     */
    private static String getLeaseTimeRange(CreateOrderFromGoodsRequest request) {
        Date minStartTime = request.getGoodsList().stream()
                .map(OrderGoodsInfo::getLeaseStartTime)
                .min(Date::compareTo)
                .orElse(new Date());
        
        Date maxEndTime = request.getGoodsList().stream()
                .map(OrderGoodsInfo::getLeaseEndTime)
                .max(Date::compareTo)
                .orElse(new Date());
        
        return minStartTime + " 至 " + maxEndTime;
    }
} 