package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.dto.PatrolUserInfoDTO;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.mapper.PatrolTaskOrganizationMapper;
import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.aiurt.boot.task.service.IPatrolTaskOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: patrol_task_organization
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
@Service
public class PatrolTaskOrganizationServiceImpl extends ServiceImpl<PatrolTaskOrganizationMapper, PatrolTaskOrganization> implements IPatrolTaskOrganizationService {
    @Autowired
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;

    @Override
    public List<PatrolTaskOrganizationDTO> selectOrgByTaskCode(String taskCode) {
        return patrolTaskOrganizationMapper.selectOrgByTaskCode(taskCode);
    }

    @Override
    public List<PatrolUserInfoDTO> getUserListByTaskCode(String code) {
        return patrolTaskOrganizationMapper.getUserListByTaskCode(code);
    }

    @Override
    public List<String> getOrgCode(String taskCode) {
        return patrolTaskOrganizationMapper.getOrgCode(taskCode);
    }
}
