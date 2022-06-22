package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolTaskServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTask> implements IPatrolTaskService {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;

    @Override
    public IPage<PatrolTask> getTaskList(Page<PatrolTask> page, PatrolTask patrolTask) {
        return patrolTaskMapper.getTaskList(page, patrolTask);
    }
}
