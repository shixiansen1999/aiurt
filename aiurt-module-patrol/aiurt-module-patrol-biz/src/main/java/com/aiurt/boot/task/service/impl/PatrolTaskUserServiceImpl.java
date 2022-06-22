package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.aiurt.boot.task.service.IPatrolTaskUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: patrol_task_user
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolTaskUserServiceImpl extends ServiceImpl<PatrolTaskUserMapper, PatrolTaskUser> implements IPatrolTaskUserService {

}
