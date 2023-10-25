package com.aiurt.modules.message.service;

import com.aiurt.modules.message.dto.MessageContext;

public interface ISysMessageService {

    void sendMessage(MessageContext messageContext);
}
