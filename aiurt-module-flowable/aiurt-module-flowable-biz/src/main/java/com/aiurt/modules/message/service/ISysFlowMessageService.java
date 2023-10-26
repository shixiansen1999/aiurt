package com.aiurt.modules.message.service;

import com.aiurt.modules.message.dto.AbstractMessage;

public interface ISysFlowMessageService {

    void sendMessage(AbstractMessage message);
}
