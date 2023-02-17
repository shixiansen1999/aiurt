package com.aiurt.modules.message.handle.impl;

import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.WebsocketConst;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.message.handle.ISendMsgHandle;
import com.aiurt.modules.message.websocket.WebSocket;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysAnnouncementSend;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.SysAnnouncementMapper;
import com.aiurt.modules.system.mapper.SysAnnouncementSendMapper;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

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
        //原方法不支持 sysBaseApi.sendSysAnnouncement(messageDTO);  有企业微信消息逻辑，

        Map<String,Object> data = messageDTO.getData();
        SysAnnouncement announcement = new SysAnnouncement();
        if(data!=null){
            // 任务节点ID
            Object taskId = data.get(CommonConstant.NOTICE_MSG_BUS_ID);
            if(taskId!=null){
                announcement.setBusId(taskId.toString());
                // announcement.setBusType(Vue3MessageHrefEnum.BPM_TASK.getBusType());
            }
            Object busType = data.get(CommonConstant.NOTICE_MSG_BUS_TYPE);
            announcement.setBusType(busType.toString());
            announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType.toString()).getOpenType());
            announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType.toString()).getOpenPage());
        }
        //摘要信息
        announcement.setMsgAbstract(messageDTO.getMsgAbstract());
        announcement.setPublishingContent(messageDTO.getPublishingContent());
        announcement.setTitile(messageDTO.getTitle());
        announcement.setMsgContent(messageDTO.getContent());
        announcement.setSender(messageDTO.getFromUser());
        if (StringUtils.isNotBlank(messageDTO.getPriority())) {
            announcement.setPriority(messageDTO.getPriority());
        } else {
            announcement.setPriority(com.aiurt.common.constant.CommonConstant.PRIORITY_M);
        }
        announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setMsgCategory(messageDTO.getCategory());
        announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
        announcement.setUserIds(messageDTO.getToUser());
        announcement.setStartTime(messageDTO.getStartTime());
        announcement.setEndTime(messageDTO.getEndTime());
        sysAnnouncementMapper.insert(announcement);
        // 2.插入用户通告阅读标记表记录
        String userId = messageDTO.getToUser();
        String[] userIds = userId.split(",");
        String anntId = announcement.getId();
        for(int i=0;i<userIds.length;i++) {
            if(oConvertUtils.isNotEmpty(userIds[i])) {
                SysUser sysUser = userMapper.getUserByName(userIds[i]);
                if(sysUser==null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, announcement.getId());
                obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }

    }
}
