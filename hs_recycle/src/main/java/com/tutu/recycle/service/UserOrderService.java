package com.tutu.recycle.service;

import ch.qos.logback.core.util.TimeUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.dto.DeliveryDTO;
import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.dto.UserOrderInfo;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleContractBeneficiary;
import com.tutu.recycle.entity.order.RecycleOrderItem;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.enums.*;
import com.tutu.recycle.mapper.UserOrderMapper;
import com.tutu.recycle.request.QueryOrderByIdRequest;
import com.tutu.recycle.request.SaveSupplementMaterialRequest;
import com.tutu.recycle.request.WxUserCreateOrderRequest;
import com.tutu.recycle.response.SortingDeliveryHallResponse;
import com.tutu.recycle.response.WxTransportOrderListResponse;
import com.tutu.recycle.schema.RecycleOrderInfo;
import com.tutu.recycle.utils.UserOrderNoGenerator;
import com.tutu.recycle.utils.pdf.PdfGenerator;
import com.tutu.common.enums.BaseEnum;
import com.tutu.point.entity.AccountPointDetail;
import com.tutu.point.entity.PointGlobalConfig;
import com.tutu.point.enums.PointChangeDirectionEnum;
import com.tutu.point.enums.PointChangeTypeEnum;
import com.tutu.point.service.AccountPointDetailService;
import com.tutu.point.service.PointGlobalConfigService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tutu.user.entity.Account;
import com.tutu.user.entity.AccountCustomer;
import com.tutu.user.entity.AccountServiceScope;
import com.tutu.user.service.AccountCustomerService;
import com.tutu.user.service.AccountService;
import cn.hutool.core.util.StrUtil;
import com.tutu.user.service.AccountServiceScopeService;
import com.tutu.system.config.FileUploadConfig;
import com.tutu.system.utils.FileUtils;
import com.tutu.system.utils.OpenPdfUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * 用户订单服务实现类
 */
@Service
public class UserOrderService extends ServiceImpl<UserOrderMapper, UserOrder> {
    @Resource
    private RecycleOrderService recycleOrderService;
    
    @Resource
    private RecycleContractService recycleContractService;
    
    @Resource
    private PointGlobalConfigService pointGlobalConfigService;
    
    @Resource
    private AccountPointDetailService accountPointDetailService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountCustomerService accountCustomerService;
    @Resource
    private AccountServiceScopeService accountServiceScopeService;
    
    @Resource
    private SpringTemplateEngine templateEngine;
    
    @Resource
    private FileUploadConfig fileUploadConfig;

    /**
     * 获取当前登录用户作为合作方的订单列表
     * @param order
     * @return
     */
    public List<UserOrder> getWxUserOrderList(UserOrder order) {
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        // 合作方
        wrapper.eq(UserOrder::getContractPartner, order.getContractPartner());
        // 阶段
        Optional.ofNullable(order.getStage()).ifPresent(stage -> {
            // 移动端特殊要求
            if (stage.equals("doing")){
                // 执行中
                wrapper.in(UserOrder::getStage, UserOrderStageEnum.PURCHASE.getCode(),
                        UserOrderStageEnum.TRANSPORT.getCode(),
                        UserOrderStageEnum.PROCESSING.getCode()
                );
            } else if (stage.equals("pending")) {
                wrapper.in(UserOrder::getStage, UserOrderStageEnum.PENDING_SETTLEMENT.getCode(),
                        UserOrderStageEnum.PENDING_CUSTOMER_CONFIRMATION.getCode()
                );
            } else {
                wrapper.eq(UserOrder::getStage, stage);
            }

        });
        wrapper.orderByDesc(UserOrder::getCreateTime);
        return list(wrapper);
    }
    
    /**
     * 创建用户订单
     * 自动生成订单编号，设置初始阶段和状态
     * 同时创建一个采购类型的回收订单
     * @param userOrder 用户订单对象
     * @return 创建的用户订单
     */
    @Transactional(rollbackFor = Exception.class)
    public UserOrder createUserOrder(UserOrder userOrder) {
        // 自动生成订单编号
        userOrder.setNo(UserOrderNoGenerator.generate());
        // 设置初始阶段为采购
        userOrder.setStage(UserOrderStageEnum.PURCHASE.getCode());
        if (StrUtil.isBlank(userOrder.getPricingMethod())) {
            // 默认一般计价方式
            userOrder.setPricingMethod(PricingMethodEnum.GENERAL.getCode());
        }
        // 初始为 未交付
        userOrder.setDeliveryStatus(DeliveryStatusEnum.NOT_DELIVERED.getCode());
        // 用户系数
        if (StrUtil.isNotBlank(userOrder.getContractPartner())) {
            Account account = accountService.getById(userOrder.getContractPartner());
            if (account == null) {
                throw new ServiceException("合作方不存在");
            }
            userOrder.setAccountCoefficient(new BigDecimal(account.getScoreFactor()));
        }
        // 初始状态
        userOrder.setSettlementStatus(SettlementStatusEnum.NOT_SETTLED.getCode());
        userOrder.setDeliveryStatus(DeliveryStatusEnum.NOT_DELIVERED.getCode());
        save(userOrder);
        return userOrder;
    }

    /**
     * 微信创建用户订单
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void createWxUserOrder(WxUserCreateOrderRequest request) {
        if (StrUtil.isBlank(request.getTransportMethod())){
            throw new ServiceException("运输方式不能为空");
        }
        UserOrder userOrder = new UserOrder();
        BeanUtil.copyProperties(request, userOrder);
        // 自动生成订单编号
        userOrder.setNo(UserOrderNoGenerator.generate());
        // 设置初始阶段为采购
        userOrder.setStage(UserOrderStageEnum.PURCHASE.getCode());
        // 查询合同信息
        RecycleContract contract = recycleContractService.getById(request.getContractId());
        if (contract == null) {
            throw new ServiceException("合同不存在");
        }
        userOrder.setContractNo(contract.getNo());
        userOrder.setContractName(contract.getName());
        // 合作方
        userOrder.setContractPartner(contract.getPartner());
        userOrder.setContractPartnerName(contract.getPartnerName());
        // 甲方
        userOrder.setPartyA(contract.getPartyA());
        userOrder.setPartyAName(contract.getPartyAName());
        // 乙方
        userOrder.setPartyB(contract.getPartyB());
        userOrder.setPartyBName(contract.getPartyBName());

        createUserOrder(userOrder);
        // 结算采购订单
        UserOrderDTO userOrderDTO = new UserOrderDTO();
        BeanUtil.copyProperties(userOrder,userOrderDTO,CopyOptions.create().ignoreNullValue());
        BeanUtil.copyProperties(request,userOrderDTO,CopyOptions.create().ignoreNullValue());
        settleOrder(userOrderDTO,false);
    }
    
    /**
     * 根据ID查询用户订单
     * @param id 订单ID
     * @return 用户订单对象
     */
    public UserOrder getUserOrderById(String id) {
        UserOrder userOrder = getById(id);
        if (userOrder != null) {
            fillProcessorName(userOrder);
        }
        return userOrder;
    }
    
