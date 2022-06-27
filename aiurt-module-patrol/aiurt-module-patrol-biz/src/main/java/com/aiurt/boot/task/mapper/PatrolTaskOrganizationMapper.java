package com.aiurt.boot.task.mapper;

import java.util.List;

import com.aiurt.boot.plan.param.PatrolPlanOrganizationParam;
import com.aiurt.boot.task.param.PatrolTaskOrganizationParam;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: patrol_task_organization
 * @Author: aiurt
 * @Date:   2022-06-27
 * @Version: V1.0
 */
public interface PatrolTaskOrganizationMapper extends BaseMapper<PatrolTaskOrganization> {

    List<PatrolTaskOrganizationParam> selectOrgByTaskCode(@Param("taskCode") String taskCode);
}
