package com.tutu.system.entity.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeConfig extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 首页图片
    private String homeImg;
}
