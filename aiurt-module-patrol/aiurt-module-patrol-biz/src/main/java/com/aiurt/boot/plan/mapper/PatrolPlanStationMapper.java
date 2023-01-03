package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.PatrolPlanStation;
import com.aiurt.boot.plan.param.PatrolPlanStationParam;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_plan_station
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@EnableDataPerm(excluseMethodName = "selectStationByPlanCode")
public interface PatrolPlanStationMapper extends BaseMapper<PatrolPlanStation> {
    /**
     * 根据巡检计划编号查询站点信息
     * @param planCode
     * @return
     */
    List<PatrolPlanStationParam> selectStationByPlanCode(@Param("planCode") String planCode);

    /**
     * 根据用户站点权限获取计划编号
     * @return
     */
    List<String> getPlanCodeByUserStation();
}
