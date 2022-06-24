package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolTaskDeviceServiceImpl extends ServiceImpl<PatrolTaskDeviceMapper, PatrolTaskDevice> implements IPatrolTaskDeviceService {

    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;

    @Override
    public List<PatrolTaskDeviceParam> selectBillInfo(PatrolTaskDeviceParam patrolTaskDeviceParam) {
        return patrolTaskDeviceMapper.selectBillInfo(patrolTaskDeviceParam);
    }
}
