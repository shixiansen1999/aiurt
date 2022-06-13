package com.aiurt.boot.modules.appMessage.utils;

import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.entity.MessageRead;
import com.aiurt.boot.modules.appMessage.mapper.MessageMapper;
import com.aiurt.boot.modules.appMessage.service.IMessageReadService;
import com.aiurt.boot.modules.appMessage.vo.SendMessageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 信息工具类
 *
 * @description: SendMessageUtils
 * @author: Mr.zhao
 * @date: 2021/11/17 9:37
 */
@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AppMessageUtils {

	private final MessageMapper messageMapper;

	private final IMessageReadService messageReadService;

	private final ISysBaseAPI sysBaseAPI;

	/**
	 * 发送一条消息
	 *
	 * @param vo 签证官
	 * @return boolean
	 */
	public boolean sendMessage(SendMessageVO vo) {
		if (StringUtils.isBlank(vo.getTitle())
				|| StringUtils.isBlank(vo.getContent())
				|| StringUtils.isBlank(vo.getCreateBy())
				|| CollectionUtils.isEmpty(vo.getUserIds())){
			//不可为空判定
			return false;
		}
		//信息对象
		Message message = new Message();
		message.setTitle(vo.getTitle())
				.setContent(vo.getContent())
				.setDelFlag(0)
				.setCreateBy(vo.getCreateBy());

		if (messageMapper.insert(message)<1) {
			return false;
		}
		if (message.getId()==null){
			//手动回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		List<MessageRead> readList = new ArrayList<>();

		//public List<LoginUser> queryAllUserByIds(String[] userIds);

		List<LoginUser> userList = sysBaseAPI.queryAllUserByIds(vo.getUserIds().toArray(new String[0]));

		//List<SysUser> userList = this.sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, vo.getUserIds()));
		if (userList==null || userList.size()!=vo.getUserIds().size()){
			//手动回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}

		for (LoginUser user : userList) {
			if (user==null || user.getId()==null){
				//手动回滚事务
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
			//读取状态
			MessageRead read = new MessageRead();
			read.setMessageId(message.getId())
					.setStaffId(user.getId())
					.setStaffName(user.getRealname())
					.setDelFlag(0)
					.setReadFlag(0);
			readList.add(read);
		}
		if (!messageReadService.saveBatch(readList)){
			//手动回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		return true;
	}

	/**
	 * 批量发送消息
	 *
	 * @param voList 签证官
	 * @return boolean
	 */
	public boolean sendBatchMessage(List<SendMessageVO> voList) {
		if (voList==null || voList.size()==0){
			return false;
		}
		for (SendMessageVO vo : voList) {
			if (!this.sendMessage(vo)){
				//手动回滚事务
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return false;
			}
		}
		return true;
	}


	/**
	 * 改为已读消息状态
	 *
	 * @param userId        用户id
	 * @param messageIdList 消息id列表
	 * @return boolean
	 */
	public boolean readMessage(String userId,List<Long> messageIdList){
		if (StringUtils.isBlank(userId)|| CollectionUtils.isEmpty(messageIdList)){
			return false;
		}
		boolean update = messageReadService.update(new MessageRead().setReadFlag(1), new LambdaQueryWrapper<MessageRead>()
				.in(MessageRead::getMessageId, messageIdList)
				.eq(MessageRead::getStaffId, userId));
		if (!update){
			//手动回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		return true;
	}
}
