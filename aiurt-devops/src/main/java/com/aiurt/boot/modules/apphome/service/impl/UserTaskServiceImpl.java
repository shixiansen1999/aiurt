package com.aiurt.boot.modules.apphome.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.entity.UserTask;
import com.aiurt.boot.modules.apphome.mapper.UserTaskMapper;
import com.aiurt.boot.modules.apphome.param.UserTaskAddParam;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: UserTaskServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/25 15:45
 */

@Service
@RequiredArgsConstructor
public class UserTaskServiceImpl extends ServiceImpl<UserTaskMapper, UserTask> implements UserTaskService {

	private final ISysBaseAPI sysBaseAPI;

	@Override
	public boolean add(UserTaskAddParam param) {
		//不能同时为空
		if (StringUtils.isBlank(param.getRecordCode()) && param.getRecordId() == null && param.getType()!=4 ) {
			return false;
		}
		//等级与类型不能为空
		if (param.getLevel() == null || param.getType() == null) {
			return false;
		}

		List<UserTask> taskList = new ArrayList<>();
		for (String userId : param.getUserIds()) {

			// todo
			LoginUser userById = sysBaseAPI.getUserById(userId);
			UserTask userTask = new UserTask();
			BeanUtils.copyProperties(param, userTask);
			userTask.setWorkTime(LocalDate.now())
					.setDelFlag(0)
					.setStatus(0)
					.setRealName(userById.getRealname())
					.setUserId(userId);
			taskList.add(userTask);
		}

		boolean b = this.saveBatch(taskList);

		return b;
	}

	@Override
	public boolean complete(String userId, String code, Integer type) {
		UserTask userTask = new UserTask().setStatus(1).setCompleteTime(LocalDateTime.now());
		int update = this.baseMapper.update(userTask,
				new LambdaQueryWrapper<UserTask>()
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(UserTask::getUserId, userId)
				.eq(UserTask::getRecordCode, code)
						.eq(UserTask::getType,type)
		);
		if (update > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean del(Long recordId, Integer type) {
		UserTask userTask = new UserTask().setDelFlag(1);
		int update = this.baseMapper.update(userTask,
				new LambdaQueryWrapper<UserTask>()
						.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
						.eq(UserTask::getRecordId, recordId)
						.eq(UserTask::getType,type)
		);
		if (update > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean complete(String userId, Long recordId, Integer type) {
		int update = this.baseMapper.update(new UserTask().setStatus(1).setCompleteTime(LocalDateTime.now()),
				new LambdaQueryWrapper<UserTask>()
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(UserTask::getUserId, userId)
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordId, recordId)
		);
		if (update > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean complete(List<String> userIds, Long recordId, Integer type) {
		int update = this.baseMapper.update(new UserTask().setStatus(1).setCompleteTime(LocalDateTime.now()),
				new LambdaQueryWrapper<UserTask>()
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.in(UserTask::getUserId, userIds)
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordId, recordId)
		);
		if (update > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean completeWork(String userId,String date) {
		return this.update(new LambdaUpdateWrapper<UserTask>()
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(UserTask::getUserId, userId)
				.eq(UserTask::getType, UserTaskConstant.USER_TASK_TYPE_4)
				.eq(UserTask::getWorkTime, date)
				.set(UserTask::getStatus,1)
				.set(UserTask::getCompleteTime,LocalDateTime.now())
		 );
	}

	@Override
	public boolean reAppoint(String originalUserId, String targetUserId, Long recordId, Integer type) {
		UserTask userTask = this.baseMapper.selectOne(new LambdaQueryWrapper<UserTask>()
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(UserTask::getUserId, originalUserId)
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordId, recordId).last("limit 1")
		);
		if (userTask == null) {
			return false;
		}
		LoginUser loginUser = sysBaseAPI.getUserById(targetUserId);
		userTask.setUserId(targetUserId).setRealName(loginUser.getRealname());
		int update = this.baseMapper.updateById(userTask);
		if (update > 0) {
			return true;
		}


		return false;
	}

	@Override
	public boolean reAppoint(String originalUserId, String targetUserId, String code, Integer type) {
		LoginUser loginUser = sysBaseAPI.getUserById(targetUserId);
		UserTask userTask = this.baseMapper.selectOne(new LambdaQueryWrapper<UserTask>()
				.eq(UserTask::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(UserTask::getUserId, originalUserId)
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordCode, code).last("limit 1")
		);
		if (userTask == null) {
			return false;
		}
		userTask.setUserId(targetUserId).setRealName(loginUser.getRealname());
		int update = this.baseMapper.updateById(userTask);
		if (update > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean removeUserTaskWork(List<String> userIds, Long recordId, Integer type) {
		int delete = this.baseMapper.delete(new LambdaQueryWrapper<UserTask>()
				.in(UserTask::getUserId, userIds)
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordId, recordId)
		);
		if (delete>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean removeUserTaskWork(List<String> userIds, String code, Integer type) {
		int delete = this.baseMapper.delete(new LambdaQueryWrapper<UserTask>()
				.in(UserTask::getUserId, userIds)
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordCode, code)
		);
		if (delete>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean removeUserTaskWork(String code, Integer type) {
		int delete = this.baseMapper.delete(new LambdaQueryWrapper<UserTask>()
				.eq(UserTask::getType, type)
				.eq(UserTask::getRecordCode, code)
		);
		if (delete>0){
			return true;
		}
		return false;
	}
}
