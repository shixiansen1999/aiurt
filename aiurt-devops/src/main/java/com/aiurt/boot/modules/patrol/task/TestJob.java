package com.aiurt.boot.modules.patrol.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * demo
 *
 * @description: TestJob
 * @author: Mr.zhao
 * @date: 2021/12/17 17:25
 */
public class TestJob implements Job {
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("定时任务");
		System.out.println("定时任务中数据:"+jobExecutionContext.getMergedJobDataMap().getString("data"));
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
	}
}
