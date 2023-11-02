package com.aiurt.modules.message.service;

import com.aiurt.modules.message.dto.AbstractMessage;

/**
 * @author gaowei
 * @desc 流程统一发送消息
 */
public interface ISysFlowMessageService {

    /**
     * 发送消息
     * @param message
     */
    void sendMessage(AbstractMessage message);
}
