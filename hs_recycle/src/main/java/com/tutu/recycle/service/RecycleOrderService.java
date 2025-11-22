package com.tutu.recycle.service;



import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.DirectedGraph;
import com.tutu.common.enums.BaseEnum;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.dto.RecycleOrderTracePath;
import com.tutu.recycle.dto.RecycleOrderTraceResponse;
import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleContractItem;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.order.RecycleOrderItem;
import com.tutu.recycle.entity.order.RecycleOrderTrace;
import com.tutu.recycle.enums.*;
import com.tutu.recycle.mapper.RecycleOrderMapper;
import com.tutu.recycle.request.DeliverOrderRequest;
import com.tutu.recycle.request.ProcessingOrderSubmitRequest;
import com.tutu.recycle.request.RecycleOrderQueryRequest;
import com.tutu.recycle.request.recycle_order.SourceCode;
import com.tutu.recycle.schema.RecycleOrderInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.server.BaseRecycleOrderServer;
import com.tutu.recycle.utils.CodeUtil;
import com.tutu.recycle.utils.RecycleOrderNoGenerator;
import com.tutu.user.entity.Account;
import com.tutu.user.entity.Processor;
import com.tutu.user.enums.UserUseTypeEnum;
import com.tutu.user.service.AccountService;
import com.tutu.user.service.ProcessorService;
import org.springframework.web.multipart.MultipartFile;
import com.tutu.system.vo.FileUploadVO;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import com.tutu.system.service.MessageService;
import com.tutu.system.service.SysFileService;
import com.tutu.system.utils.MessageUtil;

import jakarta.annotation.Resource;


