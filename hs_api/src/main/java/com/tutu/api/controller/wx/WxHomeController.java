package com.tutu.api.controller.wx;

import org.springframework.web.bind.annotation.GetMapping;

import com.tutu.common.Response.BaseResponse;
import com.tutu.system.dto.HomeConfigDto;
import com.tutu.system.service.HomeConfigService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/wx/home")
public class WxHomeController {

    @Resource
    private HomeConfigService homeConfigService;
    


    /**
     * 获取微信端首页配置信息
     * @return 首页配置信息
     */
    @GetMapping("/config")
    public BaseResponse<HomeConfigDto> getWxConfig() {
        HomeConfigDto homeConfigDto = homeConfigService.getAllHomeConfigs().getFirst().covertToDto();
        return BaseResponse.success(homeConfigDto);
    }

}
