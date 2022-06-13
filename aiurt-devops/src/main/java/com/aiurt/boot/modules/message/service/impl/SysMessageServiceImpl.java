package com.aiurt.boot.modules.message.service.impl;

import com.aiurt.boot.modules.message.service.ISysMessageService;
import com.aiurt.boot.modules.message.entity.SysMessage;
import com.aiurt.boot.modules.message.mapper.SysMessageMapper;
import com.aiurt.common.system.base.service.impl.JeecgServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
