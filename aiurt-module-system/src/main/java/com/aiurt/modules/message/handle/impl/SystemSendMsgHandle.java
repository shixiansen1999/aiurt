package com.aiurt.modules.message.handle.impl;

import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.WebsocketConst;
import com.aiurt.modules.message.handle.ISendMsgHandle;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.SysAnnouncementMapper;
import com.aiurt.modules.system.mapper.SysAnnouncementSendMapper;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* @Description: 发送系统消息
* @Author: wangshuai
* @Date: 2022年3月22日 18:48:20
*/
@Component("systemSendMsgHandle")
public class SystemSendMsgHandle implements ISendMsgHandle {

    public static final String FROM_USER="system";

    @Resource
    private SysAnnouncementMapper sysAnnouncementMapper;

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysAnnouncementSendMapper sysAnnouncementSendMapper;

    @Resource
    private WebSocket webSocket;

    /**
     * 该方法会发送3种消息：系统消息、企业微信 钉钉
     * @param esReceiver 发送人
     * @param esTitle 标题
     * @param esContent 内容
     */
    @Override
    public void sendMsg(String esReceiver, String esTitle, String esContent) {
        if(oConvertUtils.isEmpty(esReceiver)){
            throw  new JeecgBootException("被发送人不能为空");
        }
        ISysBaseAPI sysBaseApi = SpringContextUtils.getBean(ISysBaseAPI.class);
        MessageDTO messageDTO = new MessageDTO(FROM_USER,esReceiver,esTitle,esContent);
        sysBaseApi.sendSysAnnouncement(messageDTO);
    }

    /**
     * 仅发送系统消息
     * @param messageDTO
     */
    @Override
    public void sendMessage(MessageDTO messageDTO) {
        String userId = messageDTO.getToUser();
        String[] userIds = userId.split(",");
        String anntId = messageDTO.getMessageId();
        for(int i=0;i<userIds.length;i++) {
            if(oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if(sysUser==null) {
                    continue;
                }
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, messageDTO.getMessageId());
                obj.put(WebsocketConst.MSG_TXT, messageDTO.getTitle());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }

    }
}
