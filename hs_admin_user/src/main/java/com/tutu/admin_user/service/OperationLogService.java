package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutu.admin_user.entity.OperationLog;

import java.util.Date;
import java.util.List;

/**
 * 操作日志服务接口
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作日志
     */
    void recordLog(OperationLog operationLog);
    
    /**
     * 分页查询操作日志
     */
    IPage<OperationLog> getPageList(int current, int size, String userId, String operationType, 
                                   String keyword, Date startTime, Date endTime);
    
    /**
     * 根据用户ID查询操作日志
     */
    List<OperationLog> findByUserId(String userId, int limit);
    
    /**
     * 根据操作类型查询操作日志
     */
    List<OperationLog> findByOperationType(String operationType, int limit);
    
    /**
     * 统计今日操作次数
     */
    Long countTodayOperations();
    
    /**
     * 统计用户今日操作次数
     */
    Long countUserTodayOperations(String userId);
    
    /**
     * 清理指定天数前的日志
     */
    int cleanOldLogs(int days);
    
    /**
     * 批量删除操作日志
     */
    boolean batchDeleteLogs(List<String> ids);
}
