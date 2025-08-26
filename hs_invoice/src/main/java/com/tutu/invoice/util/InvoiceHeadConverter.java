package com.tutu.invoice.util;

import com.tutu.invoice.dto.InvoiceHeadDTO;
import com.tutu.invoice.entity.InvoiceHead;
import org.springframework.beans.BeanUtils;

/**
 * 发票抬头转换工具类
 */
public class InvoiceHeadConverter {
    
    /**
     * DTO转实体
     * @param dto DTO对象
     * @return 实体对象
     */
    public static InvoiceHead dtoToEntity(InvoiceHeadDTO dto) {
        if (dto == null) {
            return null;
        }
        
        InvoiceHead entity = new InvoiceHead();
        BeanUtils.copyProperties(dto, entity);
        
        // 处理枚举类型
        if (dto.getType() != null) {
            entity.setType(dto.getType().name());
        }
        
        return entity;
    }
    
    /**
     * 实体转DTO
     * @param entity 实体对象
     * @return DTO对象
     */
    public static InvoiceHeadDTO entityToDto(InvoiceHead entity) {
        if (entity == null) {
            return null;
        }
        
        InvoiceHeadDTO dto = new InvoiceHeadDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // 处理枚举类型
        if (entity.getType() != null) {
            try {
                dto.setType(com.tutu.invoice.enums.InvoiceHeadType.valueOf(entity.getType()));
            } catch (IllegalArgumentException e) {
                // 如果枚举值不存在，设置为null
                dto.setType(null);
            }
        }
        
        return dto;
    }
} 