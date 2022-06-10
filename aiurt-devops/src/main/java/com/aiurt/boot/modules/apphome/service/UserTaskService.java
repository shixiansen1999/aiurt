package com.aiurt.boot.modules.apphome.service;

import com.aiurt.boot.modules.apphome.entity.UserTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.apphome.param.UserTaskAddParam;

import java.util.List;

/**
 * @description: UserTaskService
 * @author: Mr.zhao
 * @date: 2021/11/25 15:45
 */

public interface UserTaskService extends IService<UserTask> {

	/**
	 * 添加代办事项
	 *
	 * @param param 参数
	 * @return boolean
	 */
	boolean add(UserTaskAddParam param);


	/**
	 * 改为完成状态
	 *
	 * @param userId 用户id
	 * @param code   代码
	 * @param type   类型
	 * @return boolean
	 */
	boolean complete(String userId, String code, Integer type);

	/**
	 * 删除待办事项
	 * @param recordId 记录id
	 * @param type   类型
	 * @return boolean
	 */
	boolean del( Long recordId, Integer type);


	/**
	 * 改为完成状态
	 *
	 * @param userId   用户id
	 * @param recordId 记录id
	 * @param type     类型
	 * @return boolean
	 */
	boolean complete(String userId, Long recordId, Integer type);
	/**
	 * 改为完成状态
	 *
	 * @param userIds  用户id
	 * @param recordId 记录id
	 * @param type     类型
	 * @return boolean
	 */
	boolean complete(List<String> userIds, Long recordId, Integer type);
	/**
	 * 完成工作
	 *
	 * @param userId 用户id
	 * @return boolean
	 */
	boolean completeWork(String userId,String date);

	/**
	 * 重新任命
	 *
	 * @param originalUserId 原来的用户id
	 * @param targetUserId   目标用户id
	 * @param recordId       记录id
	 * @return boolean
	 */
	boolean reAppoint(String originalUserId, String targetUserId, Long recordId, Integer type);
	/**
	 * 重新任命
	 *
	 * @param originalUserId 原来用户id
	 * @param targetUserId   目标用户id
	 * @param code           代码
	 * @param type           类型
	 * @return boolean
	 */
	boolean reAppoint(String originalUserId, String targetUserId, String code, Integer type);






	/**
	 * 删除用户的任务工作
	 *
	 * @param userIds  用户ids
	 * @param recordId 记录id
	 * @param type     类型
	 * @return boolean
	 */
	boolean removeUserTaskWork(List<String> userIds, Long recordId, Integer type);

	/**
	 * 删除用户任务工作
	 *
	 * @param userIds 用户id
	 * @param code    代码
	 * @param type    类型
	 * @return boolean
	 */
	boolean removeUserTaskWork(List<String> userIds, String code, Integer type);

	/**
	 * 删除用户任务工作
	 *
	 * @param code 代码
	 * @param type 类型
	 * @return boolean
	 */
	boolean removeUserTaskWork(String code, Integer type);

}