    /**
     * 查询用户订单信息及其子回收订单
     * 根据回收订单类型，将订单信息映射到不同的字段
     * @param id 用户订单ID
     * @return 用户订单信息（包含子回收订单）
     */
    public UserOrderInfo getUserOrderInfo(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ServiceException("用户订单ID不能为空");
        }
        
        // 查询用户订单
        UserOrder userOrder = getById(id);
        if (userOrder == null) {
            throw new ServiceException("用户订单不存在");
        }
        
        // 填充经办人名称
        fillProcessorName(userOrder);
        
        // 查询所有子回收订单
        List<RecycleOrderInfo> recycleOrderInfoList = recycleOrderService.getAllByParentId(id);
        
        // 将订单列表转为Map，key为订单类型
        Map<String, RecycleOrderInfo> orderMap = recycleOrderInfoList.stream()
                .collect(Collectors.toMap(
                        RecycleOrderInfo::getType,
                        Function.identity(),
                        (existing, replacement) -> {
                            // 如果存在相同类型的订单，保留第一个
                            return existing;
                        }
                ));
        
        // 构建返回对象
        UserOrderInfo userOrderInfo = new UserOrderInfo();
        userOrderInfo.setUserOrder(userOrder);
        
        // 根据订单类型映射到不同的字段
        RecycleOrderInfo purchaseOrder = orderMap.get(RecycleOrderTypeEnum.PURCHASE.getCode());
        if (purchaseOrder != null) {
            userOrderInfo.setPurchaseOrder(purchaseOrder);
        }
        
        RecycleOrderInfo transportOrder = orderMap.get(RecycleOrderTypeEnum.TRANSPORT.getCode());
        if (transportOrder != null) {
            userOrderInfo.setTransportOrder(transportOrder);
        }
        
        RecycleOrderInfo processingOrder = orderMap.get(RecycleOrderTypeEnum.PROCESSING.getCode());
        if (processingOrder != null) {
            userOrderInfo.setProcessingOrder(processingOrder);
        }
        
        RecycleOrderInfo storageOrder = orderMap.get(RecycleOrderTypeEnum.STORAGE.getCode());
        if (storageOrder != null) {
            userOrderInfo.setStorageOrder(storageOrder);
        }
        
