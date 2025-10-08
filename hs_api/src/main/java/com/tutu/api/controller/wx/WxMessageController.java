package com.tutu.api.controller.wx;

import com.tutu.common.Response.BaseResponse;
import com.tutu.system.entity.Message;
import com.tutu.system.service.MessageService;

import cn.dev33.satoken.stp.StpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/wx/system/message")
public class WxMessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取当前用户的消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    @GetMapping("/list")
    public BaseResponse<List<Message>> getMessagesByUserId() {
        String userId = StpUtil.getLoginIdAsString();
        List<Message> messages = messageService.getMessagesByUserId(userId);
        return BaseResponse.success(messages);
    }

    /**
     * 获取未读消息数量
     * @return 未读消息数量
     */
    @GetMapping("/unread/count")
    public BaseResponse<Long> getUnreadMessageCount() {
        String userId = StpUtil.getLoginIdAsString();
        long count = messageService.getUnreadMessageCount(userId);
        return BaseResponse.success(count);
    }

    /**
     * 将消息标记为已读
     * @param messageId 消息ID
     * @return 操作结果
     */
    @GetMapping("/read/{messageId}")
    public BaseResponse<Boolean> markAsRead(@PathVariable String messageId) {
        boolean result = messageService.markAsRead(messageId);
        return BaseResponse.success(result);
    }
}