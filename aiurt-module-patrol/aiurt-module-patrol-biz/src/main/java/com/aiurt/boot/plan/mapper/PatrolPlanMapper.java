package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.PatrolPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolPlanMapper extends BaseMapper<PatrolPlan> {

    List<String> getMajorInfoByPlanId(@Param("planId") String planId);

    List<String> getSubsystemInfoByPlanId(@Param("planId") String planId);


}
