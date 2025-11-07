package com.tutu.api.controller.wx;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Address;
import com.tutu.user.service.AddressService;
import com.tutu.system.service.CityService;

import java.util.List;

import com.tutu.system.entity.City;
import cn.dev33.satoken.stp.StpUtil;

import jakarta.annotation.Resource;



@RestController
@RequestMapping("/wx/address")
public class WxAddressController {
    @Resource
    private AddressService addressService;
    @Resource
    private CityService cityService;

 /**
     * 根据父级编码获取子级城市
     */
    @GetMapping("/children/{pCode}")
    public BaseResponse<List<City>> getChildrenByParentCode(@PathVariable String pCode) {
        return BaseResponse.success(cityService.getByParentCode(pCode));
    }

    // 获取地址列表
    @GetMapping("/current/list")
    public BaseResponse<List<Address>> list() {
        String userId = StpUtil.getLoginIdAsString();
        List<Address> list = addressService.findByAccountId(userId);
        return BaseResponse.success(list);
    }
    
    // 根据ID获取地址信息
    @GetMapping("/getById/{addressId}")
    public BaseResponse<Address> getById(@PathVariable String addressId) {
        Long userId = StpUtil.getLoginIdAsLong();
        Address address = addressService.getById(addressId);
        
        // 验证地址是否属于当前用户
        if (address == null || !address.getAccountId().equals(userId.toString())) {
            return BaseResponse.error("地址不存在或无权限访问");
        }
        
        return BaseResponse.success(address);
    }
    
    // 新增地址
    @PostMapping("/create")
    public BaseResponse<Boolean> create(@RequestBody Address address) {
        // 设置当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        address.setAccountId(userId.toString());
        addressService.createAddress(address);
        return BaseResponse.success();
    }
    
    // 设置默认地址
    @PutMapping("/set-default/{addressId}")
    public BaseResponse<Boolean> setDefaultAddress(@PathVariable String addressId) {
        boolean result = addressService.setDefaultAddress(addressId);
        return BaseResponse.success(result);
    }
}
