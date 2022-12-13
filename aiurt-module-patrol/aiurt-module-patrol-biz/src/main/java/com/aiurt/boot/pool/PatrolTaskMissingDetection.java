package com.aiurt.boot.pool;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.service.IPatrolTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 巡检任务漏检定时检测
 * @author cgkj0
 */
@Slf4j
@Component
public class PatrolTaskMissingDetection implements Job {

    @Autowired
    private IPatrolTaskService patrolTaskService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        taskDetection();
    }

    public void execute() {
        taskDetection();
    }

    /**
     * 周一和周五0点检测漏检的任务
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
            Date patrolDate = l.getPatrolDate();
            if (ObjectUtil.isNotEmpty(l.getEndTime())) {
                String endTime = DateUtil.format(l.getEndTime(), "HH:mm:ss");
                patrolDate = DateUtil.parse(DateUtil.format(patrolDate, "yyyy-MM-dd " + endTime));
            }
            // 当前时间
            Date now = new Date();
            int compare = DateUtil.compare(now, patrolDate);
            if (compare >= 0) {

                l.setOmitStatus(PatrolConstant.OMIT_STATUS);
                boolean update = patrolTaskService.updateById(l);
                if (update) {
                    missNum.getAndAdd(1);
                }
            }
        });
        log.info("存在{}条任务记录漏检,并更新为已漏检状态！", missNum.get());
    }
}
