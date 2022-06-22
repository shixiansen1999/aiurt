package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.entity.inspection.task.RepairTaskUser;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import com.aiurt.boot.task.service.IRepairTaskUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: repair_task_user
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairTaskUserServiceImpl extends ServiceImpl<RepairTaskUserMapper, RepairTaskUser> implements IRepairTaskUserService {

}
