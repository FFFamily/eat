package com.tutu.api.controller.point;

import com.tutu.common.Response.BaseResponse;
import com.tutu.point.entity.PointGlobalConfig;
import com.tutu.point.service.PointGlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 积分全局配置 Controller
 */
@RestController
@RequestMapping("/point/config")
public class PointGlobalConfigController {
    
    @Autowired
    private PointGlobalConfigService pointGlobalConfigService;
    
    /**
     * 获取积分配置
     */
    @GetMapping("/getGlobalConfig")
    public BaseResponse<PointGlobalConfig> getConfig() {
        return BaseResponse.success(pointGlobalConfigService.getConfig());
    }
    
    /**
     * 更新积分比例
     */
    @PutMapping("/updateGlobalConfig")
    public BaseResponse<Void> updatePointRatio(@RequestBody PointGlobalConfig pointGlobalConfig) {
        pointGlobalConfigService.updatePointRatio(pointGlobalConfig);
        return BaseResponse.success();
    }
}

