package com.tutu.system.dto;

import com.tutu.system.entity.config.HomeConfig;
import lombok.Data;

import java.util.List;

@Data
public class HomeConfigDto {
    // 主键ID
    private String id;
    // 首页图片URL
    private List<String> homeImg;
}
