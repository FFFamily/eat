package com.tutu.api.controller.wx;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Address;
import com.tutu.user.service.AddressService;

import java.util.List;

import cn.dev33.satoken.stp.StpUtil;

import jakarta.annotation.Resource;



@RestController
@RequestMapping("/wx/address")
public class WxAddressController {
    @Resource
    private AddressService addressService;
    // 获取地址列表
    @GetMapping("/current/list")
    public BaseResponse<List<Address>> list() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Address> list = addressService.findByAccountId(userId);
        return BaseResponse.success(list);
    }
}
