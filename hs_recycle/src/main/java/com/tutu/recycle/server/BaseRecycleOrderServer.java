package com.tutu.recycle.server;

import cn.hutool.core.util.IdUtil;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.schema.RecycleOrderInfo;
import com.tutu.recycle.utils.CodeUtil;
import com.tutu.user.entity.Processor;
import com.tutu.user.service.ProcessorService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 回收订单Server主类
 * 负责执行订单创建的核心逻辑，并委托给各个类型的Server处理特定属性赋值
 */
@Component
public class BaseRecycleOrderServer {
    
    @Resource
    private CodeUtil codeUtil;
    
    @Resource
    private ProcessorService processorService;
    
    private Map<RecycleOrderTypeEnum, RecycleOrderServer> orderServerMap;
    
    /**
     * 通过构造函数注入所有RecycleOrderServer实现
     */
    @Autowired(required = false)
    public void setOrderServers(List<RecycleOrderServer> orderServers) {
        if (orderServers == null || orderServers.isEmpty()) {
            this.orderServerMap = new java.util.HashMap<>();
            return;
        }
        this.orderServerMap = orderServers.stream()
                .filter(server -> getOrderTypeFromServer(server) != null)
                .collect(Collectors.toMap(
                        this::getOrderTypeFromServer,
                        Function.identity()
                ));
    }
    
    /**
     * 根据Server类型获取对应的订单类型
     */
    private RecycleOrderTypeEnum getOrderTypeFromServer(RecycleOrderServer server) {
        if (server instanceof PurchaseOrderServer) {
            return RecycleOrderTypeEnum.PURCHASE;
        } else if (server instanceof TransportOrderServer) {
            return RecycleOrderTypeEnum.TRANSPORT;
        } else if (server instanceof ProcessingOrderServer) {
            return RecycleOrderTypeEnum.PROCESSING;
        } else if (server instanceof StorageOrderServer) {
            return RecycleOrderTypeEnum.STORAGE;
        }
        return null;
    }
    
    /**
     * 根据用户订单和回收订单类型创建回收订单
     *
     * @param userOrderRequest 用户订单对象
     * @param order
     * @param orderType        回收订单类型
     * @param isNeedSettle
     * @return 创建的回收订单
     */
    public RecycleOrderInfo createRecycleOrderFromUserOrderByType(UserOrderDTO userOrderRequest, UserOrder order,
                                                                  RecycleOrderTypeEnum orderType, Boolean isNeedSettle) {
        if (userOrderRequest == null) {
            throw new ServiceException("用户订单不能为空");
        }
        if (orderType == null) {
            throw new ServiceException("回收订单类型不能为空");
        }
        
        // 将UserOrder转换为UserOrderDTO
//        UserOrderDTO userOrderDTO = convertToDTO(order);
        
        // 创建基础回收订单对象
        RecycleOrderInfo recycleOrder = new RecycleOrderInfo();
        
        // 填充基础属性（所有订单类型共有的属性）
        fillBaseProperties(recycleOrder, userOrderRequest,order, orderType, isNeedSettle);
        
        // 获取对应类型的Server并填充特定属性
        if (orderServerMap != null) {
            RecycleOrderServer orderServer = orderServerMap.get(orderType);
            if (orderServer != null) {
                orderServer.fillOrderProperties(recycleOrder, userOrderRequest);
            }
        }
        
        return recycleOrder;
    }
    
    /**
     * 填充基础属性（所有订单类型共有的属性）
     */
    private void fillBaseProperties(RecycleOrderInfo recycleOrder, UserOrderDTO userOrderRequest, UserOrder order, RecycleOrderTypeEnum orderType, Boolean isNeedSettle) {
        // 生成回收订单编号
        recycleOrder.setNo(IdUtil.simpleUUID());
        // 生成订单识别码
        recycleOrder.setIdentifyCode(codeUtil.generateOrderCode());
        // 设置父订单ID
        recycleOrder.setParentId(order.getId());
        // 设置订单类型
        recycleOrder.setType(orderType.getCode());
        

        
        // 复制合同相关信息
        recycleOrder.setContractId(order.getContractId());
        recycleOrder.setContractNo(order.getContractNo());
        recycleOrder.setContractName(order.getContractName());
        recycleOrder.setContractPartner(order.getContractPartner());
        recycleOrder.setContractPartnerName(order.getContractPartnerName());
        
        // 复制甲乙方信息
        recycleOrder.setPartyA(order.getPartyA());
        recycleOrder.setPartyAName(order.getPartyAName());
        recycleOrder.setPartyB(order.getPartyB());
        recycleOrder.setPartyBName(order.getPartyBName());
        
        // 设置订单图片
//        recycleOrder.setOrderNodeImg(userOrderDTO.getImgUrl());
        
        // 设置交付地址（使用用户订单的位置信息）
//        recycleOrder.setDeliveryAddress(userOrderDTO.getLocation());
        if (isNeedSettle) {
            // 设置订单状态为已完成（根据原代码逻辑）
            recycleOrder.setStatus(RecycleOrderStatusEnum.COMPLETED.getCode());
            // 结算时间
            recycleOrder.setSettlementTime(new Date());
        }else {
            recycleOrder.setStatus(RecycleOrderStatusEnum.PROCESSING.getCode());
        }

        // 设置经办人信息
        if (userOrderRequest.getProcessorId() != null && !userOrderRequest.getProcessorId().isEmpty()) {
            Processor processor = processorService.getById(userOrderRequest.getProcessorId());
            if (processor != null) {
                recycleOrder.setProcessorId(processor.getId());
                recycleOrder.setProcessor(processor.getName());
                recycleOrder.setProcessorPhone(processor.getPhone());
            }
        }
    }
    
    /**
     * 将UserOrder转换为UserOrderDTO
     */
    private UserOrderDTO convertToDTO(UserOrder userOrder) {
        UserOrderDTO dto = new UserOrderDTO();
        // 复制UserOrder的所有属性
        BeanUtils.copyProperties(userOrder, dto);
//        dto.setImgUrl(userOrder.getImgUrl());
//        dto.setLocation(userOrder.getLocation());
        // 注意：paymentAccount 等字段需要从其他地方获取，这里暂时保持为null
        // 如果UserOrder有这些字段，可以在这里设置
        
        return dto;
    }
}
