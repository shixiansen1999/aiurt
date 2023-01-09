package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.PatrolPlanStandard;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_plan_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@EnableDataPerm
public interface PatrolPlanStandardMapper extends BaseMapper<PatrolPlanStandard> {
    /**
     * 根据专业子系统权限获取计划编号
     * @return
     */
    List<String> getPlanCodeByMajorSystem();

    /**
     * 查询
     * @return
     */
    List<String> getPlanCodeByMajorSystemIsNull();
}
