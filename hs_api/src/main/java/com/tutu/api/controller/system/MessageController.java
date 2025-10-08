package com.tutu.api.controller.system;

import com.tutu.common.Response.BaseResponse;
import com.tutu.system.entity.Message;
import com.tutu.system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/system/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取当前用户的消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<Message>> getMessagesByUserId(@PathVariable String userId) {
        List<Message> messages = messageService.getMessagesByUserId(userId);
        return BaseResponse.success(messages);
    }

    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    @GetMapping("/unread/count/{userId}")
    public BaseResponse<Long> getUnreadMessageCount(@PathVariable String userId) {
        long count = messageService.getUnreadMessageCount(userId);
        return BaseResponse.success(count);
    }

    /**
     * 将消息标记为已读
     * @param messageId 消息ID
     * @return 操作结果
     */
    @PutMapping("/read/{messageId}")
    public BaseResponse<Boolean> markAsRead(@PathVariable String messageId) {
        boolean result = messageService.markAsRead(messageId);
        return BaseResponse.success(result);
    }
    /**
     * 发送消息
     * @param message 消息实体
     * @return 操作结果
     */
    @PostMapping("/send")
    public BaseResponse<Boolean> sendMessage(@RequestBody Message message) {
        boolean result = messageService.sendMessage(message);
        return BaseResponse.success(result);
    }

    /**
     * 删除消息
     * @param messageId 消息ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{messageId}")
    public BaseResponse<Boolean> deleteMessage(@PathVariable String messageId) {
        boolean result = messageService.removeById(messageId);
        return BaseResponse.success(result);
    }
}