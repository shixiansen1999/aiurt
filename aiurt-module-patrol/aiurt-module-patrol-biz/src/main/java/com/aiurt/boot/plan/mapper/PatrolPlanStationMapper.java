package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.PatrolPlanStation;
import com.aiurt.boot.plan.param.PatrolPlanStationParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_plan_station
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolPlanStationMapper extends BaseMapper<PatrolPlanStation> {
    /**
     * 根据巡检计划编号查询站点信息
     * @param planCode
     * @return
     */
    List<PatrolPlanStationParam> selectStationByPlanCode(@Param("planCode") String planCode);
}
