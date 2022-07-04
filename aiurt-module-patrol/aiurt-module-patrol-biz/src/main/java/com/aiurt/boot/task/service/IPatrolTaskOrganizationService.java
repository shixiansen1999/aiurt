package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolUserInfoDTO;
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

    /**
     * 根据任务编码获取该任务包含组织机构下的人员信息
     *
     * @param code
     * @return
     */
    List<PatrolUserInfoDTO> getUserListByTaskCode(String code);

    /**
     * 根据任务编号获取组织机构编号
     *
     * @param taskCode
     * @return
     */
    List<String> getOrgCode(String taskCode);
}
