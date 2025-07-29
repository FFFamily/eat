package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysContract extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 合同名称
    private String name;
    // 合同识别号
    private String recognitionCode;
    // 合同编码
    private String code;
}
