package com.aiurt.boot.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 任务状态开启与关闭获取
 *
 * 在 vm options加入 -DtaskStatus=1  运行定时器
 *
 * @author Mr.zhao
 * @date 2022/3/1 11:38
 */
@Slf4j
public class TaskStatusUtil {

	private static Integer taskStatus;

	static {
		try {
			log.info("任务运行状态:{}",System.getProperty("taskStatus"));
			taskStatus = Integer.parseInt(System.getProperty("taskStatus"));
		} catch (NumberFormatException e) {
			taskStatus = 0;
		}
		System.out.println("任务运行状态:"+taskStatus);
	}


	/**
	 * 获取任务状态
	 *
	 * @return {@code boolean}
	 */
	public static boolean getTaskStatus() {
		log.debug("任务运行状态:{}",taskStatus.equals(1));
		return taskStatus.equals(1);
	}

	/**
	 * 获取任务状态参数
	 *
	 * @return {@code boolean}
	 */
	public static Integer getTaskStatusNum() {
		return taskStatus;
	}
}
