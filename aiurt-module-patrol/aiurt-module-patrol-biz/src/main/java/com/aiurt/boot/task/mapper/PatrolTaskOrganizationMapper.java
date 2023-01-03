package com.aiurt.boot.task.mapper;

import java.util.List;

import com.aiurt.boot.statistics.dto.IndexOrgDTO;
import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.aiurt.boot.task.dto.PatrolUserInfoDTO;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: patrol_task_organization
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
@EnableDataPerm
public interface PatrolTaskOrganizationMapper extends BaseMapper<PatrolTaskOrganization> {
    /**
     * 根据任务编号查询组织机构信息
     *
     * @param taskCode
     * @return
     */
    List<PatrolTaskOrganizationDTO> selectOrgByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 根据任务编码获取该任务包含组织机构下的人员信息
     *
     * @param code
     * @return
     */
    List<PatrolUserInfoDTO> getUserListByTaskCode(@Param("code") String code);

    /**
     * 根据任务编号获取组织机构编号
     *
     * @param taskCode
     * @return
     */
    List<String> getOrgCode(@Param("taskCode") String taskCode);

    /**
     * 首页巡视异常任务的组织机构信息
     *
     * @param taskCode
     * @return
     */
    List<IndexOrgDTO> getOrgInfo(@Param("taskCode") String taskCode);

    /**
     * 通过用户部门权限获取任务编号
     * @return
     */
    List<String> getTaskCodeByUserOrg();
}
