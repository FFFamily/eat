package com.tutu.api.controller.wx;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.UserYearlyStatsDto;
import com.tutu.recycle.service.UserYearlyStatsService;

import cn.dev33.satoken.stp.StpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/data")
public class WxDataController {

    @Autowired
    private UserYearlyStatsService userYearlyStatsService;

    /**
     * 获取用户年度统计汇总
     * @param userId 用户ID
     * @param year 统计年份，默认为当前年份
     * @return 年度统计汇总信息
     */
    @GetMapping("/yearly-stats")
    public BaseResponse<UserYearlyStatsDto> getUserYearlyStats(
            @RequestParam(required = false) Integer year) {
        String userId = StpUtil.getLoginIdAsString();
        UserYearlyStatsDto stats = userYearlyStatsService.getUserYearlyStats(userId, year);
        return BaseResponse.success(stats);
    }
}
