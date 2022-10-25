package com.aiurt.common.util;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description: QuartzUtils
 * @author: Mr.zhao
 * @date: 2021/12/17 17:21
 */
@Component
public class QuartzUtils {

	@Autowired
	private Scheduler scheduler;



	/**
	 * @param jobName          任务名
	 * @param jobGroupName     任务组名,传null使用默认任务组名
	 * @param jobClass         任务
	 * @param triggerName      触发器名
	 * @param triggerGroupName 触发器组名,传null使用默认触发器名
	 * @param dataMap          jobClass需要的参数
	 * @Description: 添加一个定时任务
	 */
	public void addJob(String jobName, String jobGroupName,
	                          String triggerName, String triggerGroupName,
	                          Class<? extends Job> jobClass,
	                          String cron,
	                          Map<String, Object> dataMap) {
		try {
			// 获取调度任务工厂
			JobDataMap jobDataMap = new JobDataMap(dataMap);
			JobDetail job = JobBuilder.newJob(jobClass)
					.withIdentity(jobName, jobGroupName)
					.usingJobData(jobDataMap)
					.build();
			// 创建一个CronTrigger
			Trigger trigger = null;
			TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger();
			//设置名称,分组名称
			builder.withIdentity(triggerName, triggerGroupName);
			//设置开始时间
			builder.startNow();
			//设置时间
			builder.withSchedule(CronScheduleBuilder.cronSchedule(cron));

			trigger = builder.build();
			// 将JobDetail和CronTrigger加入调度任务
			scheduler.scheduleJob(job, trigger);
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param jobName          任务名
	 * @param jobGroupName     任务组名
	 * @param triggerName      触发器名
	 * @param triggerGroupName 触发器组名
	 * @param cron             定时表达式
	 * @param dataMap          jobClass需要的参数
	 * @Description: 修改一个任务
	 */
	public  void modifyJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
	                             Class<? extends Job> jobClass, String cron,  Map<String, Object> dataMap) {
		try {
			// 删除原有的定时任务
			removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
			// 添加新的定时任务
			addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron, dataMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param jobName          任务名
	 * @param jobGroupName     任务组名
	 * @param triggerName      触发器名
	 * @param triggerGroupName 触发器组名
	 * @Description: 移除一个任务
	 */
	public  void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			// 停止触发器
			scheduler.pauseTrigger(triggerKey);
			// 移除触发器
			scheduler.unscheduleJob(triggerKey);
			// 删除任务
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description:启动所有定时任务
	 */
	public  void startJobs() {
		try {
			scheduler.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description:关闭所有定时任务
	 */
	public  void shutdownJobs() {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description:检查Job运行状态
	 */
	public  boolean checkJobsRunStatus(String triggerName, String triggerGroupName) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
			String state = triggerState.name();
			if (Trigger.TriggerState.NORMAL.name().equals(state)
					|| Trigger.TriggerState.BLOCKED.name().equals(state)
					|| Trigger.TriggerState.COMPLETE.name().equals(state)) {
				return true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}
}
