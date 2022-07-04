package com.aiurt.boot.task.mapper;

import java.util.List;

import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.aiurt.boot.task.dto.PatrolUserInfoDTO;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: patrol_task_organization
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
public interface PatrolTaskOrganizationMapper extends BaseMapper<PatrolTaskOrganization> {

    List<PatrolTaskOrganizationDTO> selectOrgByTaskCode(@Param("taskCode") String taskCode);

    List<PatrolUserInfoDTO> getUserListByTaskCode(@Param("code") String code);

    List<String> getOrgCode(@Param("taskCode") String taskCode);
}
