package com.tutu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Processor;
import com.tutu.user.mapper.ProcessorMapper;
import com.tutu.user.service.ProcessorService;
import org.springframework.stereotype.Service;

/**
 * 经办人 Service 实现类
 */
@Service
public class ProcessorServiceImpl extends ServiceImpl<ProcessorMapper, Processor> implements ProcessorService {
}