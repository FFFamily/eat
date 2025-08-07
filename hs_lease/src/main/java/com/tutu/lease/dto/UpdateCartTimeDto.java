package com.tutu.lease.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
public class UpdateCartTimeDto {

    /**
     * 租赁开始时间
     */
    @NotNull(message = "租赁开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaseStartTime;
    
    /**
     * 租赁结束时间
     */
    @NotNull(message = "租赁结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaseEndTime;
}
