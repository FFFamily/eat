package com.tutu.invoice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.invoice.entity.InvoiceHead;
import com.tutu.invoice.mapper.InvoiceHeadMapper;
import org.springframework.stereotype.Service;

@Service
public class InvoiceHeadService extends ServiceImpl<InvoiceHeadMapper, InvoiceHead> {
}
