package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.entity.AdOperationLog;
import com.tutu.admin_user.service.AdOperationLogService;
import com.tutu.common.Response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/admin/operation-log")
public class OperationLogController {
    
    @Autowired
    private AdOperationLogService adOperationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
//    @LogAnnotation(value = "查询操作日志", type = OperationType.SELECT)
    public BaseResponse<IPage<AdOperationLog>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        
        IPage<AdOperationLog> page = adOperationLogService.getPageList(current, size, userId, operationType, keyword, startTime, endTime);
        return BaseResponse.success(page);
    }
    
    /**
     * 根据ID查询操作日志详情
     */
    @GetMapping("/{id}")
//    @LogAnnotation(value = "查询操作日志详情", type = OperationType.SELECT)
    public BaseResponse<AdOperationLog> getById(@PathVariable String id) {
        AdOperationLog adOperationLog = adOperationLogService.getById(id);
        if (adOperationLog == null) {
            return BaseResponse.error("操作日志不存在");
        }
        return BaseResponse.success(adOperationLog);
    }
    
    /**
     * 根据用户ID查询操作日志
     */
    @GetMapping("/user/{userId}")
//    @LogAnnotation(value = "查询用户操作日志", type = OperationType.SELECT)
    public BaseResponse<List<AdOperationLog>> findByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<AdOperationLog> logs = adOperationLogService.findByUserId(userId, limit);
        return BaseResponse.success(logs);
    }
    
    /**
     * 根据操作类型查询操作日志
     */
    @GetMapping("/type/{operationType}")
//    @LogAnnotation(value = "查询操作类型日志", type = OperationType.SELECT)
    public BaseResponse<List<AdOperationLog>> findByOperationType(
            @PathVariable String operationType,
            @RequestParam(defaultValue = "10") int limit) {
        List<AdOperationLog> logs = adOperationLogService.findByOperationType(operationType, limit);
        return BaseResponse.success(logs);
    }
    
    /**
     * 获取操作统计信息
     */
    @GetMapping("/statistics")
//    @LogAnnotation(value = "查询操作统计", type = OperationType.SELECT)
    public BaseResponse<Map<String, Object>> getStatistics(@RequestParam(required = false) String userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 今日总操作次数
        Long todayTotal = adOperationLogService.countTodayOperations();
        statistics.put("todayTotal", todayTotal);
        
        // 用户今日操作次数
        if (userId != null) {
            Long userTodayTotal = adOperationLogService.countUserTodayOperations(userId);
            statistics.put("userTodayTotal", userTodayTotal);
        }
        
        return BaseResponse.success(statistics);
    }
    
    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
//    @LogAnnotation(value = "删除操作日志", type = OperationType.DELETE)
    public BaseResponse<String> deleteLog(@PathVariable String id) {
        try {
            boolean result = adOperationLogService.removeById(id);
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
//    @LogAnnotation(value = "批量删除操作日志", type = OperationType.DELETE)
    public BaseResponse<String> batchDeleteLogs(@RequestBody List<String> ids) {
        try {
            boolean result = adOperationLogService.batchDeleteLogs(ids);
            if (result) {
                return BaseResponse.success("批量删除成功");
            } else {
                return BaseResponse.error("批量删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 清理指定天数前的日志
     */
    @DeleteMapping("/clean")
//    @LogAnnotation(value = "清理历史日志", type = OperationType.DELETE)
    public BaseResponse<String> cleanOldLogs(@RequestParam int days) {
        try {
            if (days < 7) {
                return BaseResponse.error("清理天数不能少于7天");
            }
            int count = adOperationLogService.cleanOldLogs(days);
            return BaseResponse.success("清理完成，共清理 " + count + " 条记录");
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}
