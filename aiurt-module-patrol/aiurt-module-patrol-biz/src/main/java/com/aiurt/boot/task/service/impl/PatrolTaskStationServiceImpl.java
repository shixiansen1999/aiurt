package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.entity.PatrolTaskStation;
import com.aiurt.boot.task.mapper.PatrolTaskStationMapper;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.service.IPatrolTaskStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: patrol_task_station
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
@Service
public class PatrolTaskStationServiceImpl extends ServiceImpl<PatrolTaskStationMapper, PatrolTaskStation> implements IPatrolTaskStationService {

    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;

    @Override
    public List<PatrolTaskStationDTO> selectStationByTaskCode(String taskCode) {
        return patrolTaskStationMapper.selectStationByTaskCode(taskCode);
    }
}
