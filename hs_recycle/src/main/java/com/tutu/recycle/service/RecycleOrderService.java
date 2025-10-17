package com.tutu.recycle.service;

import cn.binarywang.wx.miniapp.api.WxMaService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.DirectedGraph;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.dto.RecycleOrderTracePath;
import com.tutu.recycle.dto.RecycleOrderTraceResponse;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleContractItem;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.order.RecycleOrderItem;
import com.tutu.recycle.entity.order.RecycleOrderTrace;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.mapper.RecycleOrderMapper;
import com.tutu.recycle.request.ProcessingOrderSubmitRequest;
import com.tutu.recycle.request.RecycleOrderQueryRequest;
import com.tutu.recycle.request.recycle_order.CreateRecycleOrderRequest;
import com.tutu.recycle.request.recycle_order.SourceCode;
import com.tutu.recycle.request.recycle_order.TraceAbilityData;
import com.tutu.recycle.schema.RecycleOrderInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.Query;

import com.tutu.user.entity.Account;
import com.tutu.user.enums.UserUseTypeEnum;
import com.tutu.user.service.AccountService;
import org.springframework.web.multipart.MultipartFile;
import com.tutu.system.vo.FileUploadVO;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import com.tutu.system.service.MessageService;
import com.tutu.system.service.SysFileService;
import com.tutu.system.utils.MessageUtil;

import jakarta.annotation.Resource;

import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class RecycleOrderService extends ServiceImpl<RecycleOrderMapper, RecycleOrder> {
    @Resource
    private WxMaService wxMaService;
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
    public RecycleOrder createOrUpdate(CreateRecycleOrderRequest request){
        String id = request.getId();
        // 保存订单
        RecycleOrder recycleOrder = new RecycleOrder();
        BeanUtil.copyProperties(request, recycleOrder);
        if (StrUtil.isBlank(id)) {
            // 生成订单编号
            recycleOrder.setNo(IdUtil.simpleUUID());
            save(recycleOrder);
        }else{
            updateById(recycleOrder);
        }
        // 先查找对应订单的订单项
        List<RecycleOrderItem> orderItems = recycleOrderItemService.list(new LambdaQueryWrapper<RecycleOrderItem>().eq(RecycleOrderItem::getRecycleOrderId, recycleOrder.getId()));
        if (CollUtil.isNotEmpty(orderItems)) {
            // 和传递过来的订单项进行对比，删除不存在的订单项
            List<String> itemIds = request.getItems().stream().map(RecycleOrderItem::getId).collect(Collectors.toList());
            orderItems.stream().filter(item -> !itemIds.contains(item.getId())).forEach(item -> recycleOrderItemService.removeById(item.getId()));
        }
        // 保存订单明细
        List<RecycleOrderItem> items = request.getItems();
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
    public String createOrderQrcode(String orderId){
        try {
            File wxaCode = wxMaService.getQrcodeService().createWxaCode("/pages/home/home?orderId=" + orderId);
            
            // 将File转换为字节数组
            try (FileInputStream fis = new FileInputStream(wxaCode);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                byte[] bytes = bos.toByteArray();
                
                // 创建自定义MultipartFile实现
                MultipartFile multipartFile = new CustomMultipartFile(
                    "file",
                    "order_qrcode.png",
                    "image/png",
                    bytes
                );
                
                // 上传文件到服务器
                FileUploadVO fileUploadVO = sysFileService.uploadFile(multipartFile, null, null);
                
                return fileUploadVO.getFileUrl();
            }
        } catch (WxErrorException | IOException e) {
            throw new RuntimeException("生成订单二维码失败", e);
        }
    }
    
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
    @Transactional(rollbackFor = Exception.class)
    public void submitProcessingOrder(String orderId, String orderNodeImg, List<ProcessingOrderSubmitRequest.OrderItemUpdateRequest> items, String userId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        // 获取用户信息
        Account account = accountService.getById(userId);
        if (account == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 验证用户身份
        if (!UserUseTypeEnum.SORTING.getCode().equals(account.getUseType())) {
            throw new ServiceException("只有分拣用户才能提交加工订单");
        }
        
        // 根据订单ID查询订单
        RecycleOrder order = getById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        
        // 验证订单类型
        if (!RecycleOrderTypeEnum.PROCESSING.getCode().equals(order.getType())) {
            throw new ServiceException("只能提交加工订单");
        }
        
        // 验证订单状态，已上传的订单不能重复提交
        if (RecycleOrderStatusEnum.UPLOADED.getCode().equals(order.getStatus())) {
            throw new ServiceException("订单已上传，无法重复提交");
        }
        
        // 更新订单信息
        order.setOrderNodeImg(orderNodeImg);
        order.setUploadTime(new Date());
        order.setStatus(RecycleOrderStatusEnum.UPLOADED.getCode());
        
        // 保存订单
        updateById(order);
        
        // 添加订单明细
        if (items != null && !items.isEmpty()) {
            for (ProcessingOrderSubmitRequest.OrderItemUpdateRequest itemRequest : items) {
                // 创建新的订单明细
                RecycleOrderItem orderItem = new RecycleOrderItem();
                orderItem.setRecycleOrderId(orderId);
                orderItem.setGoodNo(itemRequest.getGoodNo());
                orderItem.setGoodType(itemRequest.getGoodType());
                orderItem.setGoodName(itemRequest.getGoodName());
                orderItem.setGoodModel(itemRequest.getGoodModel());
                orderItem.setGoodCount(itemRequest.getGoodCount());
                orderItem.setGoodWeight(itemRequest.getGoodWeight());
                orderItem.setGoodPrice(itemRequest.getGoodPrice());
                orderItem.setGoodTotalPrice(itemRequest.getGoodTotalPrice());
                orderItem.setGoodRemark(itemRequest.getGoodRemark());
                
                // 保存新的明细
                recycleOrderItemService.save(orderItem);
            }
        }
    }

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
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> getRecycleOrdersByPage(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> page,
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
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(RecycleOrder::getCreateTime);
        
        return page(page, wrapper);
    }

    /**
     * 根据订单识别码查询订单信息
     * @param identifyCode 订单识别码
     * @return 订单信息
     */
    public List<RecycleOrder> getByIdentifyCode(String identifyCode) {
        if (StrUtil.isBlank(identifyCode)) {
            return null;
        }
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>();
        wrapper.eq(RecycleOrder::getIdentifyCode, identifyCode);
        return list(wrapper);
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
}
