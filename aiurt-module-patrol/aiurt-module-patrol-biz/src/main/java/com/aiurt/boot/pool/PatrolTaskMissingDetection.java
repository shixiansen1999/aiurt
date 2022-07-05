package com.aiurt.boot.pool;

import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.service.IPatrolTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 巡检任务漏检定时检测
 */
@Slf4j
public class PatrolTaskMissingDetection implements Job {

    @Autowired
    private IPatrolTaskService patrolTaskService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TaskDetection(context);
    }

    /**
     * 每天固定时间检测漏检的任务
     */
    private void TaskDetection(JobExecutionContext context) {
        Scheduler scheduler = context.getScheduler();
        Date scheduledFireTime = context.getScheduledFireTime();
        // 获取以下状态为0待指派、1待确认、2待执行、3已退回、4执行中的任务
        List<Integer> status = Arrays.asList(PatrolConstant.TASK_INIT, PatrolConstant.TASK_CONFIRM,
                PatrolConstant.TASK_EXECUTE, PatrolConstant.TASK_RETURNED, PatrolConstant.TASK_RUNNING);
        List<PatrolTask> taskList = Optional.ofNullable(patrolTaskService.lambdaQuery()
                .in(PatrolTask::getStatus, status).list()).orElseGet(Collections::emptyList);
        taskList.stream().forEach(l -> {
            Date patrolDate = l.getPatrolDate();
            LocalDateTime localDateTime = patrolDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            localDateTime.plusDays(1);
            localDateTime.withHour(20);
            localDateTime.withMinute(0);
            localDateTime.withSecond(0);

        });
    }
}
