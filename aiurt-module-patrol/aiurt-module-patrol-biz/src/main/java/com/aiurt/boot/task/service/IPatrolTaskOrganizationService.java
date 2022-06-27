package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_task_organization
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
public interface IPatrolTaskOrganizationService extends IService<PatrolTaskOrganization> {
    /**
     * 根据任务编号查询组织机构信息
     *
     * @param taskCode
     * @return
     */
    List<PatrolTaskOrganizationDTO> selectOrgByTaskCode(String taskCode);
}
