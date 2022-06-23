package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.entity.PatrolPlanStation;
import com.aiurt.boot.plan.param.PatrolPlanStationParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_plan_station
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface IPatrolPlanStationService extends IService<PatrolPlanStation> {

    /**
     * 根据巡检计划编号查询站点信息
     *
     * @param planCode
     * @return
     */
    List<PatrolPlanStationParam> selectStationByPlanCode(String planCode);
}
