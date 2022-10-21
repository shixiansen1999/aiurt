package com.aiurt.modules.user.service;

import java.util.List;

/**
 * @author fgw
 */
public interface IFlowUserService {

    /**
     * 用户id 查询 username
     * @param userIdList 用户id
     * @return
     */
    List<String> getUserNameByUserId(List<String> userIdList);

    /**
     * roleid 查询username
     * @param roleIdList roleid
     * @return
     */
    List<String> getUserNameByRoleId(List<String> roleIdList);

    /**
     * orgId 查询username
     * @param orgIdList orgId
     * @return
     */
    List<String> getUserNameByOrgId(List<String> orgIdList);

    /**
     * 根据用户名或者账号或者用户id 获取用户账号
     * @param name  用户名或者账号或者用户id
     * @return
     */
    List<String> getUserName(String name);
}
