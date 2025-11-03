package com.tutu.api.controller.recycle;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.user.UserOrder;
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
        boolean result = userOrderService.deleteUserOrderById(id);
        return BaseResponse.success(result);
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
     * 根据订单状态查询用户订单列表
     * @param status 订单状态
     * @return 用户订单列表
     */
    @GetMapping("/status/{status}")
    public BaseResponse<List<UserOrder>> getUserOrdersByStatus(@PathVariable String status) {
        List<UserOrder> list = userOrderService.getUserOrdersByStatus(status);
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
     * 根据经办人ID查询用户订单列表
     * @param processorId 经办人ID
     * @return 用户订单列表
     */
    @GetMapping("/processor/{processorId}")
    public BaseResponse<List<UserOrder>> getUserOrdersByProcessorId(@PathVariable String processorId) {
        List<UserOrder> list = userOrderService.getUserOrdersByProcessorId(processorId);
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
        queryCondition.setStatus(status);
        queryCondition.setStage(stage);
        queryCondition.setContractId(contractId);
        queryCondition.setContractNo(contractNo);
        queryCondition.setContractPartner(contractPartner);
        queryCondition.setProcessorId(processorId);
        queryCondition.setLocation(location);
        
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
     * 批量删除用户订单
     * @param ids 订单ID集合
     * @return 删除结果
     */
    @DeleteMapping("/batchDelete")
    public BaseResponse<Boolean> batchDeleteUserOrders(@RequestBody List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return BaseResponse.error("订单ID列表不能为空");
        }
        boolean result = userOrderService.batchDeleteUserOrders(ids);
        return BaseResponse.success(result);
    }
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/updateStatus")
    public BaseResponse<Boolean> updateUserOrderStatus(
            @RequestParam String id,
            @RequestParam String status) {
        boolean result = userOrderService.updateUserOrderStatus(id, status);
        if (!result) {
            return BaseResponse.error("更新失败，订单不存在或参数错误");
        }
        return BaseResponse.success(result);
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
     * 流转到下一个状态
     * @param id 订单ID
     * @return 流转结果
     */
    @PutMapping("/toNextStatus")
    public BaseResponse<Boolean> toNextStatus(@RequestParam String id) {
        try {
            boolean result = userOrderService.toNextStatus(id);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 完成订单
     * @param id 订单ID
     * @return 完成结果
     */
    @PutMapping("/complete")
    public BaseResponse<Boolean> completeOrder(@RequestParam String id) {
        try {
            boolean result = userOrderService.completeOrder(id);
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
     * @param id 订单ID
     * @return 结算结果
     */
    @GetMapping("/settle/{id}")
    public BaseResponse<Boolean> settleOrder(@PathVariable String id) {
        boolean result = userOrderService.settleOrder(id);
        return BaseResponse.success(result);
    }
}

