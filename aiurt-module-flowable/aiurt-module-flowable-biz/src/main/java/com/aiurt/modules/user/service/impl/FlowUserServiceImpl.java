package com.aiurt.modules.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.user.dto.FlowUserRelationRespDTO;
import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.mapper.FlowUserMapper;
import com.aiurt.modules.user.service.IFlowUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class FlowUserServiceImpl implements IFlowUserService {

    @Autowired
    private FlowUserMapper flowUserMapper;
    /**
     * 用户id 查询 username
     *
     * @param userIdList 用户id
     * @return
     */
    @Override
    public List<String> getUserNameByUserId(List<String> userIdList) {
        if (CollectionUtil.isEmpty(userIdList)) {
            return Collections.emptyList();
        }

        return flowUserMapper.getUserNameByUserId(userIdList);
    }

    /**
     * roleid 查询username
     *
     * @param roleIdList roleid
     * @return
     */
    @Override
    public List<String> getUserNameByRoleId(List<String> roleIdList) {
        if (CollectionUtil.isEmpty(roleIdList)) {
            return Collections.emptyList();
        }
        return flowUserMapper.getUserNameByRoleId(roleIdList);
    }

    /**
     * orgId 查询username
     *
     * @param orgIdList orgId
     * @return
     */
    @Override
    public List<String> getUserNameByOrgId(List<String> orgIdList) {
        if (CollectionUtil.isEmpty(orgIdList)) {
            return Collections.emptyList();
        }
        return flowUserMapper.getUserNameByOrgId(orgIdList);
    }

    /**
     * @param name
     * @return
     */
    @Override
    public List<String> getUserName(String name) {
        return flowUserMapper.getUserName(name);
    }

    /**
     * 流程办理人关系下拉树
     *
     * @return
     */
    @Override
    public List<FlowUserRelationRespDTO> queryRelationList() {
        List<FlowUserRelationRespDTO> dtoList = Arrays.stream(FlowUserRelationEnum.values()).map(flowUserRelationEnum -> {
            FlowUserRelationRespDTO flowUserRelationRespDTO = new FlowUserRelationRespDTO();
            flowUserRelationRespDTO.setValue(flowUserRelationEnum.getCode());
            flowUserRelationRespDTO.setTitle(flowUserRelationEnum.getMessage());
            flowUserRelationRespDTO.setLabel(flowUserRelationEnum.getMessage());
            return flowUserRelationRespDTO;
        }).collect(Collectors.toList());
        return dtoList;
    }

    /**
     * 查询部门领导人
     *
     * @param orgId
     * @return
     */
    @Override
    public List<String> getManageUserName(String orgId) {
        return flowUserMapper.getManageUserName(orgId);
    }

    /**
     * 查询上级部门领导人
     *
     * @param orgId
     * @return
     */
    @Override
    public List<String> getParentManageUserName(String orgId) {
        return flowUserMapper.getParentManageUserName(orgId);
    }

    /**
     * 用户id 查询 username
     *
     * @param userIdList 用户id
     * @return
     */
    @Override
    public List<String> getUserNameByUserIdOrUserName(List<String> userIdList) {
        if (CollUtil.isEmpty(userIdList)) {
            return Collections.emptyList();
        }
        return flowUserMapper.getUserNameByUserIdOrUserName(userIdList);
    }

    /**
     * roleid 查询username
     *
     * @param roleIdList roleid
     * @return
     */
    @Override
    public List<String> getUserNameByRoleIdOrRoleCode(List<String> roleIdList) {
        if (CollUtil.isEmpty(roleIdList)) {
            return Collections.emptyList();
        }
        return flowUserMapper.getUserNameByRoleIdOrRoleCode(roleIdList);
    }

    /**
     * orgId 查询username
     *
     * @param orgIdList orgId
     * @return
     */
    @Override
    public List<String> getUserNameByOrgIdOrOrgCode(List<String> orgIdList) {
        if (CollUtil.isEmpty(orgIdList)) {
            return Collections.emptyList();
        }
        return flowUserMapper.getUserNameByOrgIdOrOrgCode(orgIdList);
    }

    /**
     * 岗位
     *
     * @param postList
     * @return
     */
    @Override
    public List<String> getUserNameByPost(List<String> postList) {
        if (CollUtil.isEmpty(postList)) {
            return Collections.emptyList();
        }
        return flowUserMapper.getUserNameByPost(postList);
    }
}
