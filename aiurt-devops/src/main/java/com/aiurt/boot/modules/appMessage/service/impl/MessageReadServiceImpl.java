package com.aiurt.boot.modules.appMessage.service.impl;

import com.aiurt.boot.modules.appMessage.entity.MessageRead;
import com.aiurt.boot.modules.appMessage.mapper.MessageReadMapper;
import com.aiurt.boot.modules.appMessage.service.IMessageReadService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 消息是否已读
 * @Author: swsc
 * @Date:   2021-10-29
 * @Version: V1.0
 */
@Service
public class MessageReadServiceImpl extends ServiceImpl<MessageReadMapper, MessageRead> implements IMessageReadService {

}
