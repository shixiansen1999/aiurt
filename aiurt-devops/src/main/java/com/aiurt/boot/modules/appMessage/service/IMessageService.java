package com.aiurt.boot.modules.appMessage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.param.MessagePageParam;
import com.aiurt.boot.modules.appMessage.vo.MessageStatusVO;
import com.aiurt.boot.modules.appMessage.vo.MessageUserVO;

import java.util.List;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date:   2021-10-29
 * @Version: V1.0
 */
public interface IMessageService extends IService<Message> {

	/**
	 * 查询消息分页
	 *
	 * @param param 参数
	 * @return {@code IPage<MessageStatusVO>}
	 */
	IPage<MessageStatusVO> getMessagePage(MessagePageParam param);

	/**
	 * 获取新消息
	 *
	 * @param param 参数
	 * @return {@code List<Message>}
	 */
	List<Message> getNewMessage(MessagePageParam param);

	/**
	 * 查询消息列表
	 *
	 * @param page    页面
	 * @param message 消息
	 * @return {@code IPage<MessageUserVO>}
	 */
	IPage<MessageUserVO> queryPageList(Page<MessageUserVO> page, MessageUserVO message);


	/**
	 * 添加消息
	 *
	 * @param param 参数
	 * @return boolean
	 */
	boolean addMessage(MessageAddParam param);
}
