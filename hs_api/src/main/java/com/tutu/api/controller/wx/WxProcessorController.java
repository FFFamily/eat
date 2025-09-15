package com.tutu.api.controller.wx;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Processor;
import com.tutu.user.service.ProcessorService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wx/processor")
public class WxProcessorController {

    @Resource
    private ProcessorService processorService;

    /**
     * 获取当前登录用户对应的所有经办人列表
     * @return 经办人列表
     */
    @GetMapping("/list")
    public BaseResponse<List<Processor>> getProcessorList() {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        // 根据用户ID获取经办人列表
        List<Processor> processors = processorService.getProcessorsByAccountId(userId);
        return BaseResponse.success(processors);
    }
}
