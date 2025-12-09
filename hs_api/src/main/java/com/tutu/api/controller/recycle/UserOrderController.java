package com.tutu.api.controller.recycle;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.DeliveryDTO;
import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.dto.UserOrderInfo;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.request.SaveSupplementMaterialRequest;
import com.tutu.recycle.service.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户订单控制器
 */
@RestController
@RequestMapping("/recycle/user/order")
public class UserOrderController {
    
    @Autowired
    private UserOrderService userOrderService;
    
    /**
     * 创建用户订单
     * @param userOrder 用户订单对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public BaseResponse<UserOrder> createUserOrder(@RequestBody UserOrder userOrder) {
        UserOrder result = userOrderService.createUserOrder(userOrder);
        return BaseResponse.success(result);
    }
    
    /**
     * 根据ID查询用户订单
     * @param id 订单ID
     * @return 用户订单对象
     */
    @GetMapping("/detail/{id}")
    public BaseResponse<UserOrder> getUserOrderById(@PathVariable String id) {
        UserOrder userOrder = userOrderService.getUserOrderById(id);
        if (userOrder == null) {
            return BaseResponse.error("用户订单不存在");
        }
        return BaseResponse.success(userOrder);
    }
    
    /**
     * 查询用户订单信息及其子回收订单
     * 根据回收订单类型，将订单信息映射到不同的字段
     * @param id 用户订单ID
     * @return 用户订单信息（包含子回收订单）
     */
    @GetMapping("/info/{id}")
    public BaseResponse<UserOrderInfo> getUserOrderInfo(@PathVariable String id) {
        try {
            UserOrderInfo userOrderInfo = userOrderService.getUserOrderInfo(id);
            return BaseResponse.success(userOrderInfo);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户订单
     * @param userOrder 用户订单对象
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateUserOrder(@RequestBody UserOrder userOrder) {
        if (userOrder.getId() == null) {
            return BaseResponse.error("订单ID不能为空");
        }
        boolean result = userOrderService.updateUserOrder(userOrder);
        return BaseResponse.success(result);
    }
    
    /**
     * 根据ID删除用户订单
     * @param id 订单ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteUserOrderById(@PathVariable String id) {
        try {
            boolean result = userOrderService.deleteUserOrderById(id);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据订单编号查询用户订单
     * @param no 订单编号
     * @return 用户订单对象
     */
    @GetMapping("/no/{no}")
    public BaseResponse<UserOrder> getUserOrderByNo(@PathVariable String no) {
        UserOrder userOrder = userOrderService.getUserOrderByNo(no);
        if (userOrder == null) {
            return BaseResponse.error("用户订单不存在");
        }
        return BaseResponse.success(userOrder);
    }
    
    /**
     * 根据合同ID查询用户订单列表
     * @param contractId 合同ID
     * @return 用户订单列表
     */
    @GetMapping("/contract/{contractId}")
    public BaseResponse<List<UserOrder>> getUserOrdersByContractId(@PathVariable String contractId) {
        List<UserOrder> list = userOrderService.getUserOrdersByContractId(contractId);
        return BaseResponse.success(list);
    }
    
    /**
     * 根据合同编号查询用户订单列表
     * @param contractNo 合同编号
     * @return 用户订单列表
     */
    @GetMapping("/contractNo/{contractNo}")
    public BaseResponse<List<UserOrder>> getUserOrdersByContractNo(@PathVariable String contractNo) {
        List<UserOrder> list = userOrderService.getUserOrdersByContractNo(contractNo);
        return BaseResponse.success(list);
    }

    /**
     * 根据订单阶段查询用户订单列表
     * @param stage 订单阶段
     * @return 用户订单列表
     */
    @GetMapping("/stage/{stage}")
    public BaseResponse<List<UserOrder>> getUserOrdersByStage(@PathVariable String stage) {
        List<UserOrder> list = userOrderService.getUserOrdersByStage(stage);
        return BaseResponse.success(list);
    }
    
    
    /**
     * 分页查询用户订单（支持多条件查询）
     * @param current 当前页码
     * @param size 每页大小
     * @param no 订单编号（可选）
     * @param status 订单状态（可选）
     * @param stage 订单阶段（可选）
     * @param contractId 合同ID（可选）
     * @param contractNo 合同编号（可选）
     * @param contractPartner 合同合作方（可选）
     * @param processorId 经办人ID（可选）
     * @param location 位置（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<UserOrder>> getUserOrdersPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String no,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String contractId,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String contractPartner,
            @RequestParam(required = false) String processorId,
            @RequestParam(required = false) String location) {
        
        Page<UserOrder> page = new Page<>(current, size);
        UserOrder queryCondition = new UserOrder();
        queryCondition.setNo(no);
        queryCondition.setStage(stage);
        queryCondition.setContractId(contractId);
        queryCondition.setContractNo(contractNo);
        queryCondition.setContractPartner(contractPartner);
//        queryCondition.setProcessorId(processorId);
//        queryCondition.setLocation(location);
        
        IPage<UserOrder> result = userOrderService.getUserOrdersPage(page, queryCondition);
        return BaseResponse.success(result);
    }
    
    /**
     * 查询所有用户订单列表
     * @return 用户订单列表
     */
    @GetMapping("/list")
    public BaseResponse<List<UserOrder>> getAllUserOrders() {
        List<UserOrder> list = userOrderService.getAllUserOrders();
        return BaseResponse.success(list);
    }
    
    /**
     * 更新订单阶段
     * @param id 订单ID
     * @param stage 新阶段
     * @return 更新结果
     */
    @PutMapping("/updateStage")
    public BaseResponse<Boolean> updateUserOrderStage(
            @RequestParam String id,
            @RequestParam String stage) {
        boolean result = userOrderService.updateUserOrderStage(id, stage);
        if (!result) {
            return BaseResponse.error("更新失败，订单不存在或参数错误");
        }
        return BaseResponse.success(result);
    }
    
    /**
     * 流转到下一个阶段
     * @param id 订单ID
     * @return 流转结果
     */
    @PutMapping("/toNextStage")
    public BaseResponse<Boolean> toNextStage(@RequestParam String id) {
        try {
            boolean result = userOrderService.toNextStage(id);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    

    
    /**
     * 验证订单状态流转是否合法
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 验证结果
     */
    @GetMapping("/canTransition")
    public BaseResponse<Boolean> canTransitionStatus(
            @RequestParam String currentStatus,
            @RequestParam String targetStatus) {
        boolean result = userOrderService.canTransitionStatus(currentStatus, targetStatus);
        return BaseResponse.success(result);
    }
    
    /**
     * 结算订单
     * 将订单阶段和状态同时流转到下一个
     * @param userOrderDTO 用户订单DTO
     * @return 结算结果
     */
    @PostMapping("/settle")
    public BaseResponse<Boolean> settleOrder(@RequestBody UserOrderDTO userOrderDTO) {
        boolean result = userOrderService.settleOrder(userOrderDTO,false);
        return BaseResponse.success(result);
    }

    /**
     * 保存当前阶段的子回收订单
     * 不进行阶段流转，仅持久化子订单数据
     * @param userOrderDTO 用户订单DTO
     * @return 保存结果
     */
    @PostMapping("/save")
    public BaseResponse<Boolean> saveSubOrder(@RequestBody UserOrderDTO userOrderDTO) {
        try {
            boolean result = userOrderService.saveSubOrder(userOrderDTO);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 确认结算订单
     * 将订单从待结算阶段流转到完成阶段
     * @param userOrderDTO 用户订单DTO（包含订单ID和调价信息）
     * @return 确认结算结果
     */
    @PostMapping("/confirmSettlement")
    public BaseResponse<Boolean> confirmSettlement(@RequestBody UserOrderDTO userOrderDTO) {
        boolean result = userOrderService.confirmSettlement(userOrderDTO);
        return BaseResponse.success(result);
    }

    /**
     * 交付订单
     * 保存订单的交付信息，并更新交付状态为已交付
     * @param deliveryDTO 交付信息DTO
     * @return 交付结果
     */
    @PostMapping("/delivery")
    public BaseResponse<Boolean> delivery(@RequestBody DeliveryDTO deliveryDTO) {
        try {
            boolean result = userOrderService.delivery(deliveryDTO);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 保存订单补充材料PDF
     * @param request 补充材料请求
     * @return 保存结果
     */
    @PostMapping("/materials")
    public BaseResponse<Boolean> saveSupplementMaterials(@RequestBody SaveSupplementMaterialRequest request) {
        try {
            boolean result = userOrderService.saveSupplementMaterials(request);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}

