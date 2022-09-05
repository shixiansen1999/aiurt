package com.aiurt.boot.statistics.service;

import com.aiurt.boot.statistics.dto.PatrolSituation;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.service.IPatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PatrolStatisticsService {
    @Autowired
    private IPatrolTaskService patrolTaskService;

    /**
     * 首页巡视概况
     *
     * @return
     */
    public PatrolSituation getOverviewInfo(Date startDate, Date endDate) {
        PatrolSituation situation = new PatrolSituation();
        Long sum = patrolTaskService.lambdaQuery().between(PatrolTask::getPatrolDate, startDate, endDate).count();

        return situation;
    }
}
