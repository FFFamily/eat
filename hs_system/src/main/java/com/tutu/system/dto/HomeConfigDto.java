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
    // 商城分类配置
    private List<String> mallType;
    // 商城分类列表
    private List<MallType> mallTypeList;

    @Data
    public static class MallType {
        private String id;
        private String name;
    }
}