        return userOrderInfo;
    }
    
    /**
     * 更新用户订单
     * @param userOrder 用户订单对象
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserOrder(UserOrder userOrder) {
        return updateById(userOrder);
    }
    
    /**
     * 根据ID删除用户订单
     * @param id 订单ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserOrderById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        // 检查订单是否存在
        UserOrder userOrder = getById(id);
        if (userOrder == null) {
            throw new ServiceException("用户订单不存在");
        }
        
        // 检查是否存在子订单
        List<RecycleOrderInfo> childOrders = recycleOrderService.getAllByParentId(id);
        if (childOrders != null && !childOrders.isEmpty()) {
            throw new ServiceException("该订单已存在登记信息,无法删除");
        }
        
        return removeById(id);
    }
    
    /**
     * 根据订单编号查询用户订单
     * @param no 订单编号
     * @return 用户订单对象
     */
    public UserOrder getUserOrderByNo(String no) {
        if (StrUtil.isBlank(no)) {
            return null;
        }
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOrder::getNo, no);
        UserOrder userOrder = getOne(wrapper);
        if (userOrder != null) {
            fillProcessorName(userOrder);
        }
        return userOrder;
    }
    
    /**
     * 根据合同ID查询用户订单列表
     * @param contractId 合同ID
     * @return 用户订单列表
     */
    public List<UserOrder> getUserOrdersByContractId(String contractId) {
        if (StrUtil.isBlank(contractId)) {
            return null;
        }
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOrder::getContractId, contractId);
        wrapper.orderByDesc(UserOrder::getCreateTime);
        List<UserOrder> list = list(wrapper);
        fillProcessorNames(list);
        return list;
    }
    
    /**
     * 根据合同编号查询用户订单列表
     * @param contractNo 合同编号
     * @return 用户订单列表
     */
    public List<UserOrder> getUserOrdersByContractNo(String contractNo) {
        if (StrUtil.isBlank(contractNo)) {
            return null;
        }
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOrder::getContractNo, contractNo);
        wrapper.orderByDesc(UserOrder::getCreateTime);
        List<UserOrder> list = list(wrapper);
        fillProcessorNames(list);
        return list;
    }

    
    /**
     * 根据订单阶段查询用户订单列表
     * @param stage 订单阶段
     * @return 用户订单列表
     */
    public List<UserOrder> getUserOrdersByStage(String stage) {
        if (StrUtil.isBlank(stage)) {
            return null;
        }
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOrder::getStage, stage);
        wrapper.orderByDesc(UserOrder::getCreateTime);
        List<UserOrder> list = list(wrapper);
        fillProcessorNames(list);
        return list;
    }

    
    /**
     * 分页查询用户订单（支持多条件查询）
     * @param page 分页对象
     * @param userOrder 查询条件对象
     * @return 分页结果
     */
    public IPage<UserOrder> getUserOrdersPage(Page<UserOrder> page, UserOrder userOrder) {
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (userOrder != null) {
            if (StrUtil.isNotBlank(userOrder.getNo())) {
                wrapper.like(UserOrder::getNo, userOrder.getNo());
            }
            if (StrUtil.isNotBlank(userOrder.getStage())) {
                wrapper.eq(UserOrder::getStage, userOrder.getStage());
            }
            if (StrUtil.isNotBlank(userOrder.getContractId())) {
                wrapper.eq(UserOrder::getContractId, userOrder.getContractId());
            }
            if (StrUtil.isNotBlank(userOrder.getContractNo())) {
                wrapper.like(UserOrder::getContractNo, userOrder.getContractNo());
            }
            if (StrUtil.isNotBlank(userOrder.getContractPartner())) {
                wrapper.eq(UserOrder::getContractPartner, userOrder.getContractPartner());
            }
//            if (StrUtil.isNotBlank(userOrder.getProcessorId())) {
//                wrapper.eq(UserOrder::getProcessorId, userOrder.getProcessorId());
//            }
//            if (StrUtil.isNotBlank(userOrder.getLocation())) {
//                wrapper.like(UserOrder::getLocation, userOrder.getLocation());
//            }
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(UserOrder::getCreateTime);
        
        IPage<UserOrder> result = page(page, wrapper);
        
        // 填充经办人名称
        fillProcessorNames(result.getRecords());
        
        return result;
    }
    
    /**
     * 查询所有用户订单列表
     * @return 用户订单列表
     */
    public List<UserOrder> getAllUserOrders() {
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(UserOrder::getCreateTime);
        List<UserOrder> list = list(wrapper);
        fillProcessorNames(list);
        return list;
    }
    
    /**
     * 批量删除用户订单
     * @param ids 订单ID集合
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUserOrders(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return removeByIds(ids);
    }

    
    /**
     * 更新订单阶段
     * @param id 订单ID
     * @param stage 新阶段
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserOrderStage(String id, String stage) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(stage)) {
            return false;
        }
        
        // 验证阶段枚举值是否有效
        if (!UserOrderStageEnum.isValid(stage)) {
            throw new ServiceException("无效的订单阶段");
        }
        
        UserOrder userOrder = getById(id);
        if (userOrder == null) {
            return false;
        }
        userOrder.setStage(stage);
        return updateById(userOrder);
    }
    
    /**
     * 流转到下一个阶段
     * @param id 订单ID
     * @return 是否流转成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toNextStage(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        UserOrder userOrder = getById(id);
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        
        // 获取当前阶段
        UserOrderStageEnum currentStage = UserOrderStageEnum.getByCode(userOrder.getStage());
        if (currentStage == null) {
            throw new ServiceException("当前订单阶段无效");
        }
        
        // 检查是否已经是最后一个阶段
        if (currentStage.isLastStage()) {
            throw new ServiceException("订单已经在最后阶段，无法继续流转");
        }

        // 获取下一个阶段（根据计价方式判断）
        UserOrderStageEnum nextStage = currentStage.getNextStage(userOrder.getTransportMethod());
        userOrder.setStage(nextStage.getCode());

        return updateById(userOrder);
    }

    

    /**
     * 验证订单状态流转是否合法
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否可以流转
     */
    public boolean canTransitionStatus(String currentStatus, String targetStatus) {
        if (StrUtil.isBlank(currentStatus) || StrUtil.isBlank(targetStatus)) {
            return false;
        }
        
        UserOrderStatusEnum current = UserOrderStatusEnum.getByCode(currentStatus);
        UserOrderStatusEnum target = UserOrderStatusEnum.getByCode(targetStatus);
        
        if (current == null || target == null) {
            return false;
        }
        
        return current.canTransitionTo(target);
    }
    
    /**
     * 结算订单
     * 将订单阶段和状态同时流转到下一个
     * @param userOrderRequest 订单
     * @return 是否结算成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean settleOrder(UserOrderDTO userOrderRequest,Boolean isCreateNextOrder) {
        if (StrUtil.isBlank(userOrderRequest.getId())) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(userOrderRequest.getId());
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 获取当前阶段
        UserOrderStageEnum currentStage = UserOrderStageEnum.getByCode(userOrder.getStage());
        if (currentStage == null) {
            throw new ServiceException("当前订单阶段无效");
        }        
        // 检查是否已经是最后一个阶段
        if (currentStage.isLastStage()) {
            throw new ServiceException("订单已经在最后阶段，无法继续结算");
        }
        
        // 获取下一个阶段和下一个状态（使用枚举类中的方法）
        // 根据计价方式获取下一个阶段
        UserOrderStageEnum nextStage = currentStage.getNextStage(userOrder.getTransportMethod());
        // 如果下一个阶段或下一个状态为null，说明已经到达最后阶段/状态
        if (nextStage == null) {
            throw new ServiceException("订单阶段已经是最后阶段，无法继续结算");
        }
        // 同时更新阶段
        userOrder.setStage(nextStage.getCode());
//        updateById(userOrder);
        // 如果不是完成阶段，则创建对应阶段的回收订单
        if (!nextStage.isLastStage()) {
            recycleOrderService.createRecycleOrderFromUserOrderByStage(userOrderRequest,userOrder, currentStage,true);
            if (isCreateNextOrder) {
                // 创建下一个阶段的订单
                recycleOrderService.createRecycleOrderFromUserOrderByStage(new UserOrderDTO(), userOrder, nextStage, false);
            }
        }
        
        // 如果是采购阶段，生成申请单PDF
        if (currentStage == UserOrderStageEnum.PURCHASE) {
            try {
                String applicationPdfUrl = generateApplicationPdf(userOrder, userOrderRequest);
                userOrder.setApplicationPdf(applicationPdfUrl);
            } catch (Exception e) {
                throw new ServiceException("生成申请单PDF失败"+ e.getMessage());
            }
        }
        updateById(userOrder);
        // 结算时生成 结算单
        return true;
    }

    /**
     * 保存当前阶段的子回收订单
     * 仅保存数据，不做阶段流转
     * @param userOrderDTO 用户订单DTO
     * @return 是否保存成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSubOrder(UserOrderDTO userOrderDTO) {
        if (StrUtil.isBlank(userOrderDTO.getId())) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(userOrderDTO.getId());
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        UserOrderStageEnum currentStage = UserOrderStageEnum.getByCode(userOrder.getStage());
        if (currentStage == null) {
            throw new ServiceException("当前订单阶段无效");
        }
        if (currentStage == UserOrderStageEnum.PENDING_SETTLEMENT || currentStage == UserOrderStageEnum.COMPLETED) {
            throw new ServiceException("当前阶段无需保存子订单");
        }
        recycleOrderService.createRecycleOrderFromUserOrderByStage(userOrderDTO, userOrder, currentStage,false);
        return true;
    }

    /**
     * 确认结算订单
     * 将订单从待结算阶段流转到完成阶段，并更新结算时间和调价信息
     * @param userOrderDTO 用户订单DTO
     * @return 是否确认成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmSettlement(UserOrderDTO userOrderDTO) {
        if (StrUtil.isBlank(userOrderDTO.getId())) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(userOrderDTO.getId());
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 验证当前阶段必须是待结算
        if (!(UserOrderStageEnum.PENDING_SETTLEMENT.getCode().equals(userOrder.getStage())
                || UserOrderStageEnum.PENDING_CUSTOMER_CONFIRMATION.getCode().equals(userOrder.getStage()))
        ) {
            throw new ServiceException("当前项目阶段无法确认结算");
        }
        // 获取入库订单（仓储订单）的货物详情
        List<RecycleOrderInfo> recycleOrders = recycleOrderService.getAllByParentId(userOrder.getId());
        RecycleOrderInfo storageOrder = recycleOrders.stream()
                .filter(order -> RecycleOrderTypeEnum.STORAGE.getCode().equals(order.getType()))
                .findFirst()
                .orElseThrow(() -> new ServiceException("未找到入库订单"));

        // 计算金额
        BigDecimal goodsTotalAmount = BigDecimal.ZERO;
        List<RecycleOrderItem> items = storageOrder.getItems();
        if (items != null) {
            for (RecycleOrderItem item : items) {
                BigDecimal price = Optional.ofNullable(item.getGoodPrice()).orElse(BigDecimal.ZERO);
                BigDecimal count = item.getGoodCount() != null ? BigDecimal.valueOf(item.getGoodCount()) : BigDecimal.ZERO;
//                BigDecimal weight = Optional.ofNullable(item.getGoodWeight()).orElse(BigDecimal.ZERO);
                goodsTotalAmount = goodsTotalAmount.add(price.multiply(count));
            }
        }
        //   - 货物总金额 = Σ(单价 × 数量 )
        //  - 最终总金额 = 货物总金额 × (1 + 评级系数) + 其他调价
        BigDecimal ratingCoefficient = Optional.ofNullable(userOrder.getAccountCoefficient()).orElse(BigDecimal.ZERO);
        BigDecimal otherAdjustAmount = Optional.ofNullable(userOrderDTO.getOtherAdjustAmount()).orElse(BigDecimal.ZERO);
        BigDecimal ratingCoefficientAmount = goodsTotalAmount.multiply(ratingCoefficient).setScale(2,RoundingMode.HALF_UP);
        BigDecimal totalAmount = goodsTotalAmount.add(ratingCoefficientAmount).add(otherAdjustAmount);
        // 更新金额和调价信息
        userOrder.setGoodsTotalAmount(goodsTotalAmount);
        userOrder.setTotalAmount(totalAmount);
        // 更新结算时间
        userOrder.setSettlementTime(new Date());
        // 流转到完成阶段
        userOrder.setStage(UserOrderStageEnum.PENDING_CUSTOMER_CONFIRMATION.getCode());
        userOrder.setSettlementStatus(SettlementStatusEnum.WAITING_CONFIRMATION.getCode());
        // 查询甲方和乙方
        Account partyA = accountService.getById(userOrder.getPartyA());
        Account partyB = accountService.getById(userOrder.getPartyB());
        // 生成结算单PDF
        String settlementPdfUrl = generateSettlementPdf(
                userOrder,
                partyA,
                partyB,
                userOrderDTO,
                storageOrder,
                goodsTotalAmount,
                totalAmount,
                ratingCoefficient,
                ratingCoefficientAmount,
                otherAdjustAmount);
        userOrder.setSettlementPdf(settlementPdfUrl);
        return updateById(userOrder);
    }

    /**
     * 确认结算结果
     * 只有待客户确认的订单才可以确认结算
     * 更新阶段、结算确认时间和结算状态为已结算
     * @param orderId 订单ID
     * @return 是否确认成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmSettlementResult(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(orderId);
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 验证当前阶段必须是待客户确认
        if (!UserOrderStageEnum.PENDING_CUSTOMER_CONFIRMATION.getCode().equals(userOrder.getStage())) {
            throw new ServiceException("只有待客户确认的订单才可以确认结算");
        }
        // 更新阶段为已完成
        userOrder.setStage(UserOrderStageEnum.COMPLETED.getCode());
        // 更新结算确认时间
        userOrder.setConfirmTime(new Date());
        // 更新结算状态为已结算
        userOrder.setSettlementStatus(SettlementStatusEnum.SETTLED.getCode());
        updateById(userOrder);
        // 根据合同受益人发放积分
        distributeSettlementPoints(userOrder);
    }

    /**
     * 否定结算
     * 只有待客户确认的订单才可以否定结算
     * 更新结算状态为已驳回
     * @param request 订单ID
     * @return 是否否定成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectSettlement(QueryOrderByIdRequest request) {
        String orderId = request.getOrderId();
        if (StrUtil.isBlank(orderId)) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(orderId);
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 验证当前阶段必须是待客户确认
        if (!UserOrderStageEnum.PENDING_CUSTOMER_CONFIRMATION.getCode().equals(userOrder.getStage())) {
            throw new ServiceException("只有待客户确认的订单才可以否定结算");
        }
        // 更新结算状态为已驳回
        userOrder.setSettlementStatus(SettlementStatusEnum.REJECTED.getCode());
        userOrder.setConfirmRemark(request.getConfirmRemark());
        updateById(userOrder);
    }

    /**
     * 保存订单补充材料PDF
     * @param request 补充材料请求
     * @return 是否保存成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSupplementMaterials(SaveSupplementMaterialRequest request) {
        if (request == null || StrUtil.isBlank(request.getOrderId())) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(request.getOrderId());
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        Optional.ofNullable(request.getDeliveryPdf()).ifPresent(userOrder::setDeliveryPdf);
        Optional.ofNullable(request.getSettlementPdf()).ifPresent(userOrder::setSettlementPdf);
        Optional.ofNullable(request.getApplicationPdf()).ifPresent(userOrder::setApplicationPdf);
        return updateById(userOrder);
    }

    /**
     * 交付订单
     * 保存订单的交付信息，并更新交付状态为已交付
     * 如果是线上交付，会自动生成交付单PDF
     * @param deliveryDTO 交付信息DTO
     * @return 是否交付成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delivery(DeliveryDTO deliveryDTO) {
        if (StrUtil.isBlank(deliveryDTO.getOrderId())) {
            throw new ServiceException("订单ID不能为空");
        }
        UserOrder userOrder = getById(deliveryDTO.getOrderId());
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 更新交付信息
        Optional.ofNullable(deliveryDTO.getDeliveryTime()).ifPresent(userOrder::setDeliveryTime);
        Optional.ofNullable(deliveryDTO.getDeliveryMethod()).ifPresent(userOrder::setDeliveryMethod);
        Optional.ofNullable(deliveryDTO.getDeliveryPhoto()).ifPresent(userOrder::setDeliveryPhoto);
        // 签名
        Optional.ofNullable(deliveryDTO.getPartnerSignature()).ifPresent(userOrder::setPartnerSignature);
        Optional.ofNullable(deliveryDTO.getProcessorSignature()).ifPresent(userOrder::setProcessorSignature);
        // PDF 线下交付时会上传pdf
        Optional.ofNullable(deliveryDTO.getDeliveryPDF()).ifPresent(userOrder::setDeliveryPdf);
        // 更新交付状态为已交付
        userOrder.setDeliveryStatus(DeliveryStatusEnum.DELIVERED.getCode());
        boolean result = updateById(userOrder);
        
        // 如果是线上交付，生成交付单PDF
        if (result && OrderDeliveryMethodEnum.ONLINE.getCode().equals(deliveryDTO.getDeliveryMethod())) {
            try {
                // 查找对应的回收订单（优先查找运输订单，如果没有则查找加工订单）
                List<RecycleOrderInfo> recycleOrders = recycleOrderService.getAllByParentId(deliveryDTO.getOrderId());
                
                // 优先使用运输订单
                RecycleOrderInfo targetOrder = recycleOrders.stream()
                        .filter(order -> RecycleOrderTypeEnum.TRANSPORT.getCode().equals(order.getType()))
                        .findFirst()
                        .orElse(null);
                
                // 如果没有运输订单，则使用加工订单
                if (targetOrder == null) {
                    targetOrder = recycleOrders.stream()
                            .filter(order -> RecycleOrderTypeEnum.PROCESSING.getCode().equals(order.getType()))
                            .findFirst()
                            .orElse(null);
                }
                
                // 如果找到了对应的回收订单，生成交付单PDF
                if (targetOrder != null) {
                    String pdfUrl = recycleOrderService.generateDeliveryNotePdf(targetOrder.getId());
                    // 将PDF URL保存到订单的交付PDF字段
                    userOrder.setDeliveryPdf(pdfUrl);
                    updateById(userOrder);
                }
            } catch (Exception e) {
                // 生成PDF失败不影响交付流程，只记录错误
                // 线上交付时如果PDF生成失败，可以后续手动生成或重新生成
            }
        }
        
        return result;
    }
    
    /**
     * 填充单个订单的经办人名称
     * @param userOrder 用户订单
     */
    private void fillProcessorName(UserOrder userOrder) {
//        if (userOrder != null && StrUtil.isNotBlank(userOrder.getProcessorId())) {
//            Processor processor = processorService.getById(userOrder.getProcessorId());
//            if (processor != null) {
//                userOrder.setProcessorName(processor.getName());
//            }
//        }
    }
    
    /**
     * 批量填充订单列表的经办人名称
     * @param userOrders 用户订单列表
     */
    private void fillProcessorNames(List<UserOrder> userOrders) {
        if (userOrders == null || userOrders.isEmpty()) {
            return;
        }
        
        // 收集所有经办人ID
//        List<String> processorIds = userOrders.stream()
//                .map(UserOrder::getProcessorId)
//                .filter(StrUtil::isNotBlank)
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (processorIds.isEmpty()) {
//            return;
//        }
//
//        // 批量查询经办人信息
//        List<Processor> processors = processorService.listByIds(processorIds);
//
//        // 构建经办人ID到名称的映射
//        Map<String, String> processorMap = processors.stream()
//                .collect(Collectors.toMap(Processor::getId, Processor::getName));
//
//        // 填充经办人名称
//        userOrders.forEach(order -> {
//            if (StrUtil.isNotBlank(order.getProcessorId())) {
//                order.setProcessorName(processorMap.get(order.getProcessorId()));
//            }
//        });
    }

    /**
     * 获取可抢单的用户订单列表
     * 条件：当前阶段为运输，且不存在运输子订单
     * @return 可抢单的用户订单列表
     */
    public List<WxTransportOrderListResponse> getAvailableTransportOrders() {
        return baseMapper.selectAvailableTransportOrders();
    }

    /**
     * 获取分拣交付大厅订单列表
     * 条件：当前阶段为加工，且不存在加工子订单
     * @return 分拣交付大厅订单列表
     */
    public List<SortingDeliveryHallResponse> getSortingDeliveryHallOrders() {
//        return list(new LambdaQueryWrapper<UserOrder>()
//                .eq(UserOrder::getStage, UserOrderStageEnum.PROCESSING.getCode()));
        return baseMapper.selectSortingDeliveryHallOrders();
    }

    /**
     * 获取送货上门的分拣交付大厅订单列表
     * 在原有条件基础上筛选 transport_method=home_delivery，并过滤经办人ID
     * @param processorId 经办人ID
     */
    public List<SortingDeliveryHallResponse> getSortingHomeDeliveryHallOrders(String processorId) {
        return baseMapper.selectSortingHomeDeliveryHallOrders(processorId);
    }

    /**
     * 结算后根据合同受益人发放积分
     * @param userOrder 当前订单
     */
    private void distributeSettlementPoints(UserOrder userOrder) {
        if (userOrder == null || StrUtil.isBlank(userOrder.getContractId())) {
            return;
        }
        BigDecimal totalAmount = Optional.ofNullable(userOrder.getTotalAmount()).orElse(BigDecimal.ZERO);
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        RecycleContract contract = recycleContractService.getContractById(userOrder.getContractId());
        if (contract == null || contract.getBeneficiaries() == null || contract.getBeneficiaries().isEmpty()) {
            return;
        }
        PointGlobalConfig pointGlobalConfig = pointGlobalConfigService.getConfig();
        BigDecimal pointRatio = Optional.ofNullable(pointGlobalConfig)
                .map(PointGlobalConfig::getPointRatio)
                .orElse(BigDecimal.ZERO);
        if (pointRatio.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        List<RecycleContractBeneficiary> eligibleBeneficiaries = contract.getBeneficiaries().stream()
                .filter(beneficiary -> StrUtil.isNotBlank(beneficiary.getBeneficiaryId()))
                .filter(beneficiary -> Optional.ofNullable(beneficiary.getShareRatio()).orElse(BigDecimal.ZERO)
                        .compareTo(BigDecimal.ZERO) > 0)
                .toList();
        if (eligibleBeneficiaries.isEmpty()) {
            return;
        }
        BigDecimal totalPointAmount = totalAmount.multiply(pointRatio);
        long totalPoints = totalPointAmount.setScale(0, RoundingMode.DOWN).longValue();
        if (totalPoints <= 0) {
            return;
        }
        BigDecimal totalPointsDecimal = BigDecimal.valueOf(totalPoints);
        long distributedPoints = 0L;
        for (int i = 0; i < eligibleBeneficiaries.size(); i++) {
            RecycleContractBeneficiary beneficiary = eligibleBeneficiaries.get(i);
            long remainingPoints = totalPoints - distributedPoints;
            if (remainingPoints <= 0) {
                break;
            }
            BigDecimal shareRatio = beneficiary.getShareRatio();
            long sharePoints;
            if (i == eligibleBeneficiaries.size() - 1) {
                sharePoints = remainingPoints;
            } else {
                BigDecimal sharePointDecimal = totalPointsDecimal.multiply(shareRatio);
                sharePoints = sharePointDecimal.setScale(0, RoundingMode.DOWN).longValue();
                if (sharePoints > remainingPoints) {
                    sharePoints = remainingPoints;
                }
            }
            if (sharePoints <= 0) {
                continue;
            }
            distributedPoints += sharePoints;
            AccountPointDetail pointDetail = new AccountPointDetail();
            pointDetail.setAccountId(beneficiary.getBeneficiaryId());
            pointDetail.setChangeDirection(PointChangeDirectionEnum.ADD.getValue());
            pointDetail.setChangePoint(sharePoints);
            pointDetail.setChangeType(PointChangeTypeEnum.SYSTEM_REWARD.getType());
            pointDetail.setChangeReason(StrUtil.format("订单{}结算积分奖励", Optional.ofNullable(userOrder.getNo()).orElse(userOrder.getId())));
            pointDetail.setRemark(StrUtil.format("总金额:{}, 积分比例:{}, 受益人分成:{}", totalAmount, pointRatio, shareRatio));
            accountPointDetailService.createDetail(pointDetail);
        }
    }


    /**
     * 生成申请单PDF
     * @param userOrder 用户订单
     * @param userOrderRequest 用户订单DTO（包含经办人等额外信息）
     * @return PDF文件访问URL
     * @throws Exception 生成PDF时的异常
     */
    private String generateApplicationPdf(UserOrder userOrder, UserOrderDTO userOrderRequest) throws Exception {
        // 创建Model并添加订单数据
        Model model = new ExtendedModelMap();
        
        // 构建订单数据Map
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("id", userOrder.getId());
        orderData.put("no", userOrder.getNo());
        orderData.put("type", RecycleOrderTypeEnum.PURCHASE.getCode());
        orderData.put("contractNo", userOrder.getContractNo());
        orderData.put("partyA", userOrder.getPartyAName());
        orderData.put("partyB", userOrder.getPartyBName());
        // 经办人信息从 UserOrderDTO 中获取
        String processorName = userOrderRequest != null ? userOrderRequest.getProcessorName() : null;
        orderData.put("processor", StrUtil.isNotBlank(processorName) ? processorName : "");
        orderData.put("processorPhone", ""); // TODO: 从经办人信息中获取
        // 交付地址和仓库地址从 UserOrderDTO 中获取
        String deliveryAddress = userOrderRequest != null ? userOrderRequest.getDeliveryAddress() : null;
        orderData.put("deliveryAddress", StrUtil.isNotBlank(deliveryAddress) ? deliveryAddress : "");
        String warehouseAddress = userOrderRequest != null ? userOrderRequest.getWarehouseAddress() : null;
        orderData.put("warehouseAddress", StrUtil.isNotBlank(warehouseAddress) ? warehouseAddress : "");
        // 走款账号从 UserOrderDTO 中获取
        String paymentAccount = userOrderRequest != null ? userOrderRequest.getPaymentAccount() : null;
        orderData.put("paymentAccount", StrUtil.isNotBlank(paymentAccount) ? paymentAccount : "");
        
        // 订单申请时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (userOrder.getCreateTime() != null) {
            LocalDateTime createTime = LocalDateTime.ofInstant(
                userOrder.getCreateTime().toInstant(),
                java.time.ZoneId.systemDefault()
            );
            orderData.put("startTime", createTime.format(formatter) + "（我方）");
        } else {
            orderData.put("startTime", LocalDateTime.now().format(formatter) + "（我方）");
        }
        
        model.addAttribute("orderData", orderData);
        
        // 获取采购订单的明细数据
        List<RecycleOrderInfo> recycleOrders = recycleOrderService.getAllByParentId(userOrder.getId());
        RecycleOrderInfo purchaseOrder = recycleOrders.stream()
                .filter(order -> RecycleOrderTypeEnum.PURCHASE.getCode().equals(order.getType()))
                .findFirst()
                .orElse(null);
        
        // 构建订单明细列表
        List<Map<String, Object>> orderItems = new ArrayList<>();
        if (purchaseOrder != null && purchaseOrder.getItems() != null) {
            for (RecycleOrderItem item : purchaseOrder.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                // 获取货物类型文本
                String goodType = item.getGoodType();
                itemMap.put("type", goodType);
                itemMap.put("typeText", getItemTypeText(goodType));
                itemMap.put("name", item.getGoodName());
                itemMap.put("specification", item.getGoodModel());
                itemMap.put("remark", Optional.ofNullable(item.getGoodRemark()).orElse(""));
                itemMap.put("quantity", Optional.ofNullable(item.getGoodCount()).orElse(0));
                itemMap.put("unitPrice", Optional.ofNullable(item.getGoodPrice()).orElse(BigDecimal.ZERO));
                // 计算总价：单价 × 数量
                BigDecimal amount = Optional.ofNullable(item.getGoodPrice()).orElse(BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(Optional.ofNullable(item.getGoodCount()).orElse(0)));
                itemMap.put("amount", amount);
                orderItems.add(itemMap);
            }
        }
        
        // 如果没有明细，使用空列表
        model.addAttribute("orderItems", orderItems);
        
        // 订单类型文本
        model.addAttribute("orderTypeText", getOrderTypeText(RecycleOrderTypeEnum.PURCHASE.getCode()));
        
        // 流转方向文本
        model.addAttribute("flowDirectionText", getFlowDirectionText(RecycleOrderTypeEnum.PURCHASE.getCode()));
        
        // 申请单生成日期
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        model.addAttribute("applicationDate", LocalDateTime.now().format(dateFormatter));
        
        Context context = new Context();
        context.setVariables(model.asMap());

        // 渲染 HTML 模板
        String html = templateEngine.process("report", context);

        // 转 PDF
        byte[] pdfBytes = PdfGenerator.htmlToPdf(html);
        
        // 保存PDF文件
        String fileName = "application_" + userOrder.getNo() + "_" + System.currentTimeMillis() + ".pdf";
        String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
        FileUtils.createDirectories(storePath);
        String fullPath = storePath + fileName;

        if (OpenPdfUtils.savePdfToFile(pdfBytes, fullPath)) {
            // 生成访问URL
            String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            return fileUploadConfig.getUrlPrefix() + relativePath;
        }
        
        throw new ServiceException("生成申请单PDF失败");
    }
    
    /**
     * 获取项目类型显示文本
     */
    private String getItemTypeText(String type) {
        if (StrUtil.isBlank(type)) {
            return "货物";
        }
        // 如果type是订单明细中的货物类型（如：废纸、废塑料等），直接返回
        // 如果是系统预定义的类型，则进行映射转换
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("goods", "货物");
        typeMap.put("transport", "运输");
        typeMap.put("service", "服务");
        typeMap.put("other", "其他");
        
        // 如果在预定义映射中找到，使用映射值；否则直接返回原值（货物类型）
        return typeMap.getOrDefault(type, type);
    }
    
    /**
     * 获取订单类型显示文本
     */
    private String getOrderTypeText(String type) {
        if (type == null) {
            return "未知类型";
        }
        RecycleOrderTypeEnum orderType = BaseEnum.getByCode(RecycleOrderTypeEnum.class, type);
        return orderType != null ? orderType.getTitle().replace("订单", "") : "未知类型";
    }

    /**
     * 获取流转方向文本
     */
    private String getFlowDirectionText(String orderType) {
        if (orderType == null) {
            return "未知";
        }
        switch (orderType) {
            case "purchase":
                return "采购";
            case "sale":
                return "销售";
            case "process":
                return "加工";
            case "storage":
                return "入库/出库";
            case "transport":
                return "运输";
            case "other":
                return "其他";
            default:
                return "未知";
        }
    }

    /**
     * 生成结算单PDF
     * @param userOrder 用户订单
     * @param userOrderDTO 用户订单DTO
     * @param storageOrder 仓储订单（入库订单）
     * @param goodsTotalAmount 货物总金额
     * @param totalAmount 最终总金额
     * @param ratingCoefficient 评级系数
     * @param ratingCoefficientAmount 评级系数调整金额
     * @param otherAdjustAmount 其他调价金额
     * @return PDF文件访问URL
     * @throws Exception 生成PDF时的异常
     */
    private String generateSettlementPdf(UserOrder userOrder,
                                         Account partyA,
                                         Account partyB,
                                         UserOrderDTO userOrderDTO,
                                        RecycleOrderInfo storageOrder,
                                         BigDecimal goodsTotalAmount,
                                        BigDecimal totalAmount,
                                         BigDecimal ratingCoefficient,
                                        BigDecimal ratingCoefficientAmount,
                                        BigDecimal otherAdjustAmount)  {
        // 创建Model并添加订单数据
        Model model = new ExtendedModelMap();
        
        // 构建订单数据Map
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("no", userOrder.getNo());
        orderData.put("partyAName", userOrder.getPartyAName());
        orderData.put("partyBName", userOrder.getPartyBName());
        orderData.put("contractNo", userOrder.getContractNo());
        orderData.put("deliveryAddress", Optional.ofNullable(userOrderDTO.getDeliveryAddress()).orElse(""));
        orderData.put("warehouseAddress", Optional.ofNullable(userOrderDTO.getWarehouseAddress()).orElse(""));
        orderData.put("processor", Optional.ofNullable(userOrderDTO.getProcessorName()).orElse(""));
        orderData.put("paymentAccount", Optional.ofNullable(userOrderDTO.getPaymentAccount()).orElse(""));
        
        // 订单开始时间和结束时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (userOrder.getCreateTime() != null) {
            LocalDateTime createTime = LocalDateTime.ofInstant(
                userOrder.getCreateTime().toInstant(),
                java.time.ZoneId.systemDefault()
            );
            orderData.put("startTime", createTime.format(formatter));
        } else {
            orderData.put("startTime", "--");
        }
        
        if (userOrder.getSettlementTime() != null) {
            LocalDateTime settlementTime = LocalDateTime.ofInstant(
                userOrder.getSettlementTime().toInstant(),
                java.time.ZoneId.systemDefault()
            );
            orderData.put("endTime", settlementTime.format(formatter));
        } else {
            orderData.put("endTime", "--");
        }
        
        model.addAttribute("orderData", orderData);
        
        // 构建订单明细列表
        List<Map<String, Object>> orderItems = new ArrayList<>();
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmountSum = BigDecimal.ZERO;
        BigDecimal totalUnitPrice = BigDecimal.ZERO;
        if (storageOrder != null && storageOrder.getItems() != null) {
            for (RecycleOrderItem item : storageOrder.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                String goodType = item.getGoodType();
                itemMap.put("type", goodType);
                itemMap.put("typeText", getItemTypeText(goodType));
                itemMap.put("name", item.getGoodName());
                itemMap.put("specification", item.getGoodModel());
                itemMap.put("quantity", Optional.ofNullable(item.getGoodCount()).orElse(0));
                itemMap.put("unitPrice", Optional.ofNullable(item.getGoodPrice()).orElse(BigDecimal.ZERO));
                
                // 计算金额：单价 × 数量
                BigDecimal quantity = BigDecimal.valueOf(Optional.ofNullable(item.getGoodCount()).orElse(0));
                BigDecimal amount = Optional.ofNullable(item.getGoodPrice()).orElse(BigDecimal.ZERO).multiply(quantity);
                itemMap.put("amount", amount);
                
                orderItems.add(itemMap);
                totalQuantity = totalQuantity.add(quantity);
                totalAmountSum = totalAmountSum.add(amount);
                totalUnitPrice = totalUnitPrice.add(Optional.ofNullable(item.getGoodPrice()).orElse(BigDecimal.ZERO));
            }
        }
        
        model.addAttribute("orderItems", orderItems);
        // 交付地址
        model.addAttribute("deliveryAddress", Optional.ofNullable(userOrderDTO.getDeliveryAddress()).orElse("暂无交付地址，请联系客户"));
        // 开始时间、结束时间
        model.addAttribute("startTime", userOrderDTO.getCreateTime() != null ? DateUtil.format(userOrderDTO.getCreateTime(), formatter) : "暂无开始时间，请联系客户");
        model.addAttribute("endTime", userOrderDTO.getSettlementTime() != null ? DateUtil.format(userOrderDTO.getEndTime(), formatter) : "暂无结束时间，请联系客户");
        // 供方公司名称
        model.addAttribute("partyBCompanyName", partyB.getNickname());
        // 供方经办人
        model.addAttribute("partyBProcessor", "暂无");
        // 需求方公司名称
        model.addAttribute("partyACompanyName", partyA.getNickname());
        // 需求方经办人
        model.addAttribute("partyAProcessor", "暂无");
        // 计算调价金额
        // 评级系数
        String accountRatingLevel = userOrder.getAccountRatingLevel();
        model.addAttribute("ratingLevel", accountRatingLevel);
        // 评级系数比例
        model.addAttribute("ratingCoefficient", ratingCoefficient);
        // 评级系数金额
        model.addAttribute("ratingAdjustment", ratingCoefficientAmount);
        // 其他调价
        model.addAttribute("otherAdjustment", otherAdjustAmount);
        // 总调价金额
        BigDecimal totalAdjustment = ratingCoefficientAmount.add(otherAdjustAmount);
        model.addAttribute("totalAdjustment", totalAdjustment);

        // 货物小计统计
        // 总数量
        model.addAttribute("totalQuantity", totalQuantity.intValue());
        // 待结算金额
        model.addAttribute("totalAmount", goodsTotalAmount);
        // 总单价
        model.addAttribute("totalUnitPrice", totalUnitPrice);

        // 结算部分
        // 待结金额
        model.addAttribute("pendingAmount", totalAmount);
        // 综合单价
        model.addAttribute("averageUnitPrice", totalAmount.divide(totalQuantity, 2, RoundingMode.HALF_UP));
        // 金额转中文大写
        String amountToChinese = convertAmountToChinese(totalAmount);
        model.addAttribute("amountToChinese", amountToChinese);
        // 结算单生成日期
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        model.addAttribute("settlementDate", LocalDateTime.now().format(dateFormatter));
        
        Context context = new Context();
        context.setVariables(model.asMap());

        // 渲染 HTML 模板
        String html = templateEngine.process("settlement", context);

        // 转 PDF
        byte[] pdfBytes = PdfGenerator.htmlToPdf(html);
        
        // 保存PDF文件
        String fileName = "settlement_" + userOrder.getNo() + "_" + System.currentTimeMillis() + ".pdf";
        String storePath = FileUtils.generateFilePath(fileUploadConfig.getBasePath(), true);
        FileUtils.createDirectories(storePath);
        String fullPath = storePath + fileName;

        if (OpenPdfUtils.savePdfToFile(pdfBytes, fullPath)) {
            // 生成访问URL
            String relativePath = fullPath.replace(fileUploadConfig.getBasePath(), "").replace("\\", "/");
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            return fileUploadConfig.getUrlPrefix() + relativePath;
        }
        
        throw new ServiceException("生成结算单PDF失败");
    }

    /**
     * 金额转中文大写
     */
    private String convertAmountToChinese(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "零元整";
        }
        
        String[] digits = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[] units = {"", "拾", "佰", "仟"};
        String[] bigUnits = {"", "万", "亿"};
        
        // 分离整数和小数部分
        long integerPart = amount.longValue();
        int decimalPart = amount.subtract(BigDecimal.valueOf(integerPart))
            .multiply(BigDecimal.valueOf(100))
            .intValue();
        
        StringBuilder result = new StringBuilder();
        
        // 处理整数部分
        if (integerPart > 0) {
            String intStr = String.valueOf(integerPart);
            int len = intStr.length();
            
            for (int i = 0; i < len; i++) {
                int digit = Character.getNumericValue(intStr.charAt(i));
                int unitIndex = len - 1 - i;
                int bigUnitIndex = unitIndex / 4;
                int smallUnitIndex = unitIndex % 4;
                
                if (digit != 0) {
                    result.append(digits[digit]).append(units[smallUnitIndex]);
                } else if (result.length() > 0 && !result.toString().endsWith("零")) {
                    result.append("零");
                }
                
                if (smallUnitIndex == 0 && bigUnitIndex > 0 && unitIndex > 0) {
                    result.append(bigUnits[bigUnitIndex]);
                }
            }
            
            result.append("元");
        }
        
        // 处理小数部分
        if (decimalPart > 0) {
            int jiao = decimalPart / 10;
            int fen = decimalPart % 10;
            
            if (jiao > 0) {
                result.append(digits[jiao]).append("角");
            }
            if (fen > 0) {
                result.append(digits[fen]).append("分");
            }
        } else {
            result.append("整");
        }
        
        return result.toString();
    }

    /**
     * 检查用户是否具备创建订单的权限
     * TODO 后续要更新
     * @param userId
     * @param order
     * @return
     */
    public Boolean checkCreateOrderPermission(String userId, WxUserCreateOrderRequest order) {
        AccountCustomer accountCustomer =  accountCustomerService.getByCustomerAccountId(userId);
        if (accountCustomer == null){
            return false;
        }
        // 查询其服务范围
        List<AccountServiceScope> accountServiceScopes = accountServiceScopeService.listByAccountId(accountCustomer.getAccountId());
        // 省
        Set<String> provinceList = new HashSet<>();
        // 市
        Set<String> cityList = new HashSet<>();
        // 区
        Set<String> districtList = new HashSet<>();
        for (AccountServiceScope accountServiceScope : accountServiceScopes) {
            provinceList.add(accountServiceScope.getProvince());
            cityList.add(accountServiceScope.getCity());
            districtList.add(accountServiceScope.getDistrict());
        }
        if (!provinceList.contains(order.getPickupProvince())){
            return false;
        }
        if (!cityList.contains(order.getPickupCity())){
            return false;
        }
        if (!districtList.contains(order.getPickupDistrict())){
            return false;
        }
        return true;
    }
}

