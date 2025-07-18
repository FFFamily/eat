package com.tutu.admin_user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.OperationLog;
import com.tutu.admin_user.mapper.OperationLogMapper;
import com.tutu.admin_user.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Override
    @Async
    public void recordLog(OperationLog operationLog) {
        try {
            save(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    @Override
    public IPage<OperationLog> getPageList(int current, int size, String userId, String operationType, 
                                          String keyword, Date startTime, Date endTime) {
        Page<OperationLog> page = new Page<>(current, size);
        return baseMapper.selectPageByCondition(page, userId, operationType, keyword, startTime, endTime);
    }
    
    @Override
    public List<OperationLog> findByUserId(String userId, int limit) {
        return baseMapper.findByUserId(userId, limit);
    }
    
    @Override
    public List<OperationLog> findByOperationType(String operationType, int limit) {
        return baseMapper.findByOperationType(operationType, limit);
    }
    
    @Override
    public Long countTodayOperations() {
        return baseMapper.countTodayOperations();
    }
    
    @Override
    public Long countUserTodayOperations(String userId) {
        return baseMapper.countUserTodayOperations(userId);
    }
    
    @Override
    public int cleanOldLogs(int days) {
        return baseMapper.cleanOldLogs(days);
    }
    
    @Override
    public boolean batchDeleteLogs(List<String> ids) {
        return removeByIds(ids);
    }
}
