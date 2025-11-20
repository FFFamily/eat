package com.tutu.recycle.service;

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
import com.tutu.recycle.enums.DeliveryStatusEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.enums.UserOrderStageEnum;
import com.tutu.recycle.enums.UserOrderStatusEnum;
import com.tutu.recycle.mapper.UserOrderMapper;
import com.tutu.recycle.response.SortingDeliveryHallResponse;
import com.tutu.recycle.response.WxTransportOrderListResponse;
import com.tutu.recycle.schema.RecycleOrderInfo;
import com.tutu.recycle.utils.UserOrderNoGenerator;
import com.tutu.point.entity.AccountPointDetail;
import com.tutu.point.entity.PointGlobalConfig;
import com.tutu.point.enums.PointChangeDirectionEnum;
import com.tutu.point.enums.PointChangeTypeEnum;
import com.tutu.point.service.AccountPointDetailService;
import com.tutu.point.service.PointGlobalConfigService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tutu.user.service.ProcessorService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户订单服务实现类
 */
@Service
public class UserOrderService extends ServiceImpl<UserOrderMapper, UserOrder> {
    
    @Resource
    private ProcessorService processorService;
    
    @Resource
    private RecycleOrderService recycleOrderService;
    
    @Resource
    private RecycleContractService recycleContractService;
    
    @Resource
    private PointGlobalConfigService pointGlobalConfigService;
    
    @Resource
    private AccountPointDetailService accountPointDetailService;

    /**
     * 获取当前登录用户作为合作方的订单列表
     * @param order
     * @return
     */
    public List<UserOrder> getUserOrderList(UserOrder order) {
        LambdaQueryWrapper<UserOrder> wrapper = new LambdaQueryWrapper<>();
        // 合作方
        wrapper.eq(UserOrder::getContractPartner, order.getContractPartner());
        // 阶段
        Optional.ofNullable(order.getStage()).ifPresent(stage -> wrapper.eq(UserOrder::getStage, stage));
        wrapper.orderByDesc(UserOrder::getCreateTime);
        List<UserOrder> list = list(wrapper);
        return list;
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
        save(userOrder);
        // 同步创建采购类型的回收订单
        // recycleOrderService.createPurchaseOrderFromUserOrder(userOrder);
        return userOrder;
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
     * 完成订单
     * 将订单状态设置为已完成，阶段设置为入库
     * @param id 订单ID
     * @return 是否完成成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrder(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ServiceException("订单ID不能为空");
        }
        
        UserOrder userOrder = getById(id);
        if (userOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 设置为完成状态和入库阶段
        userOrder.setStage(UserOrderStageEnum.WAREHOUSING.getCode());
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
        updateById(userOrder);
        // 如果不是完成阶段，则创建对应阶段的回收订单
        if (!nextStage.isLastStage()) {
            recycleOrderService.createRecycleOrderFromUserOrderByStage(userOrderRequest,userOrder, currentStage,true);
            if (isCreateNextOrder) {
                // 创建下一个阶段的订单
                recycleOrderService.createRecycleOrderFromUserOrderByStage(new UserOrderDTO(), userOrder, nextStage,false);
            }
        }

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
        if (!UserOrderStageEnum.PENDING_SETTLEMENT.getCode().equals(userOrder.getStage())) {
            throw new ServiceException("订单当前阶段不是待结算，无法确认结算");
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
                BigDecimal weight = Optional.ofNullable(item.getGoodWeight()).orElse(BigDecimal.ZERO);
                goodsTotalAmount = goodsTotalAmount.add(price.multiply(count).multiply(weight));
            }
        }
        //   - 货物总金额 = Σ(单价 × 数量 × 重量)
        //  - 最终总金额 = 货物总金额 × (1 + 评级系数) + 其他调价
        BigDecimal ratingCoefficient = Optional.ofNullable(userOrderDTO.getAccountCoefficient()).orElse(BigDecimal.ZERO);
        BigDecimal otherAdjustAmount = Optional.ofNullable(userOrderDTO.getOtherAdjustAmount()).orElse(BigDecimal.ZERO);
        BigDecimal totalAmount = goodsTotalAmount.multiply(BigDecimal.ONE.add(ratingCoefficient)).add(otherAdjustAmount);
        // 更新金额和调价信息
        userOrder.setGoodsTotalAmount(goodsTotalAmount);
        userOrder.setTotalAmount(totalAmount);
        // 更新结算时间
        userOrder.setSettlementTime(new Date());
        // 流转到完成阶段
        userOrder.setStage(UserOrderStageEnum.COMPLETED.getCode());
        // 根据合同受益人发放积分
        distributeSettlementPoints(userOrder);
        return updateById(userOrder);
    }

    /**
     * 交付订单
     * 保存订单的交付信息，并更新交付状态为已交付
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
        // 更新交付状态为已交付
        userOrder.setDeliveryStatus(DeliveryStatusEnum.DELIVERED.getCode());
        return updateById(userOrder);
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
                .collect(Collectors.toList());
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
}

