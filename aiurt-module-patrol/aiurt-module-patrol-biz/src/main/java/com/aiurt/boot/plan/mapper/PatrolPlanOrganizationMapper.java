package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.PatrolPlanOrganization;
import com.aiurt.boot.plan.param.PatrolPlanOrganizationParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_plan_organization
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolPlanOrganizationMapper extends BaseMapper<PatrolPlanOrganization> {
    /**
     * 通过计划编号查询组织结构
     * @param planCode
     * @return
     */
    List<PatrolPlanOrganizationParam> selectOrgByPlanCode(@Param("planCode") String planCode);
}
