package com.aiurt.modules.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.user.mapper.FlowUserMapper;
import com.aiurt.modules.user.service.IFlowUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
}
