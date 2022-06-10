package com.aiurt.boot.modules.worklog.task;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.entity.MessageRead;
import com.aiurt.boot.modules.appMessage.service.IMessageReadService;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.mapper.SysUserMapper;
import com.aiurt.boot.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.boot.modules.worklog.entity.WorkLog;
import com.aiurt.boot.modules.worklog.service.IWorkLogService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.TaskStatusUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * demo
 *
 * @description: TestJob
 * @author: Mr.zhao
 * @date: 2021/12/17 17:25
 */
public class WorkLogJobNight implements Job {


	@Resource
	private SysUserMapper userMapper;

	@Resource
	private IWorkLogService workLogService;

	@Resource
	private IMessageService messageService;

	@Resource
	private IMessageReadService messageReadService;
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		if (!TaskStatusUtil.getTaskStatus()) {
			return;
		}
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务开始");
		WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
		LocalDate now = LocalDate.now();
		LocalDateTime startTime = now.atTime(0, 0, 0);
		LocalDateTime endTime = now.atTime(23, 59, 59);
		List<SysUser> userList = userMapper.selectUserByTimeAndItemAndOrgId(DateUtils.getDate("yyyy-MM-dd"),"夜",dto.getOrgId());
		if (ObjectUtil.isEmpty(userList)){
			return;
		}
		List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
		List<WorkLog> workLogList = workLogService.list(new LambdaQueryWrapper<WorkLog>()
				.eq(WorkLog::getDelFlag, CommonConstant.DEL_FLAG_0)
				.in(WorkLog::getCreateBy, userIds)
				.between(WorkLog::getCreateTime, startTime, endTime)
				.select(WorkLog::getCreateBy)
		);
		List<String> successIds = workLogList.stream().map(WorkLog::getCreateBy).collect(Collectors.toList());
		userIds.removeAll(successIds);
		if (CollectionUtils.isNotEmpty(userIds)){
			Message message = new Message();
			message.setTitle("工作日志提醒");
			message.setContent(dto.getContent());
			messageService.save(message);
			if (message.getId()!=null){
				List<MessageRead> list = new ArrayList<>();
				userIds.forEach(
						u->{
							MessageRead read = new MessageRead();
							read.setMessageId(message.getId()).setReadFlag(CommonConstant.DEL_FLAG_0).setStaffId(u).setDelFlag(CommonConstant.DEL_FLAG_0);
							list.add(read);
						}
				);
				messageReadService.saveBatch(list);
			}
		}
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务结束");
	}
}
