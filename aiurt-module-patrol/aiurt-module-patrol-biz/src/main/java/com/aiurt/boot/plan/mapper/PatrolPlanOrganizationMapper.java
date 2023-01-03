package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.PatrolPlanOrganization;
import com.aiurt.boot.plan.param.PatrolPlanOrganizationParam;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_plan_organization
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@EnableDataPerm(excluseMethodName = "selectOrgByPlanCode")
public interface PatrolPlanOrganizationMapper extends BaseMapper<PatrolPlanOrganization> {
    /**
     * 通过计划编号查询组织结构
     *
     * @param planCode
     * @return
     */
    List<PatrolPlanOrganizationParam> selectOrgByPlanCode(@Param("planCode") String planCode);

    /**
     * 根据用户组织机构权限获取计划编号
     * @return
     */
    List<String> getPlanCodeByUserOrg();
}
