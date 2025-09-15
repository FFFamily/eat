package com.tutu.api.controller.wx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.BusinessScope;
import com.tutu.recycle.service.BusinessScopeService;

import java.util.List;

@RestController
@RequestMapping("/wx/busomessScope")
public class WxBusomessScopeController {

    @Autowired
    private BusinessScopeService businessScopeService;

    /**
     * 获取所有经营范围列表（按排序号排序）
     * @return 经营范围列表
     */
    @GetMapping("/list")
    public BaseResponse<List<BusinessScope>> getBusinessScopeList() {
        List<BusinessScope> businessScopes = businessScopeService.getAllOrdered();
        return BaseResponse.success(businessScopes);
    }
}
