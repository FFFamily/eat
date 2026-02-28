package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.SysAuditLog;
import com.tutu.admin_user.mapper.SysAuditLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysAuditLogService extends ServiceImpl<SysAuditLogMapper, SysAuditLog> {

    /**
     * 审计写入使用独立事务，避免主业务回滚导致审计丢失。
     * 写入异常由调用方处理（通常应吞掉，避免影响主流程）。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveInNewTx(SysAuditLog log) {
        save(log);
    }
}
