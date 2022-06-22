package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.entity.inspection.task.RepairTask;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.service.IRepairTaskService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService {

}
