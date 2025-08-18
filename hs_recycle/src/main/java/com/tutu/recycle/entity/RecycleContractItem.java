package com.tutu.recycle.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecycleContractItem extends BaseEntity{
    // 合同ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 合同id
    private String recycleContractId;
    // 货物编号
    private String goodNo;
    // 货物分类
    private String goodType;
    // 货物名称
    private String goodName;
    // 货物型号
    private String goodModel;
    // 货物数量
    private Integer goodCount;
    // 货物单价
    private BigDecimal goodPrice;
    // 货物总价
    private BigDecimal goodTotalPrice;
    // 货物备注
    private String goodRemark;
    
}
