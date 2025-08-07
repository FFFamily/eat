package com.tutu.lease.dto;

import com.tutu.lease.entity.LeaseGood;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaseGoodDto extends LeaseGood {
    // 分类名称
    private String typeName;
}