import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class RecycleOrderService extends ServiceImpl<RecycleOrderMapper, RecycleOrder> {
//    @Resource
//    private WxMaService wxMaService;
    @Autowired
    private SysFileService sysFileService;
    @Resource
    private AccountService accountService;
    @Resource
    private RecycleOrderItemService recycleOrderItemService;
    @Resource
    private RecycleContractService recycleContractService;
    @Resource
    private RecycleContractItemService recycleContractItemService;
    @Resource
    private MessageService messageService;
    @Resource
    private RecycleOrderTraceService recycleOrderTraceService;
    @Resource
    private ProcessorService processorService;
    @Resource
    private CodeUtil codeUtil;
    @Resource
    private BaseRecycleOrderServer baseRecycleOrderServer;
    
    @Resource
    @Lazy
    private UserOrderService userOrderService;
    
    // 使用ConcurrentHashMap存储订单ID对应的锁，防止并发抢单
    private final ConcurrentHashMap<String, ReentrantLock> orderLocks = new ConcurrentHashMap<>();
    /**
     * 根据用户订单和阶段创建对应类型的回收订单
     *
     * @param userOrderRequest 用户订单对象
     * @param order
     * @param stage     订单阶段枚举
     */
    @Transactional(rollbackFor = Exception.class)
    public void createRecycleOrderFromUserOrderByStage(UserOrderDTO userOrderRequest,
                                                       UserOrder order,
                                                       UserOrderStageEnum stage,
                                                       Boolean isNeedSettle) {
        if (userOrderRequest == null) {
            throw new ServiceException("用户订单不能为空");
        }
        if (stage == null) {
            throw new ServiceException("订单阶段不能为空");
        }
        // 根据阶段获取对应的回收订单类型
        RecycleOrderTypeEnum orderType = getRecycleOrderTypeByStage(stage);
        if (orderType == null) {
            throw new ServiceException("阶段 " + stage.getDescription() + " 没有对应的回收订单类型");
        }
        // 使用代理对象调用，确保事务生效
        ((RecycleOrderService) AopContext.currentProxy()).createRecycleOrderFromUserOrderByType(userOrderRequest,order, orderType,isNeedSettle);
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
    @Transactional(rollbackFor = Exception.class)
    public void createRecycleOrderFromUserOrderByType(UserOrderDTO userOrderRequest, UserOrder order, RecycleOrderTypeEnum orderType, Boolean isNeedSettle) {
        if (userOrderRequest == null) {
            throw new ServiceException("用户订单不能为空");
        }
        if (orderType == null) {
            throw new ServiceException("回收订单类型不能为空");
        }
        // 使用BaseRecycleOrderServer创建回收订单
        RecycleOrderInfo recycleOrder = baseRecycleOrderServer.createRecycleOrderFromUserOrderByType(userOrderRequest,order, orderType,isNeedSettle);
        // 查询对应阶段是否有已存在订单
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getParentId, recycleOrder.getParentId());
        RecycleOrderInfo existingOrder = Optional.ofNullable(getByParentId(recycleOrder.getParentId(),orderType.getCode())).orElse(new RecycleOrderInfo());
        BeanUtil.copyProperties(recycleOrder, existingOrder, CopyOptions.create().setIgnoreNullValue(true));
        // 保存回收订单
        createOrUpdate(existingOrder);
    }
    
    /**
     * 根据用户订单阶段获取对应的回收订单类型
     * @param stage 用户订单阶段
     * @return 对应的回收订单类型，如果没有对应关系则返回null
     */
    private RecycleOrderTypeEnum getRecycleOrderTypeByStage(UserOrderStageEnum stage) {
        if (stage == null) {
            return null;
        }
        return switch (stage) {
            case PURCHASE -> RecycleOrderTypeEnum.PURCHASE;
            case TRANSPORT -> RecycleOrderTypeEnum.TRANSPORT;
            case PROCESSING -> RecycleOrderTypeEnum.PROCESSING;
            case WAREHOUSING -> RecycleOrderTypeEnum.STORAGE; // 入库阶段对应仓储订单
            case PENDING_SETTLEMENT -> null;
            case COMPLETED -> null; // 完成阶段不需要创建订单
        };
    }
    
    /**
     * 根据父订单ID查询回收订单（包含订单明细）
     * 同一个parentId下只会有一个订单，若查询到多个则抛出异常
     * @param parentId 父订单ID
     * @return 回收订单信息（包含订单明细和追溯信息）
     */
    public RecycleOrderInfo getByParentId(String parentId,String orderType) {
        if (StrUtil.isBlank(parentId)) {
            throw new ServiceException("父订单ID不能为空");
        }
        
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getParentId, parentId);
        wrapper.eq(RecycleOrder::getType, orderType);
        
        List<RecycleOrder> orders = list(wrapper);
        
        if (orders == null || orders.isEmpty()) {
            return null;
        }
        
        if (orders.size() > 1) {
            throw new ServiceException("查询到多个匹配订单，请联系管理员修正数据");
        }
        
        RecycleOrder order = orders.get(0);
        
        // 构建完整的订单信息（包含明细）
        RecycleOrderInfo recycleOrderInfo = new RecycleOrderInfo();
        BeanUtil.copyProperties(order, recycleOrderInfo);
        
        // 查询订单明细
        recycleOrderInfo.setItems(recycleOrderItemService.list(
            new LambdaQueryWrapper<RecycleOrderItem>().eq(RecycleOrderItem::getRecycleOrderId, order.getId())
        ));
        
        // 查询订单追溯信息
        List<RecycleOrderTrace> traceList = recycleOrderTraceService.getByOrderId(order.getId());
        recycleOrderInfo.setSourceCodes(traceList.stream().map(trace -> {
            SourceCode sourceCode = new SourceCode();
            sourceCode.setChangeReason(trace.getChangeReason());
            sourceCode.setIdentifyCode(trace.getParentCode());
            sourceCode.setOrderId(trace.getParentOrderId());
            sourceCode.setOrderNo(trace.getParentOrderNo());
            sourceCode.setOrderType(trace.getParentOrderType());
            return sourceCode;
        }).collect(Collectors.toList()));
        
        return recycleOrderInfo;
    }
    
    /**
     * 根据父订单ID查询所有子回收订单（包含订单明细）
     * @param parentId 父订单ID
     * @return 回收订单信息列表（包含订单明细和追溯信息）
     */
    public List<RecycleOrderInfo> getAllByParentId(String parentId) {
        if (StrUtil.isBlank(parentId)) {
            throw new ServiceException("父订单ID不能为空");
        }
        
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getParentId, parentId);
        
        List<RecycleOrder> orders = list(wrapper);
        
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 收集所有经办人ID（processor字段可能存储的是ID）
        List<String> processorIds = orders.stream()
                .map(RecycleOrder::getProcessor)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询经办人信息
        Map<String, String> processorNameMap = new HashMap<>();
        if (!processorIds.isEmpty()) {
            List<Processor> processors = processorService.listByIds(processorIds);
            processorNameMap = processors.stream()
                    .collect(Collectors.toMap(
                            Processor::getId,
                            Processor::getName,
                            (existing, replacement) -> existing
                    ));
        }
        
        // 将每个订单转换为 RecycleOrderInfo
        final Map<String, String> finalProcessorNameMap = processorNameMap;
        return orders.stream().map(order -> {
            RecycleOrderInfo recycleOrderInfo = new RecycleOrderInfo();
            BeanUtil.copyProperties(order, recycleOrderInfo);
            
            // 填充经办人名称
            if (StrUtil.isNotBlank(order.getProcessor())) {
                String processorName = finalProcessorNameMap.get(order.getProcessor());
                if (processorName != null) {
                    recycleOrderInfo.setProcessorName(processorName);
                } else {
                    // 如果processor字段存储的是名称而不是ID，则直接使用
                    recycleOrderInfo.setProcessorName(order.getProcessor());
                }
            }
            
            // 查询订单明细
            recycleOrderInfo.setItems(recycleOrderItemService.list(
                new LambdaQueryWrapper<RecycleOrderItem>().eq(RecycleOrderItem::getRecycleOrderId, order.getId())
            ));
            
            // 查询订单追溯信息
            List<RecycleOrderTrace> traceList = recycleOrderTraceService.getByOrderId(order.getId());
            recycleOrderInfo.setSourceCodes(traceList.stream().map(trace -> {
                SourceCode sourceCode = new SourceCode();
                sourceCode.setChangeReason(trace.getChangeReason());
                sourceCode.setIdentifyCode(trace.getParentCode());
                sourceCode.setOrderId(trace.getParentOrderId());
                sourceCode.setOrderNo(trace.getParentOrderNo());
                sourceCode.setOrderType(trace.getParentOrderType());
                return sourceCode;
            }).collect(Collectors.toList()));
            
            return recycleOrderInfo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 创建微信订单
     * @param request 订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void createWxOrder(RecycleOrder request) {
        RecycleOrder order = new RecycleOrder();
        // 根据合同订单查询合同
        RecycleContract contract = recycleContractService.getById(request.getContractId());
        if (contract == null) {
            throw new ServiceException("对应合同不存在");
        }
        // 合同甲方
        order.setPartyA(contract.getPartyA());
        // 合同乙方
        order.setPartyB(contract.getPartyB());
        // 合同甲方名称
        order.setPartyAName(contract.getPartyAName());
        // 合同乙方名称
        order.setPartyBName(contract.getPartyBName());
        // 合同合作方
        order.setContractPartner(contract.getPartner());
        order.setContractPartnerName(contract.getPartnerName());
        // 合同订单
        order.setContractId(contract.getId());
        order.setContractNo(contract.getNo());
        order.setContractName(contract.getName());
        // 订单状态
        order.setStatus(RecycleOrderStatusEnum.PROCESSING.getCode());
        // 订单类型
        order.setType(RecycleOrderTypeEnum.PURCHASE.getCode());
        // 订单图片
        order.setOrderNodeImg(request.getOrderNodeImg());
        // 地址
        order.setDeliveryAddress(request.getDeliveryAddress());
        // 经办人
        order.setProcessor(request.getProcessor());
        // 经办人电话
        order.setProcessorPhone(request.getProcessorPhone());
        // 生成订单编号
        order.setNo(IdUtil.simpleUUID());
        // 保存订单
        save(order);
        // 查询合同明细，复制给订单明细
        List<RecycleContractItem> contractItems = recycleContractItemService.list(new LambdaQueryWrapper<RecycleContractItem>().eq(RecycleContractItem::getRecycleContractId, contract.getId()));
        for (RecycleContractItem item : contractItems) {
            RecycleOrderItem orderItem = new RecycleOrderItem();
            BeanUtil.copyProperties(item, orderItem);
            orderItem.setRecycleOrderId(order.getId());
            orderItem.setDirection("in");
            recycleOrderItemService.save(orderItem);
        }
    }

    /**
     * 创建或更新回收订单
     * @param request 回收订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public RecycleOrder createOrUpdate(RecycleOrderInfo request){
        String id = request.getId();
        // 保存订单
        RecycleOrder recycleOrder = new RecycleOrder();
        BeanUtil.copyProperties(request, recycleOrder);
        if (StrUtil.isBlank(id)) {
            // 生成订单编号
            String type = recycleOrder.getType();
            recycleOrder.setNo(RecycleOrderNoGenerator.generate(BaseEnum.getByCode(RecycleOrderTypeEnum.class, type)));
            save(recycleOrder);
        }else{
            updateById(recycleOrder);
        }
        // 先查找对应订单的订单项
        List<RecycleOrderItem> orderItems = recycleOrderItemService.list(new LambdaQueryWrapper<RecycleOrderItem>().eq(RecycleOrderItem::getRecycleOrderId, recycleOrder.getId()));
        if (CollUtil.isNotEmpty(orderItems)) {
            // 和传递过来的订单项进行对比，删除不存在的订单项
            List<String> itemIds = request.getItems().stream().map(RecycleOrderItem::getId).toList();
            orderItems.stream().filter(item -> !itemIds.contains(item.getId()))
                    .forEach(item -> recycleOrderItemService.removeById(item.getId()));
        }
        // 保存订单明细
        List<RecycleOrderItem> items = Optional.ofNullable(request.getItems()).orElse(new ArrayList<>());
        for (RecycleOrderItem item : items) {
            item.setRecycleOrderId(recycleOrder.getId());
            if (StrUtil.isNotBlank(item.getId())) {
                recycleOrderItemService.updateById(item);
            }else {
                recycleOrderItemService.save(item);
            }
        }
        // 保存轨迹
        List<SourceCode> sourceCodes = request.getSourceCodes();
        if (CollUtil.isEmpty(sourceCodes)) {
            sourceCodes = new ArrayList<>();
        }
        // 移除原有的轨迹
        recycleOrderTraceService.removeByOrderId(recycleOrder.getId());
        // 说明有父轨迹
        for (SourceCode sourceCode : sourceCodes) {
            RecycleOrderTrace trace = new RecycleOrderTrace();
            trace.setOrderId(recycleOrder.getId());
            trace.setParentCode(sourceCode.getIdentifyCode());
            trace.setChangeReason(sourceCode.getChangeReason());
            trace.setParentOrderId(sourceCode.getOrderId());
            trace.setParentOrderNo(sourceCode.getOrderNo());
            trace.setParentOrderType(sourceCode.getOrderType());
            recycleOrderTraceService.save(trace);
        }
        return recycleOrder;
    }

    /**
     * 获取订单二维码并上传到服务器
     * @param orderId 订单ID
     * @return 二维码图片URL
     */
//    public String createOrderQrcode(String orderId){
//        try {
//            File wxaCode = wxMaService.getQrcodeService().createWxaCode("/pages/home/home?orderId=" + orderId);
//
//            // 将File转换为字节数组
//            try (FileInputStream fis = new FileInputStream(wxaCode);
//                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
//                byte[] buffer = new byte[1024];
//                int len;
//                while ((len = fis.read(buffer)) != -1) {
//                    bos.write(buffer, 0, len);
//                }
//                byte[] bytes = bos.toByteArray();
//
//                // 创建自定义MultipartFile实现
//                MultipartFile multipartFile = new CustomMultipartFile(
//                    "file",
//                    "order_qrcode.png",
//                    "image/png",
//                    bytes
//                );
//
//                // 上传文件到服务器
//                FileUploadVO fileUploadVO = sysFileService.uploadFile(multipartFile, null, null);
//
//                return fileUploadVO.getFileUrl();
//            }
//        } catch (WxErrorException | IOException e) {
//            throw new RuntimeException("生成订单二维码失败", e);
//        }
//    }
    
    /**
     * 自定义MultipartFile实现类，避免依赖外部库
     */
    private static class CustomMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;
        
        public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }
        
        @Override
        public String getContentType() {
            return contentType;
        }
        
        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }
        
        @Override
        public long getSize() {
            return content.length;
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }
        
        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }

    /**
     * 给订单分配专人
     * @param orderId 订单 ID
     * @param processor 处理人 ID
     */
    public void assignProcessor(String orderId, String processor) {
        Account account = accountService.getById(processor);
        if (account == null) {
            throw new RuntimeException("处理人不存在");
        }
        RecycleOrder recycleOrder = getById(orderId);
        if (recycleOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!account.getUseType().equals(UserUseTypeEnum.TRANSPORT.getCode())) {
           throw new RuntimeException("处理人不是司机");
        } 
        recycleOrder.setProcessor(processor);
        recycleOrder.setProcessorPhone(account.getPhone());
        updateById(recycleOrder);
    }

    /**
     * 获取专人负责的订单
     * @param recycleOrder 订单实体
     * @return 订单列表
     */
    public List<RecycleOrder> getOrdersByProcessor(RecycleOrder recycleOrder) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getProcessor, recycleOrder.getProcessor());
        if (StrUtil.isNotBlank(recycleOrder.getStatus()) && !recycleOrder.getStatus().equals("all")) {
            wrapper.eq(RecycleOrder::getStatus, recycleOrder.getStatus());
        }
        return list(wrapper);
    }

    /**
     * 获取订单信息
     * @param id 订单ID
     * @return 订单信息
     */
    public RecycleOrderInfo getOrderInfo(String id) {
        RecycleOrderInfo recycleOrderInfo = new RecycleOrderInfo();
        BeanUtil.copyProperties(getById(id), recycleOrderInfo);
        recycleOrderInfo.setItems(recycleOrderItemService.list(new LambdaQueryWrapper<RecycleOrderItem>().eq(RecycleOrderItem::getRecycleOrderId, id)));
        // 轨迹能力数据
        List<RecycleOrderTrace> traceList =  recycleOrderTraceService.getByOrderId(id);
        recycleOrderInfo.setSourceCodes(traceList.stream().map(trace ->{
            SourceCode sourceCode = new SourceCode();
            sourceCode.setChangeReason(trace.getChangeReason());
            sourceCode.setIdentifyCode(trace.getParentCode());
            sourceCode.setOrderId(trace.getParentOrderId());
            sourceCode.setOrderNo(trace.getParentOrderNo());
            sourceCode.setOrderType(trace.getParentOrderType());
            return sourceCode;
        }).collect(Collectors.toList()));
        
        return recycleOrderInfo;
    }

    /**
     * 获取合作方的订单列表
     * @param partnerId 合作方ID
     * @param status 订单状态，可为空表示查询所有状态
     * @return 订单列表
     */
    public List<RecycleOrder> getPartnerOrderList(String partnerId, String status) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, partnerId)
                .orderByDesc(RecycleOrder::getCreateTime);
        
        // 如果状态不为空且不是"all"，则添加状态过滤条件
        if (StrUtil.isNotBlank(status) && !"all".equals(status)) {
            wrapper.eq(RecycleOrder::getStatus, status);
        }
        
        return list(wrapper);
    }

    /**
     * 获取订单列表
     * @param order 订单实体
     * @return 订单列表
     */
    public List<RecycleOrder> getOrderList(RecycleOrder order) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>();
        if (StrUtil.isNotBlank(order.getType())) {
            wrapper.eq(RecycleOrder::getType, order.getType());
        }
        if (StrUtil.isNotBlank(order.getStatus())) {
            wrapper.eq(RecycleOrder::getStatus, order.getStatus());
        }
        if (StrUtil.isNotBlank(order.getContractPartner())) {
            wrapper.eq(RecycleOrder::getContractPartner, order.getContractPartner());
        }
        if (StrUtil.isNotBlank(order.getIdentifyCode())) {
            wrapper.eq(RecycleOrder::getIdentifyCode, order.getIdentifyCode());
        }
        return list(wrapper);
    }

    /**
     * 根据订单识别号查询订单（带权限控制）
     * @param identifyCode 订单识别号
     * @param userId 用户ID
     * @return 订单信息
     */
    public RecycleOrder getOrderByIdentifyCode(String identifyCode, String userId) {
        if (StrUtil.isBlank(identifyCode)) {
            throw new ServiceException("订单识别号不能为空");
        }
        
        // 获取用户信息
        Account account = accountService.getById(userId);
        if (account == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 根据用户身份确定允许查询的订单类型
        String userUseType = account.getUseType();
        String allowedOrderType = null;
        
        if (UserUseTypeEnum.TRANSPORT.getCode().equals(userUseType)) {
            allowedOrderType = RecycleOrderTypeEnum.TRANSPORT.getCode();
        } else if (UserUseTypeEnum.SORTING.getCode().equals(userUseType)) {
            allowedOrderType = RecycleOrderTypeEnum.PROCESSING.getCode();
        } else {
            // 其他身份用户不允许使用此接口
            throw new ServiceException("您的身份无权限使用此接口，请联系管理员");
        }
        
        // 根据订单识别号和订单类型查询订单
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getIdentifyCode, identifyCode)
                .eq(RecycleOrder::getType, allowedOrderType);
        
        List<RecycleOrder> orders = list(wrapper);
        
        if (orders.isEmpty()) {
            throw new ServiceException("未找到对应的订单");
        }
        
        if (orders.size() > 1) {
            throw new ServiceException("查询到多个相同类型的订单，请联系管理员处理");
        }
        
        RecycleOrder order = orders.get(0);
        
        return order;
    }

    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 订单详情信息
     */
    public RecycleOrder getOrderByIdWithPermission(String orderId, String userId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        // 根据订单ID查询订单
        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        
        return order;
    }

    /**
     * 运输订单提交
     * @param orderId 订单ID
     * @param orderNodeImg 订单图片
     * @param deliveryAddress 交付地址
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitTransportOrder(String orderId, String orderNodeImg, String deliveryAddress, String userId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        // 获取用户信息
        Account account = accountService.getById(userId);
        if (account == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 验证用户身份
        if (!UserUseTypeEnum.TRANSPORT.getCode().equals(account.getUseType())) {
            throw new ServiceException("只有运输用户才能提交运输订单");
        }
        
        // 根据订单ID查询订单
        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        
        // 验证订单类型
        if (!RecycleOrderTypeEnum.TRANSPORT.getCode().equals(order.getType())) {
            throw new ServiceException("只能提交运输订单");
        }
        
        // 验证订单状态，已上传的订单不能重复提交
        if (RecycleOrderStatusEnum.UPLOADED.getCode().equals(order.getStatus())) {
            throw new ServiceException("订单已上传，无法重复提交");
        }
        
        // 更新订单信息
        order.setOrderNodeImg(orderNodeImg);
        order.setDeliveryAddress(deliveryAddress);
        order.setUploadTime(new Date());
        order.setStatus(RecycleOrderStatusEnum.UPLOADED.getCode());
        
        // 保存订单
        updateById(order);
    }

    /**
     * 加工订单提交
     * @param orderId 订单ID
     * @param orderNodeImg 订单图片
     * @param items 订单明细列表
     * @param userId 用户ID
     */
