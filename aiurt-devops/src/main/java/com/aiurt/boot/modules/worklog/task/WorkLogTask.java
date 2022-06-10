package com.aiurt.boot.modules.worklog.task;

import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.entity.UserTask;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.TaskStatusUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: WorkLogTask
 * @author: Mr.zhao
 * @date: 2021/11/30 20:08
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkLogTask {

	private static final String WORK_TITLE = "工作日志未填写";

	private final ISysUserService sysUserService;

	private final UserTaskService userTaskService;

	/**
	 * 生成待办任务
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	public void setUserWorkTask(){

		if (!TaskStatusUtil.getTaskStatus()) {
			return;
		}

		Date date = new Date();
		LocalDate now = LocalDate.now();

		userTaskService.lambdaUpdate().eq(UserTask::getType, UserTaskConstant.USER_TASK_TYPE_4).lt(UserTask::getCreateTime,now).remove();

		List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>()
				.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
				.select(SysUser::getId,SysUser::getRealname)
		);
		List<UserTask> taskList = new ArrayList<>();
		userList.forEach(u->{
					UserTask userTask = new UserTask();
					userTask.setWorkTime(now)
							.setRealName(u.getRealname())
							.setUserId(u.getId())
							.setType(UserTaskConstant.USER_TASK_TYPE_4)
							.setLevel(1)
							.setStatus(PatrolConstant.DISABLE)
							.setTitle(WORK_TITLE)
							.setProductionTime(date);
					taskList.add(userTask);
				}
		);
		userTaskService.saveBatch(taskList);
	}


}
