package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.entity.patrol.task.PatrolTask;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.service.IPatrolTaskService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolTaskServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTask> implements IPatrolTaskService {

}
