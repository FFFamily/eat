package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Processor;
import com.tutu.user.mapper.ProcessorMapper;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 经办人 Service 实现类
 */
@Service
public class ProcessorService extends ServiceImpl<ProcessorMapper, Processor>{
    /**
     * 分页查询经办人
     * @param page 页码
     * @param size 每页条数
     * @param processor 经办人
     * @return
     */
    public List<Processor> findPage(Integer page, Integer size, Processor processor) {
        return this.baseMapper.findPage(page, size, processor);
    }

    /**
     * 查询经办人数量
     * @param processor 经办人
     * @return
     */
    public long findPageCount(Processor processor) {
        return this.baseMapper.findPageCount(processor);
    }

    /**
     * 根据账号ID获取经办人列表
     * @param accountId 账号ID
     * @return 经办人列表
     */
    public List<Processor> getProcessorsByAccountId(String accountId) {
        LambdaQueryWrapper<Processor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Processor::getAccountId, accountId);
        return this.list(queryWrapper);
    }
}