//    @Transactional(rollbackFor = Exception.class)
//    public void submitProcessingOrder(String orderId, String orderNodeImg, List<ProcessingOrderSubmitRequest.OrderItemUpdateRequest> items, String userId) {
//        if (StrUtil.isBlank(orderId)) {
//            throw new ServiceException("订单ID不能为空");
//        }
//
//        // 获取用户信息
//        Account account = accountService.getById(userId);
//        if (account == null) {
//            throw new ServiceException("用户不存在");
//        }
//
//        // 验证用户身份
//        if (!UserUseTypeEnum.SORTING.getCode().equals(account.getUseType())) {
//            throw new ServiceException("只有分拣用户才能提交加工订单");
//        }
//
//        // 根据订单ID查询订单
//        RecycleOrder order = getById(orderId);
//        if (order == null) {
//            throw new ServiceException("订单不存在");
//        }
//
//        // 验证订单类型
//        if (!RecycleOrderTypeEnum.PROCESSING.getCode().equals(order.getType())) {
//            throw new ServiceException("只能提交加工订单");
//        }
//
//        // 验证订单状态，已上传的订单不能重复提交
//        if (RecycleOrderStatusEnum.UPLOADED.getCode().equals(order.getStatus())) {
//            throw new ServiceException("订单已上传，无法重复提交");
//        }
//
//        // 更新订单信息
//        order.setOrderNodeImg(orderNodeImg);
//        order.setUploadTime(new Date());
//        order.setStatus(RecycleOrderStatusEnum.UPLOADED.getCode());
//
//        // 保存订单
//        updateById(order);
//
//        // 添加订单明细
//        if (items != null && !items.isEmpty()) {
//            for (ProcessingOrderSubmitRequest.OrderItemUpdateRequest itemRequest : items) {
//                // 创建新的订单明细
//                RecycleOrderItem orderItem = new RecycleOrderItem();
//                orderItem.setRecycleOrderId(orderId);
//                orderItem.setGoodNo(itemRequest.getGoodNo());
//                orderItem.setGoodType(itemRequest.getGoodType());
//                orderItem.setGoodName(itemRequest.getGoodName());
//                orderItem.setGoodModel(itemRequest.getGoodModel());
//                orderItem.setGoodCount(itemRequest.getGoodCount());
//                orderItem.setGoodWeight(itemRequest.getGoodWeight());
//                orderItem.setGoodPrice(itemRequest.getGoodPrice());
//                orderItem.setGoodTotalPrice(itemRequest.getGoodTotalPrice());
//                orderItem.setGoodRemark(itemRequest.getGoodRemark());
//
//                // 保存新的明细
//                recycleOrderItemService.save(orderItem);
//            }
//        }
//    }

    /**
     * 订单结算
     * @param orderId 订单ID
     * @param settlementPdfUrl 结算单PDF URL地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void settlementOrder(String orderId, String settlementPdfUrl) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        if (StrUtil.isBlank(settlementPdfUrl)) {
            throw new ServiceException("结算单PDF URL不能为空");
        }
        
        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        
        // 更新结算单PDF URL
        order.setSettlementPdfUrl(settlementPdfUrl);
        // 更新订单状态为已结算
        order.setStatus(RecycleOrderStatusEnum.COMPLETED.getCode());
        // 更新结算时间
        order.setSettlementTime(new Date());
        // 给特定的用户发送已结算消息
        String userId = order.getContractPartner();
        messageService.sendMessage(MessageUtil.buildOrderSettleMessage(userId, orderId));
        updateById(order);
    }

    /**
     * 订单申请
     * @param orderId 订单ID
     * @param applicationPdfUrl 申请单PDF URL地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void applicationOrder(String orderId, String applicationPdfUrl) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        if (StrUtil.isBlank(applicationPdfUrl)) {
            throw new ServiceException("申请单PDF URL不能为空");
        }
        
        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        
        // 更新申请单PDF URL
        order.setApplicationPdfUrl(applicationPdfUrl);
        
        updateById(order);
    }

    /**
     * 根据订单编号查询订单
     * @param orderNo 订单编号
     * @return 订单信息
     */
    public RecycleOrder getByOrderNo(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return null;
        }
        
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getNo, orderNo);
        return getOne(wrapper);
    }

    /**
     * 分页查询回收订单（支持多条件查询）
     * @param page 分页对象
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    public Page<RecycleOrder> getRecycleOrdersByPage(
            Page<RecycleOrder> page,
            RecycleOrderQueryRequest queryRequest) {
        
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>();
        
        // 添加查询条件
        if (StrUtil.isNotBlank(queryRequest.getType())) {
            wrapper.eq(RecycleOrder::getType, queryRequest.getType());
        }
        if (StrUtil.isNotBlank(queryRequest.getStatus())) {
            wrapper.eq(RecycleOrder::getStatus, queryRequest.getStatus());
        }
        if (StrUtil.isNotBlank(queryRequest.getIdentifyCode())) {
            wrapper.like(RecycleOrder::getIdentifyCode, queryRequest.getIdentifyCode());
        }
        if (StrUtil.isNotBlank(queryRequest.getContractPartner())) {
            wrapper.like(RecycleOrder::getContractPartner, queryRequest.getContractPartner());
        }
        if (StrUtil.isNotBlank(queryRequest.getFlowDirection())) {
            wrapper.eq(RecycleOrder::getFlowDirection, queryRequest.getFlowDirection());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(RecycleOrder::getCreateTime);
        
        return page(page, wrapper);
    }

    /**
     * 根据订单识别码查询订单信息列表
     * @param identifyCode 订单识别码
     * @return 订单信息列表
     */
    public List<RecycleOrder> getListByIdentifyCode(String identifyCode) {
        if (StrUtil.isBlank(identifyCode)) {
            return null;
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>();
        wrapper.eq(RecycleOrder::getIdentifyCode, identifyCode);
        return list(wrapper);
    }

    /**
     * 分页查询仓储入库订单
     * @param page 分页对象
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    public Page<RecycleOrder> getStorageInboundOrdersByPage(
            Page<RecycleOrder> page,
            RecycleOrderQueryRequest queryRequest) {
        
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>();
        
        // 固定查询条件：订单类型为仓储订单，流转方向为入库
        wrapper.eq(RecycleOrder::getType, RecycleOrderTypeEnum.STORAGE.getCode())
               .eq(RecycleOrder::getFlowDirection, RecycleFlowDirectionEnum.IN.getValue());
        
        // 添加其他查询条件
        if (StrUtil.isNotBlank(queryRequest.getStatus())) {
            wrapper.eq(RecycleOrder::getStatus, queryRequest.getStatus());
        }
        if (StrUtil.isNotBlank(queryRequest.getIdentifyCode())) {
            wrapper.like(RecycleOrder::getIdentifyCode, queryRequest.getIdentifyCode());
        }
        if (StrUtil.isNotBlank(queryRequest.getContractPartner())) {
            wrapper.like(RecycleOrder::getContractPartner, queryRequest.getContractPartner());
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(RecycleOrder::getCreateTime);
        
        return page(page, wrapper);
    }

    /**
     * 获取订单追溯链路
     * @param orderId 订单ID
     * @return 订单追溯链路
     */
    public RecycleOrderTraceResponse getRecycleOrderTrace(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            return null;
        }
        RecycleOrderTraceResponse response = new RecycleOrderTraceResponse();
        DirectedGraph<RecycleOrderTracePath> result = DirectedGraph.init();
        // 先往上查询链路
        recursiveQueryFormTrace(orderId, result);
        // 往下查询当前节点能到达的地方
        recursiveQueryToTrace(orderId, result);
        Map<String, List<RecycleOrderTracePath>> graph = new HashMap<>();
        result.getGraph().forEach((key, value) -> {
            graph.put(key.getOrderId(), value);
        });
        response.setGraph(graph);
        response.setPaths(result.layeredTopologicalSort());
        // 遍历链路拿到所有的订单id
        Set<String> orderIds = response.getPaths().stream().flatMap(List::stream).map(RecycleOrderTracePath::getOrderId).collect(Collectors.toSet());
        if (!orderIds.isEmpty()) {
            // 根据id查询对应的订单
            List<RecycleOrder> allOrders = listByIds(orderIds);
            Map<String, RecycleOrder> orderMap = allOrders.stream().collect(Collectors.toMap(RecycleOrder::getId, Function.identity()));
            // 填充链路中的订单信息
            response.getPaths().forEach(paths -> paths.forEach(path -> {
                path.setContext(RecycleOrderTracePath.Order.convert(orderMap.get(path.getOrderId())));
            }));
            response.getGraph().forEach((key, value) -> {
                value.forEach(path -> {
                    path.setContext(RecycleOrderTracePath.Order.convert(orderMap.get(path.getOrderId())));
                });
            });
            response.setAllOrders(allOrders);
        }
        return response;
    }
    /**
     * 递归查询订单追溯链路
     * @param orderId 订单ID
     * @param result 结果映射
     */
    private void recursiveQueryFormTrace(String orderId, DirectedGraph<RecycleOrderTracePath> result) {
        // 拿到这个订单的上级
        List<RecycleOrderTrace> recycleOrderTraces = recycleOrderTraceService.getByOrderId(orderId);
        // 递归查询链路
        for (RecycleOrderTrace trace : recycleOrderTraces) {
            if (trace.getParentCode() == null) {
                continue;
            }
            String parentOrderId = trace.getParentOrderId();
            result.addEdge(
                RecycleOrderTracePath.builder().orderId(parentOrderId).build(), 
                RecycleOrderTracePath.builder().orderId(orderId).changeReason(trace.getChangeReason()).build()
                );
                recursiveQueryFormTrace(parentOrderId, result);
        }
    }
    /**
     * 递归查询订单追溯链路
     * @param orderId 订单ID
     * @param result 结果映射
     */
    private void recursiveQueryToTrace(String orderId, DirectedGraph<RecycleOrderTracePath> result) {
        // 拿到这个订单的下级
        List<RecycleOrderTrace> recycleOrderTraces = recycleOrderTraceService.getChildrenByOrderId(orderId);
        // 递归查询链路
        for (RecycleOrderTrace trace : recycleOrderTraces) {
            result.addEdge(
                RecycleOrderTracePath.builder().orderId(orderId).changeReason(trace.getChangeReason()).build(), 
                RecycleOrderTracePath.builder().orderId(trace.getOrderId()).build()
            );
            recursiveQueryToTrace(trace.getOrderId(), result);
        }
    }

    // ==================== 运输中心相关方法 ====================

    /**
     * 根据运输状态查询运输订单列表
     * @param transportStatus 运输状态
     * @return 运输订单列表
     */
    public List<RecycleOrder> getTransportOrdersByStatus(String transportStatus, String processorId) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getType, RecycleOrderTypeEnum.TRANSPORT.getCode());
        if (StrUtil.isNotBlank(transportStatus)) {
            wrapper.eq(RecycleOrder::getTransportStatus, transportStatus);
        }
        if (StrUtil.isNotBlank(processorId)) {
            wrapper.eq(RecycleOrder::getProcessorId, processorId);
        }
        wrapper.orderByDesc(RecycleOrder::getCreateTime);
        return list(wrapper);
    }

    /**
     * 获取订单锁
     * @param orderId 订单ID
     * @return 对应的锁对象
     */
    private ReentrantLock getOrderLock(String orderId) {
        return orderLocks.computeIfAbsent(orderId, k -> new ReentrantLock());
    }
    
    /**
     * 尝试获取订单锁
     * @param orderId 订单ID
     * @return 是否成功获取锁
     */
    private boolean tryLock(String orderId) {
        ReentrantLock lock = getOrderLock(orderId);
        return lock.tryLock();
    }
    
    /**
     * 释放订单锁
     * @param orderId 订单ID
     */
    private void releaseLock(String orderId) {
        ReentrantLock lock = orderLocks.get(orderId);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            // 如果锁没有被其他线程持有，则从Map中移除，防止内存泄漏
            if (!lock.isLocked()) {
                orderLocks.remove(orderId);
            }
        }
    }
    
    /**
     * 抢单操作
     * 根据主订单ID（UserOrder）创建运输类型的子订单（RecycleOrder）
     * @param userOrderId 主订单ID（UserOrder的ID）
     * @param processorId 经办人ID
     * @return 是否抢单成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean grabOrder(String userOrderId, String processorId) {
        if (StrUtil.isBlank(userOrderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        if (StrUtil.isBlank(processorId)) {
            throw new ServiceException("经办人ID不能为空");
        }
        // 尝试获取订单锁，防止并发抢单
        if (!tryLock(userOrderId)) {
            throw new ServiceException("当前订单正在被处理，请稍后重试");
        }
        try {
            // 查询主订单（UserOrder）
            UserOrder userOrder = userOrderService.getById(userOrderId);
            if (userOrder == null) {
                throw new ServiceException("主订单不存在");
            }
            // 检查主订单阶段是否为运输阶段
            if (!UserOrderStageEnum.TRANSPORT.getCode().equals(userOrder.getStage())) {
                throw new ServiceException("只有运输阶段的订单才能抢单");
            }
            // 检查是否已经存在运输类型的子订单
            List<RecycleOrderInfo> existingOrders = getAllByParentId(userOrderId);
            boolean hasTransportOrder = existingOrders.stream()
                    .anyMatch(order -> RecycleOrderTypeEnum.TRANSPORT.getCode().equals(order.getType()));
            if (hasTransportOrder) {
                throw new ServiceException("该订单已经被抢单，无法重复抢单");
            }
            // 获取经办人信息
            Processor processor = processorService.getById(processorId);
            if (processor == null) {
                throw new ServiceException("经办人不存在");
            }
            // 查询对应的采购订单
            RecycleOrderInfo purchaseOrder = existingOrders.stream()
                    .filter(order -> RecycleOrderTypeEnum.PURCHASE.getCode().equals(order.getType()))
                    .findFirst()
                    .orElseThrow(() -> new ServiceException("未找到对应的采购订单"));

            // 将UserOrder转换为UserOrderDTO
            UserOrderDTO userOrderDTO = new UserOrderDTO();
            // 设置经办人信息
            userOrderDTO.setProcessorId(processorId);
            userOrderDTO.setProcessorName(processor.getName());
            userOrderDTO.setPickupAddress(purchaseOrder.getPickupAddress());
            userOrderDTO.setDeliveryAddress(purchaseOrder.getDeliveryAddress());
            // 设置运输状态为已抢单
            userOrderDTO.setTransportStatus(TransportStatusEnum.GRABBED.getCode());
            // 设置开始时间（抢单时间）
            userOrderDTO.setStartTime(new Date());
            // 创建运输类型的回收订单
            // 使用代理对象调用，确保事务生效
            ((RecycleOrderService) AopContext.currentProxy()).createRecycleOrderFromUserOrderByType(
                    userOrderDTO, userOrder, RecycleOrderTypeEnum.TRANSPORT, false);
            return true;
        } finally {
            // 释放锁
            releaseLock(userOrderId);
        }
    }

    /**
     * 分拣接单
     * 根据主订单ID创建加工类型的子订单
     * @param userOrderId 主订单ID
     * @param processorId 经办人ID
     * @return 是否接单成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean grabSortingOrder(String userOrderId, String processorId) {
        if (StrUtil.isBlank(userOrderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        if (StrUtil.isBlank(processorId)) {
            throw new ServiceException("经办人ID不能为空");
        }

        if (!tryLock(userOrderId)) {
            throw new ServiceException("当前订单正在被处理，请稍后重试");
        }

        try {
            UserOrder userOrder = userOrderService.getById(userOrderId);
            if (userOrder == null) {
                throw new ServiceException("主订单不存在");
            }

            if (!UserOrderStageEnum.PROCESSING.getCode().equals(userOrder.getStage())) {
                throw new ServiceException("只有加工阶段的订单才能接单");
            }

            List<RecycleOrderInfo> existingOrders = getAllByParentId(userOrderId);
            boolean hasProcessingOrder = existingOrders.stream()
                    .anyMatch(order -> RecycleOrderTypeEnum.PROCESSING.getCode().equals(order.getType()));
            if (hasProcessingOrder) {
                throw new ServiceException("该订单已经存在分拣子订单，无法重复接单");
            }

            Processor processor = processorService.getById(processorId);
            if (processor == null) {
                throw new ServiceException("经办人不存在");
            }

            UserOrderDTO userOrderDTO = new UserOrderDTO();
            BeanUtil.copyProperties(userOrder, userOrderDTO);
            userOrderDTO.setProcessorId(processorId);
            userOrderDTO.setProcessorName(processor.getName());

            RecycleOrderInfo recycleOrderInfo = baseRecycleOrderServer.createRecycleOrderFromUserOrderByType(
                    userOrderDTO, userOrder, RecycleOrderTypeEnum.PROCESSING, false);
            recycleOrderInfo.setSortingStatus(SortingStatusEnum.PENDING.getCode());
            recycleOrderInfo.setProcessor(processor.getName());
            recycleOrderInfo.setProcessorId(processor.getId());
            recycleOrderInfo.setProcessorPhone(processor.getPhone());

            createOrUpdate(recycleOrderInfo);
            return true;
        } finally {
            releaseLock(userOrderId);
        }
    }

    /**
     * 更新运输状态
     * @param orderId 订单ID
     * @param transportStatus 新的运输状态
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransportStatus(String orderId, String transportStatus) {
        if (StrUtil.isBlank(orderId) || StrUtil.isBlank(transportStatus)) {
            throw new ServiceException("订单ID和运输状态不能为空");
        }

        // 验证运输状态是否有效
        if (!TransportStatusEnum.isValid(transportStatus)) {
            throw new ServiceException("无效的运输状态");
        }

        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        order.setTransportStatus(transportStatus);

        // 如果是确认送达，设置结束时间
        if (TransportStatusEnum.ARRIVED.getCode().equals(transportStatus)) {
            order.setEndTime(new Date());
        }

        return updateById(order);
    }

    /**
     * 确认送达（单个订单）
     * @param orderId 订单ID
     * @return 是否确认成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmArrival(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }

        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        if (!TransportStatusEnum.TRANSPORTING.getCode().equals(order.getTransportStatus())) {
            throw new ServiceException("订单【" + order.getNo() + "】不在运输中状态，无法确认送达");
        }

        order.setTransportStatus(TransportStatusEnum.ARRIVED.getCode());

        UserOrderDTO dto = new UserOrderDTO();
        dto.setId(order.getParentId());
        userOrderService.settleOrder(dto,false);
        return updateById(order);
    }

    /**
     * 交付订单（从交付大厅到运输中）
     * @param request 交付请求
     * @return 是否交付成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deliverOrder(DeliverOrderRequest request) {
        if (request == null) {
            throw new ServiceException("交付请求不能为空");
        }
        String orderId = StrUtil.isNotBlank(request.getOrderId()) ? request.getOrderId() : request.getId();
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }

        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        // 检查订单状态

        String orderType = order.getType();
        if (orderType.equals(RecycleOrderTypeEnum.TRANSPORT.getCode())) {
            if (!TransportStatusEnum.GRABBED.getCode().equals(order.getTransportStatus())) {
                throw new ServiceException("只有交付大厅的订单才能交付");
            }
            // 更新状态为运输中
            order.setTransportStatus(TransportStatusEnum.TRANSPORTING.getCode());
        }else if (orderType.equals(RecycleOrderTypeEnum.PROCESSING.getCode())) {
            order.setSortingStatus(SortingStatusEnum.SORTING.getCode());
        }
        boolean orderUpdated = updateById(order);
        boolean userOrderUpdated = updateUserOrderDeliveryInfo(order, request);
        return orderUpdated && userOrderUpdated;
    }

    /**
     * 同步更新主订单的交付信息
     */
    private boolean updateUserOrderDeliveryInfo(RecycleOrder recycleOrder, DeliverOrderRequest request) {
        String parentId = recycleOrder.getParentId();
        if (StrUtil.isBlank(parentId)) {
            return true;
        }
        UserOrder userOrder = userOrderService.getById(parentId);
        if (userOrder == null) {
            throw new ServiceException("关联的主订单不存在");
        }
        userOrder.setDeliveryStatus(DeliveryStatusEnum.DELIVERED.getCode());
        userOrder.setDeliveryMethod(OrderDeliveryMethodEnum.ONLINE.getCode());
        userOrder.setDeliveryTime(new Date());
        if (StrUtil.isNotBlank(request.getCustomerSignature())) {
            userOrder.setPartnerSignature(request.getCustomerSignature());
        }
        if (StrUtil.isNotBlank(request.getProcessorSignature())) {
            userOrder.setProcessorSignature(request.getProcessorSignature());
        }
        userOrder.setDeliveryPhoto(request.getDeliveryPhoto());
        return userOrderService.updateById(userOrder);
    }

    /**
     * 保存交付单 PDF URL
     * @param orderId 订单ID
     * @param deliveryNotePdfUrl 交付单PDF URL
     * @return 是否保存成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDeliveryNotePdf(String orderId, String deliveryNotePdfUrl) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        if (StrUtil.isBlank(deliveryNotePdfUrl)) {
            throw new ServiceException("交付单PDF URL不能为空");
        }

        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        order.setDeliveryNotePdfUrl(deliveryNotePdfUrl);
        return updateById(order);
    }

    /**
     * 获取交付单 PDF URL
     * @param orderId 订单ID
     * @return 交付单PDF URL
     */
    public String getDeliveryNotePdfUrl(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }

        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        return order.getDeliveryNotePdfUrl();
    }

    /**
     * 校验订单识别码是否属于指定订单
     * @param orderId 订单ID
     * @param identifyCode 订单识别码
     * @return 是否匹配
     */
    public boolean validateOrderIdentifyCode(String orderId, String identifyCode) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        if (StrUtil.isBlank(identifyCode)) {
            throw new ServiceException("订单识别码不能为空");
        }

        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }

        return identifyCode.equals(order.getIdentifyCode());
    }

    /**
     * 获取开始分拣列表（待分拣的加工订单）
     * @return 待分拣订单列表
     */
    public List<RecycleOrder> getStartSortingList(String processorId) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode())
                .eq(RecycleOrder::getSortingStatus, SortingStatusEnum.PENDING.getCode())
                .orderByDesc(RecycleOrder::getCreateTime);
        if (StrUtil.isNotBlank(processorId)) {
            wrapper.eq(RecycleOrder::getProcessorId, processorId);
        }
        return list(wrapper);
    }

    /**
     * 获取结果暂存列表（分拣中的加工订单）
     * @return 分拣中的订单列表
     */
    public List<RecycleOrder> getSortingTempList(String processorId) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode())
                .eq(RecycleOrder::getSortingStatus, SortingStatusEnum.SORTING.getCode())
                .orderByDesc(RecycleOrder::getCreateTime);
        if (StrUtil.isNotBlank(processorId)) {
            wrapper.eq(RecycleOrder::getProcessorId, processorId);
        }
        return list(wrapper);
    }

    /**
     * 获取已分拣列表（已分拣的加工订单）
     * @return 已分拣订单列表
     */
    public List<RecycleOrder> getSortedList(String processorId) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode())
                .eq(RecycleOrder::getSortingStatus, SortingStatusEnum.SORTED.getCode())
                .orderByDesc(RecycleOrder::getCreateTime);
        if (StrUtil.isNotBlank(processorId)) {
            wrapper.eq(RecycleOrder::getProcessorId, processorId);
        }
        return list(wrapper);
    }

    /**
     * 获取我的分拣列表（指定经办人的未分拣订单）
     * @param processorId 经办人ID（必填）
     * @return 未分拣订单列表
     */
    public List<RecycleOrder> getMyPendingSortingList(String processorId) {
        if (StrUtil.isBlank(processorId)) {
            throw new ServiceException("经办人ID不能为空");
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode())
                .eq(RecycleOrder::getProcessorId, processorId)
                .ne(RecycleOrder::getSortingStatus, SortingStatusEnum.SORTED.getCode())
                .orderByDesc(RecycleOrder::getCreateTime);
        return list(wrapper);
    }

    /**
     * 根据主订单ID获取加工（分拣）子订单
     * @param parentId 主订单ID
     * @return 加工子订单
     */
    public RecycleOrder getProcessingOrderByParentId(String parentId) {
        if (StrUtil.isBlank(parentId)) {
            throw new ServiceException("主订单ID不能为空");
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getParentId, parentId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode())
                .orderByDesc(RecycleOrder::getCreateTime)
                .last("limit 1");
        return getOne(wrapper, false);
    }

    /**
     * 判断能否分拣
     * 检查主订单的交付状态是否为已交付
     * @param orderId 子订单ID
     * @return 判断结果，true表示可以分拣，false表示不能分拣
     * @throws ServiceException 如果订单不存在或主订单未交付，抛出异常
     */
    public boolean canSort(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        // 查询子订单
        RecycleOrder recycleOrder = getById(orderId);
        if (recycleOrder == null) {
            throw new ServiceException("订单不存在");
        }
        
        // 获取主订单ID
        String parentId = recycleOrder.getParentId();
        if (StrUtil.isBlank(parentId)) {
            throw new ServiceException("该订单没有关联的主订单");
        }
        
        // 查询主订单
        UserOrder userOrder = userOrderService.getById(parentId);
        if (userOrder == null) {
            throw new ServiceException("主订单不存在");
        }
        
        // 检查交付状态
        String deliveryStatus = userOrder.getDeliveryStatus();
        if (!DeliveryStatusEnum.DELIVERED.getCode().equals(deliveryStatus)) {
            throw new ServiceException("需要先交付才能分拣");
        }
        
        return true;
    }

    /**
     * 保存分拣结果（暂存）
     * @param request 分拣请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSortingResult(ProcessingOrderSubmitRequest request) {
        RecycleOrder order = getById(request.getOrderId());
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!RecycleOrderTypeEnum.PROCESSING.getCode().equals(order.getType())) {
            throw new ServiceException("只有加工订单才能进行分拣");
        }
        // 更新分拣状态为分拣中
        order.setSortingStatus(SortingStatusEnum.SORTING.getCode());
        updateById(order);
        // 保存分拣明细
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<RecycleOrderItem> items = convertToRecycleOrderItems(request.getOrderId(), request.getItems());
            recycleOrderItemService.saveOrUpdateBatch(items);
        }
        return true;
    }

    /**
     * 提交分拣结果（完成分拣）
     * @param request 分拣请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitSortingResult(ProcessingOrderSubmitRequest request) {
        String orderId = request.getOrderId();
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        UserOrderDTO dto = new UserOrderDTO();
        dto.setId(order.getParentId());
        userOrderService.settleOrder(dto,false);
    }

    /**
     * 转换为 RecycleOrderItem 列表
     */
    private List<RecycleOrderItem> convertToRecycleOrderItems(String orderId, List<RecycleOrderItem> requests) {
        return requests.stream().map(req -> {
            RecycleOrderItem item = new RecycleOrderItem();
            item.setRecycleOrderId(orderId);
            item.setGoodNo(req.getGoodNo());
            item.setGoodType(req.getGoodType());
            item.setGoodName(req.getGoodName());
            item.setGoodModel(req.getGoodModel());
            item.setGoodCount(req.getGoodCount());
            item.setGoodWeight(req.getGoodWeight());
            item.setGoodPrice(req.getGoodPrice());
            item.setGoodTotalPrice(req.getGoodTotalPrice());
            item.setGoodRemark(req.getGoodRemark());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 根据识别码获取订单
     * @param identifyCode 识别码
     * @return 订单信息
     */
    public List<RecycleOrder> getByIdentifyCode(String identifyCode) {
        if (StrUtil.isBlank(identifyCode)) {
            throw new ServiceException("识别码不能为空");
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleOrder::getIdentifyCode, identifyCode)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode());
        return list(wrapper);
    }

    /**
     * 根据多个父订单ID查询所有子订单
     * @param parentIds 父订单ID列表
     * @return 子订单列表
     */
    public List<RecycleOrderInfo> getAllByParentIds(List<String> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RecycleOrder::getParentId, parentIds);
        List<RecycleOrder> orders = list(wrapper);
        return orders.stream().map(order -> {
            RecycleOrderInfo info = new RecycleOrderInfo();
            BeanUtil.copyProperties(order, info);
            return info;
        }).collect(Collectors.toList());
    }
}

