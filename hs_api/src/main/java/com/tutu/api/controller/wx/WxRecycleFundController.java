package com.tutu.api.controller.wx;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.request.FundRecordQueryRequest;
import com.tutu.recycle.service.RecycleFundService;

import java.util.List;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx/recycle/fund")
public class WxRecycleFundController {
    
    @Resource
    private RecycleFundService recycleFundService;

    /**
     * 获取当前登录用户作为合作方的走款记录列表
     * @param request 查询请求，包含status参数，status可为空或"all"表示查询所有状态
     * @return 走款记录列表
     */
    @PostMapping("/current/list")
    public BaseResponse<List<RecycleFund>> getPartnerFundList(@RequestBody FundRecordQueryRequest request) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 查询合作方走款记录列表
        List<RecycleFund> fundList = recycleFundService.getPartnerFundList(userId, request.getStatus());
        return BaseResponse.success(fundList);
    }
}
