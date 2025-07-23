package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.AdOperationLog;
import com.tutu.admin_user.mapper.AdOperationLogMapper;
import com.tutu.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
public class AdOperationLogService extends ServiceImpl<AdOperationLogMapper, AdOperationLog> {


    
    @Async
    public void recordLog(AdOperationLog operationLog) {
        try {
            save(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    
    public IPage<AdOperationLog> getPageList(int current, int size, String userId, String operationType,
                                             String keyword, Date startTime, Date endTime) {
        Page<AdOperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<AdOperationLog> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AdOperationLog::getIsDeleted, CommonConstant.NO_STR);

        // 用户ID条件
        if (StrUtil.isNotBlank(userId)) {
            queryWrapper.eq(AdOperationLog::getUserId, userId);
        }

        // 操作类型条件
        if (StrUtil.isNotBlank(operationType)) {
            queryWrapper.eq(AdOperationLog::getOperationType, operationType);
        }

        // 关键字搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AdOperationLog::getUsername, keyword)
                    .or()
                    .like(AdOperationLog::getOperationName, keyword)
                    .or()
                    .like(AdOperationLog::getRequestUrl, keyword)
            );
        }

        // 时间范围条件
        if (startTime != null) {
            queryWrapper.ge(AdOperationLog::getCreateTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(AdOperationLog::getCreateTime, endTime);
        }

        queryWrapper.orderByDesc(AdOperationLog::getCreateTime);

        return page(page, queryWrapper);
    }

    
    public List<AdOperationLog> findByUserId(String userId, int limit) {
        LambdaQueryWrapper<AdOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdOperationLog::getUserId, userId)
                .eq(AdOperationLog::getIsDeleted, CommonConstant.NO_STR)
                .orderByDesc(AdOperationLog::getCreateTime)
                .last("LIMIT " + limit);
        return list(queryWrapper);
    }

    
    public List<AdOperationLog> findByOperationType(String operationType, int limit) {
        LambdaQueryWrapper<AdOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdOperationLog::getOperationType, operationType)
                .eq(AdOperationLog::getIsDeleted, CommonConstant.NO_STR)
                .orderByDesc(AdOperationLog::getCreateTime)
                .last("LIMIT " + limit);
        return list(queryWrapper);
    }

    
    public Long countTodayOperations() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        LambdaQueryWrapper<AdOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdOperationLog::getIsDeleted, CommonConstant.NO_STR)
                .ge(AdOperationLog::getCreateTime, startOfDay)
                .le(AdOperationLog::getCreateTime, endOfDay);

        return count(queryWrapper);
    }

    
    public Long countUserTodayOperations(String userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        LambdaQueryWrapper<AdOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdOperationLog::getUserId, userId)
                .eq(AdOperationLog::getIsDeleted, CommonConstant.NO_STR)
                .ge(AdOperationLog::getCreateTime, startOfDay)
                .le(AdOperationLog::getCreateTime, endOfDay);

        return count(queryWrapper);
    }

    
    public void cleanOldLogs(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

        LambdaUpdateWrapper<AdOperationLog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.lt(AdOperationLog::getCreateTime, cutoffDate)
                .set(AdOperationLog::getIsDeleted, CommonConstant.YES_STR)
                .set(AdOperationLog::getUpdateTime, new Date());
        update(updateWrapper);
    }

    
    public boolean batchDeleteLogs(List<String> ids) {
        return removeByIds(ids);
    }
}
