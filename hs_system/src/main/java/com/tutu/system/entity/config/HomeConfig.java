package com.tutu.system.entity.config;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import com.tutu.system.dto.HomeConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

/**
 * 首页配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("home_config")
public class HomeConfig extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 首页图片URL
     */
    private String homeImg;
    /**
     * 商城类型
     */
    private String mallType;

    public HomeConfigDto covertToDto() {
        HomeConfigDto dto = new HomeConfigDto();
        BeanUtil.copyProperties(this, dto);
        Optional.ofNullable(homeImg).ifPresent(img -> dto.setHomeImg(List.of(img.split("、"))));
        Optional.ofNullable(mallType).ifPresent(type -> dto.setMallType(List.of(type.split(","))));
        return dto;
    }
}
