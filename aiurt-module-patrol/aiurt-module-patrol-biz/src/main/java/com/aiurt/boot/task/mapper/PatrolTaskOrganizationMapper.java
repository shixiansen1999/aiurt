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

    List<PatrolTaskOrganizationDTO> selectOrgByTaskCode(@Param("taskCode") String taskCode);

    List<PatrolUserInfoDTO> getUserListByTaskCode(@Param("code") String code);

    List<String> getOrgCode(@Param("taskCode") String taskCode);

    /**
     * 首页巡视异常任务的组织机构信息
     *
     * @param taskCode
     * @return
     */
    List<IndexOrgDTO> getOrgInfo(@Param("taskCode") String taskCode);
}
