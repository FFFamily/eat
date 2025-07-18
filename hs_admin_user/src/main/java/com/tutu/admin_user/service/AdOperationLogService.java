package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.AdOperationLog;
import com.tutu.admin_user.mapper.AdOperationLogMapper;
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
public class AdOperationLogService extends ServiceImpl<AdOperationLogMapper, AdOperationLog> {
    
    
    @Async
    public void recordLog(AdOperationLog adOperationLog) {
        try {
            save(adOperationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    
    public IPage<AdOperationLog> getPageList(int current, int size, String userId, String operationType,
                                             String keyword, Date startTime, Date endTime) {
        Page<AdOperationLog> page = new Page<>(current, size);
        return baseMapper.selectPageByCondition(page, userId, operationType, keyword, startTime, endTime);
    }
    
    
    public List<AdOperationLog> findByUserId(String userId, int limit) {
        return baseMapper.findByUserId(userId, limit);
    }
    
    
    public List<AdOperationLog> findByOperationType(String operationType, int limit) {
        return baseMapper.findByOperationType(operationType, limit);
    }
    
    
    public Long countTodayOperations() {
        return baseMapper.countTodayOperations();
    }
    
    
    public Long countUserTodayOperations(String userId) {
        return baseMapper.countUserTodayOperations(userId);
    }
    
    
    public int cleanOldLogs(int days) {
        return baseMapper.cleanOldLogs(days);
    }
    
    
    public boolean batchDeleteLogs(List<String> ids) {
        return removeByIds(ids);
    }
}
