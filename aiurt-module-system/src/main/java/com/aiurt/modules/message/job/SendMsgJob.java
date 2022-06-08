package com.aiurt.modules.message.job;

import java.util.List;

import com.aiurt.common.util.DateUtils;
import com.aiurt.modules.message.handle.ISendMsgHandle;
import com.aiurt.modules.message.handle.enums.SendMsgStatusEnum;
import com.aiurt.modules.message.handle.enums.SendMsgTypeEnum;
import com.aiurt.modules.message.service.ISysMessageService;
import com.aiurt.modules.message.entity.SysMessage;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 发送消息任务
 * @author: jeecg-boot
 */

@Slf4j
public class SendMsgJob implements Job {

	@Autowired
	private ISysMessageService sysMessageService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		log.info(String.format(" Jeecg-Boot 发送消息任务 SendMsgJob !  时间:" + DateUtils.getTimestamp()));

		// 1.读取消息中心数据，只查询未发送的和发送失败不超过次数的
		QueryWrapper<SysMessage> queryWrapper = new QueryWrapper<SysMessage>();
		queryWrapper.eq("es_send_status", SendMsgStatusEnum.WAIT.getCode())
				.or(i -> i.eq("es_send_status", SendMsgStatusEnum.FAIL.getCode()).lt("es_send_num", 6));
		List<SysMessage> sysMessages = sysMessageService.list(queryWrapper);
		System.out.println(sysMessages);
		// 2.根据不同的类型走不通的发送实现类
		for (SysMessage sysMessage : sysMessages) {
			ISendMsgHandle sendMsgHandle = null;
			try {
				if (sysMessage.getEsType().equals(SendMsgTypeEnum.EMAIL.getType())) {
					sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.EMAIL.getImplClass()).newInstance();
				} else if (sysMessage.getEsType().equals(SendMsgTypeEnum.SMS.getType())) {
					sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.SMS.getImplClass()).newInstance();
				} else if (sysMessage.getEsType().equals(SendMsgTypeEnum.WX.getType())) {
					sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.WX.getImplClass()).newInstance();
				} else if(sysMessage.getEsType().equals(SendMsgTypeEnum.SYSTEM_MESSAGE.getType())){
                    //update-begin---author:wangshuai ---date:20220323  for：[issues/I4X698]根据模板发送系统消息，发送失败------------
				    sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.SYSTEM_MESSAGE.getImplClass()).newInstance();
                    //update-end---author:wangshuai ---date:20220323  for：[issues/I4X698]根据模板发送系统消息，发送失败------------
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			Integer sendNum = sysMessage.getEsSendNum();
			try {
                //update-begin---author:wangshuai ---date:20220323  for：[issues/I4X698]模板管理发送消息出现NullPointerException 錯誤------------
                if(null != sendMsgHandle){
                    sendMsgHandle.SendMsg(sysMessage.getEsReceiver(), sysMessage.getEsTitle(),
                            sysMessage.getEsContent().toString());
                    //发送消息成功
                    sysMessage.setEsSendStatus(SendMsgStatusEnum.SUCCESS.getCode());
                }
                //update-end---author:wangshuai ---date:20220323  for：[issues/I4X698]模板管理发送消息出现NullPointerException 錯誤------------
			} catch (Exception e) {
				e.printStackTrace();
				// 发送消息出现异常
				sysMessage.setEsSendStatus(SendMsgStatusEnum.FAIL.getCode());
			}
			sysMessage.setEsSendNum(++sendNum);
			// 发送结果回写到数据库
			sysMessageService.updateById(sysMessage);
		}

	}

}
