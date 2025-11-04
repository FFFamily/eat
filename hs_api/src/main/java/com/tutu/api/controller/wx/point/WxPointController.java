//package com.tutu.api.controller.wx.point;
//
//import cn.dev33.satoken.stp.StpUtil;
//import com.tutu.point.entity.AccountPointDetail;
//import com.tutu.point.service.AccountPointDetailService;
//import jakarta.annotation.Resource;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.tutu.common.Response.BaseResponse;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/wx/point")
//public class WxPointController {
//    @Resource
//    private AccountPointDetailService accountPointService;
//    @Resource
//    private AccountPointDetailService accountPointDetailService;
//    /**
//     * 获取当前登录人的积分
//     */
//    @GetMapping("/get")
//    public BaseResponse<Long> getPoint() {
//        String accountId = StpUtil.getLoginIdAsString();
//        long point = accountPointService.getPointByAccountId(accountId);
//        return BaseResponse.success(point);
//    }
//    /**
//     * 获取当前登录人的积分明细
//     */
//    @GetMapping("/detail")
//    public BaseResponse<List<AccountPointDetail>> getPointDetail() {
//        String accountId = StpUtil.getLoginIdAsString();
//        List<AccountPointDetail> pointDetail = accountPointDetailService.getPointDetailByAccountId(accountId);
//        return BaseResponse.success(pointDetail);
//    }
//
//}
