package com.aiurt.modules.message.handle.impl;

import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.message.handle.ISendMsgHandle;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.stereotype.Component;

/**
* @Description: 发送系统消息
* @Author: wangshuai
* @Date: 2022年3月22日 18:48:20
*/
@Component("systemSendMsgHandle")
public class SystemSendMsgHandle implements ISendMsgHandle {

    public static final String FROM_USER="system";

    @Override
    public void sendMsg(String esReceiver, String esTitle, String esContent) {
        if(oConvertUtils.isEmpty(esReceiver)){
            throw  new AiurtBootException("被发送人不能为空");
        }
        ISysBaseAPI sysBaseApi = SpringContextUtils.getBean(ISysBaseAPI.class);
        MessageDTO messageDTO = new MessageDTO(FROM_USER,esReceiver,esTitle,esContent);
        sysBaseApi.sendSysAnnouncement(messageDTO);
    }
}
