package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.aiurt.boot.task.mapper.PatrolDeviceMapper;
import com.aiurt.boot.task.service.IPatrolDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Service
public class PatrolDeviceServiceImpl extends ServiceImpl<PatrolDeviceMapper, PatrolDevice> implements IPatrolDeviceService {

    @Autowired
    private PatrolDeviceMapper patrolDeviceMapper;

    @Override
    public List<PatrolDeviceDTO> queryDevices(String taskId, String taskStandardId) {
        return patrolDeviceMapper.queryDevices(taskId, taskStandardId);
    }
}
