package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.PatrolTask;
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

    IPage<PatrolTask> getTaskList(Page<PatrolTask> page, PatrolTask patrolTask);
}
