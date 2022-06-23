package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IPatrolTaskService extends IService<PatrolTask> {
    /**
     * app-巡检列表
     * @param pageList
     * @param patrolTaskDTO
     * @return
     * author hlq
     */
    Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO);
    /**
     * app-巡检任务领取
     * @param patrolTaskDTO
     */
    void getPatrolTaskReceive(PatrolTaskDTO patrolTaskDTO);

    /**
     * app-巡检任务领取后-退回
     * @param patrolTaskDTO
     */
    void getPatrolTaskReturn(PatrolTaskDTO patrolTaskDTO);

    /**
     * PC巡检任务池列表
     * @param page
     * @return
     */
    IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam);
}
