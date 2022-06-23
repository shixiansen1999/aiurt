package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.entity.PatrolPlanOrganization;
import com.aiurt.boot.plan.param.PatrolPlanOrganizationParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_plan_organization
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface IPatrolPlanOrganizationService extends IService<PatrolPlanOrganization> {

    /**
     * 通过计划编号查询组织结构
     *
     * @param planCode
     * @return
     */
    List<PatrolPlanOrganizationParam> selectOrgByPlanCode(String planCode);
}
