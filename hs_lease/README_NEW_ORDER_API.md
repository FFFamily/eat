# 租赁订单API - 通过商品信息创建订单

## 新增功能

新增了一个通过商品信息直接创建订单的API，无需先添加到购物车。

## API接口

### 通过商品信息创建订单

**接口地址：** `POST /lease/order/create-from-goods`

**请求参数：**
```json
{
  "goodsList": [
    {
      "goodId": "商品ID",
      "goodName": "商品名称",
      "goodPrice": 100.00,
      "quantity": 1,
      "leaseStartTime": "2024-01-01T00:00:00.000Z",
      "leaseEndTime": "2024-01-05T00:00:00.000Z",
      "leaseDays": 5,
      "subtotal": 500.00,
      "remark": "商品备注"
    }
  ],
  "receiverName": "收货人姓名",
  "receiverPhone": "收货人手机号",
  "receiverAddress": "收货地址",
  "returnAddress": "归还地址",
  "remark": "订单备注"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 与原有API的区别

### 原有API（从购物车创建订单）
- **接口：** `POST /lease/order/create`
- **参数：** 需要购物车ID列表
- **流程：** 先添加到购物车 → 从购物车创建订单

### 新增API（直接通过商品信息创建订单）
- **接口：** `POST /lease/order/create-from-goods`
- **参数：** 直接传递商品信息列表
- **流程：** 直接创建订单，无需购物车

## 使用场景

1. **快速下单：** 用户可以直接选择商品并下单，无需先添加到购物车
2. **批量下单：** 一次性选择多个商品直接下单
3. **第三方集成：** 其他系统可以直接调用此API创建订单

## 注意事项

1. 商品信息中的`subtotal`字段需要提前计算好（数量 × 单价 × 天数）
2. `leaseDays`字段需要根据开始时间和结束时间计算
3. 订单创建后状态为"待支付"
4. 押金自动计算为总金额的20% 