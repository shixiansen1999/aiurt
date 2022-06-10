package com.aiurt.boot.modules.message.service.impl;

import com.swsc.copsms.modules.message.service.ISysMessageService;
import com.swsc.copsms.common.system.base.service.impl.BaseServiceImpl;
import com.swsc.copsms.modules.message.entity.SysMessage;
import com.swsc.copsms.modules.message.mapper.SysMessageMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends BaseServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
