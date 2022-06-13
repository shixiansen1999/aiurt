package com.aiurt.boot.modules.appMessage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.entity.MessageRead;
import com.aiurt.boot.modules.appMessage.mapper.MessageMapper;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.param.MessagePageParam;
import com.aiurt.boot.modules.appMessage.service.IMessageReadService;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.appMessage.vo.MessageStatusVO;
import com.aiurt.boot.modules.appMessage.vo.MessageUserVO;
import com.aiurt.boot.modules.message.websocket.WebSocket;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date: 2021-10-29
 * @Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

	private final IMessageReadService messageReadService;

	private final WebSocket webSocket;

	@Override
	public IPage<MessageStatusVO> getMessagePage(MessagePageParam param) {
		LocalDate now = LocalDate.now();
		if (param.getStartTime() == null) {
			param.setStartTime(now.plusDays(-30).atTime(0, 0, 0));
		}
		if (param.getEndTime() == null) {
			param.setEndTime(now.atTime(23, 59, 59));
		}

		return this.baseMapper.selectMessagePage(new Page<>(param.getPageNo(), param.getPageSize()), param);
	}

	@Override
	public List<Message> getNewMessage(MessagePageParam param) {
		LocalDate now = LocalDate.now();
		if (param.getStartTime() == null) {
			param.setStartTime(LocalDate.of(now.getYear(), now.getMonthValue(), 1).atTime(0, 0, 0));
		}
		if (param.getEndTime() == null) {
			param.setEndTime(now.atTime(23, 59, 59));
		}
		List<Message> list = this.baseMapper.selectMessageList(param);


		return list;
	}

	@Override
	public IPage<MessageUserVO> queryPageList(Page<MessageUserVO> page, MessageUserVO message) {
		IPage<MessageUserVO> pages = this.baseMapper.selectUserMessagePage(page, message);

		return pages;
	}

	@Override
	public boolean addMessage(MessageAddParam param) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (param == null || user == null || CollectionUtils.isEmpty(param.getUserIds()) || param.getMessage() == null) {
			return false;
		}
		Message message = param.getMessage();
		message.setId(null);

		int insert = this.baseMapper.insert(message);
		if (insert < 1) {
			return false;
		}

		//插入关联用户
		List<MessageRead> list = new ArrayList<>();
		for (String userId : param.getUserIds()) {
			MessageRead read = new MessageRead();
			read.setMessageId(message.getId())
					.setReadFlag(0)
					.setDelFlag(0)
					.setCreateTime(message.getCreateTime())
					.setUpdateTime(message.getCreateTime())
					.setStaffId(userId)
					.setStaffName(user.getRealname());
			list.add(read);
		}
		boolean b = messageReadService.saveBatch(list);
		if (!b){
			return false;
		}

		JSONObject obj = new JSONObject();
		obj.put("title", message.getTitle());
		obj.put("msg", message.getContent());

		for (String userId : param.getUserIds()) {
			webSocket.sendOneMessage(userId, obj.toJSONString());
		}
		return true;
	}
}
