package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

// 合同子表
@Data
public class SysContractItem {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 合同ID
    private String contractId;
    /** 回收货物信息 */
    // 回收货物ID
    private String recycleGoodId;
    // 回收货物名称
    private String recycleGoodName;
    // 回收货物规格型号
    private String recycleGoodSpecificationModel;
    // 回收货物运输模式
    private String recycleGoodTransportMode;
    // 货物金额
    private BigDecimal recycleGoodSubtotal;
    /** 租赁设备信息 */
    // 租赁设备ID
    private String leaseGoodId;
    // 租赁设备名称
    private String leaseGoodName;
    // 租赁设备押金
    private BigDecimal leaseGoodDeposit;
    // 租赁设备金额
    private BigDecimal leaseGoodSubtotal;
    // 租赁设备安装日期
    private Date leaseGoodInstallDate;

}
