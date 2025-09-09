package com.tutu.api.controller.wx;

import java.util.List;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.service.RecycleContractService;

import cn.dev33.satoken.stp.StpUtil;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx/contract")
public class WxContractController {
    @Resource
    private RecycleContractService recycleContractService;
    // 获取对应合作方合同列表
    @GetMapping("/current/list")
    public BaseResponse<List<RecycleContract>> list() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<RecycleContract> list = recycleContractService.findByPartner(userId);
        return BaseResponse.success(list);
    }
}
