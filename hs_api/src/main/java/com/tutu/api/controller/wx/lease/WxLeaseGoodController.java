package com.tutu.api.controller.wx.lease;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.entity.LeaseGood;
import com.tutu.lease.enums.LeaseGoodStatusEnum;
import com.tutu.lease.service.LeaseGoodService;

import cn.hutool.core.util.StrUtil;

@RestController
@RequestMapping("/wx/lease/good")
public class WxLeaseGoodController {

    @Autowired
    private LeaseGoodService leaseGoodService;

      /**
     * 根据类型查询
     * @return 列表
     */
    @PostMapping("/type")
    public BaseResponse<List<LeaseGood>> pageByType(@RequestBody HashMap<String,String> data) {
        String type = data.get("type");
        LambdaQueryWrapper<LeaseGood> queryWrapper = new LambdaQueryWrapper<LeaseGood>()
                .eq(LeaseGood::getStatus, LeaseGoodStatusEnum.UP.getCode())
                .orderByDesc(LeaseGood::getCreateTime);
        if (StrUtil.isNotBlank(type) && ! "all".equals(type)){
            queryWrapper.eq(LeaseGood::getType, type);
        }
        return BaseResponse.success(leaseGoodService.list(queryWrapper));
    }
}
