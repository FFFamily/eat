package com.tutu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.system.entity.Message;
import com.tutu.system.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息服务实现类
 */
@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {
    /**
     * 发送消息
     * @param message 消息实体
     * @return 操作结果
     */
    public boolean sendMessage(Message message) {
        message.setStatus(CommonConstant.NO_STR);
        return save(message);
    }
    /**
     * 根据用户ID获取消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    public List<Message> getMessagesByUserId(String userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getUserId, userId)
                .eq(Message::getIsDeleted, CommonConstant.NO_STR)
                .orderByDesc(Message::getCreateTime);
        return list(queryWrapper);
    }

    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    public long getUnreadMessageCount(String userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getUserId, userId)
                .eq(Message::getStatus, CommonConstant.NO_STR)
                .eq(Message::getIsDeleted, CommonConstant.NO_STR);
        return count(queryWrapper);
    }

    /**
     * 将消息标记为已读
     * @param messageId 消息ID
     * @return 操作结果
     */
    public boolean markAsRead(String messageId) {
        Message message = getById(messageId);
        if (message != null) {
            message.setStatus("1");
            return updateById(message);
        }
        return false;
    }

    /**
     * 批量将消息标记为已读
     * @param messageIds 消息ID列表
     * @return 操作结果
     */
    public boolean batchMarkAsRead(List<String> messageIds) {
        return lambdaUpdate()
                .in(Message::getId, messageIds)
                .set(Message::getStatus, "1")
                .update();
    }
}