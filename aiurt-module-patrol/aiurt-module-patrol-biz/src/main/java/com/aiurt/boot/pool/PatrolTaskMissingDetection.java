package com.aiurt.boot.pool;

import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.service.IPatrolTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 巡检任务漏检定时检测
 */
@Slf4j
@Component
public class PatrolTaskMissingDetection implements Job {

    @Autowired
    private IPatrolTaskService patrolTaskService;

    // 默认44个小时，即第二天晚上8点后算漏检
    @Value("${patrol.missing.hour:44}")
    private Integer missingTime;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        taskDetection();
    }

    public void execute() {
        taskDetection();
    }

    /**
     * 每天固定时间检测漏检的任务
     */
    private void taskDetection() {

        // 获取以下状态为0待指派、1待确认、2待执行、3已退回、4执行中的任务
        List<Integer> status = Arrays.asList(PatrolConstant.TASK_INIT, PatrolConstant.TASK_CONFIRM,
                PatrolConstant.TASK_EXECUTE, PatrolConstant.TASK_RETURNED, PatrolConstant.TASK_RUNNING);
        List<PatrolTask> taskList = Optional.ofNullable(patrolTaskService.lambdaQuery()
                .in(PatrolTask::getStatus, status).list()).orElseGet(Collections::emptyList);

        // 统计漏检数
        AtomicInteger missNum = new AtomicInteger();

        taskList.stream().forEach(l -> {
            if (null == l.getPatrolDate()) {
                return;
            }
            Date patrolDate = DateUtil.parseDate(DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd 00:00:00"));
            LocalDateTime localDateTime = patrolDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            // 漏检时间
            Date missDate = Date.from(localDateTime.plusHours(missingTime).atZone(ZoneId.systemDefault()).toInstant());
            // 当前时间
            Date now = new Date();
            int compare = DateUtil.compare(now, missDate);
            // 如果当前时间大于了漏检的时间
            if (compare >= 0) {
                // 更新任务为已漏检状态
                l.setStatus(PatrolConstant.TASK_MISSED);
                boolean update = patrolTaskService.updateById(l);
                if (update) {
                    missNum.getAndAdd(1);
                }
            }
        });
        log.info("存在{}条任务记录漏检,并更新为已漏检状态！", missNum.get());
    }
}
