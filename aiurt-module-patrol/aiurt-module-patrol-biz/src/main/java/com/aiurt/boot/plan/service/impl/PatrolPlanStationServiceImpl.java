package com.aiurt.boot.plan.service.impl;

import com.aiurt.boot.plan.entity.PatrolPlanStation;
import com.aiurt.boot.plan.mapper.PatrolPlanStationMapper;
import com.aiurt.boot.plan.param.PatrolPlanStationParam;
import com.aiurt.boot.plan.service.IPatrolPlanStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: patrol_plan_station
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolPlanStationServiceImpl extends ServiceImpl<PatrolPlanStationMapper, PatrolPlanStation> implements IPatrolPlanStationService {

    @Autowired
    private PatrolPlanStationMapper patrolPlanStationMapper;

    @Override
    public List<PatrolPlanStationParam> selectStationByPlanCode(String planCode) {
        return patrolPlanStationMapper.selectStationByPlanCode(planCode);
    }
}
