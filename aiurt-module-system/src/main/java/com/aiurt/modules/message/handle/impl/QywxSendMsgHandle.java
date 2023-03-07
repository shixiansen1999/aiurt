package com.aiurt.modules.message.handle.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.modules.message.handle.ISendMsgHandle;
import com.aiurt.modules.system.entity.SysAnnouncement;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.SysUserMapper;
import com.aiurt.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 发企业微信消息模板
 * @author: jeecg-boot
 */
@Slf4j
@Component("qywxSendMsgHandle")
public class QywxSendMsgHandle implements ISendMsgHandle {

	@Autowired
	private ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
	@Resource
	private SysUserMapper userMapper;

	@Override
	public void sendMsg(String esReceiver, String esTitle, String esContent) {
		log.info("发微信消息模板");
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setToUser(esReceiver);
		messageDTO.setTitle(esTitle);
		messageDTO.setContent(esContent);
		messageDTO.setToAll(false);
		sendMessage(messageDTO);
	}

	@Override
	public void sendMessage(MessageDTO messageDTO) {
		try {
			//wechatEnterpriseService.sendMessage(messageDTO, true);
			SysAnnouncement sysAnnouncement = new SysAnnouncement();
			sysAnnouncement.setId(messageDTO.getMessageId());
			sysAnnouncement.setMsgType(CommonConstant.MSG_TYPE_UESR);
			//接收人账号转换成接收人id
			String toUser = messageDTO.getToUser();
			List<String> userNames = StrUtil.splitTrim(toUser,",");
			LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.in(SysUser::getUsername, userNames);
			List<SysUser> userList = userMapper.selectList(queryWrapper);
			List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
			sysAnnouncement.setUserIds(CollUtil.join(userIds,",")+",");

			sysAnnouncement.setTitile(messageDTO.getTitle());
			String publishingContent = messageDTO.getPublishingContent();
			String content = messageDTO.getContent();
			if (content.endsWith("<br/>")) {
				int i = content.lastIndexOf("<br/>");
				content = content.substring(0, i);
			}
			String s = content.replaceAll("<br/>", "</div><div>");
			s= "<div>"+s+"</div>";

			if (StrUtil.isNotEmpty(publishingContent)) {
				publishingContent = "<div>" + publishingContent + "</div>" + "<br/>";
				sysAnnouncement.setMsgAbstract(publishingContent + s);
			} else {
				sysAnnouncement.setMsgAbstract(s);
			}
			sysAnnouncement.setBusId(messageDTO.getBusKey());
			sysAnnouncement.setBusType(messageDTO.getBusType());
			wechatEnterpriseService.sendTextCardMessage(sysAnnouncement,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
