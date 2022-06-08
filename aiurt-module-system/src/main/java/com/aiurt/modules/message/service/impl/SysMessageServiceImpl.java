package com.aiurt.modules.message.service.impl;

import com.aiurt.common.system.base.service.impl.JeecgServiceImpl;
import com.aiurt.modules.message.mapper.SysMessageMapper;
import com.aiurt.modules.message.entity.SysMessage;
import com.aiurt.modules.message.service.ISysMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
