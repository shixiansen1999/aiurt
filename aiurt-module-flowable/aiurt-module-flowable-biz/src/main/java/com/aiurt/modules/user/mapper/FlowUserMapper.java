package com.aiurt.modules.user.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 版本管理
 * @Author: aiurt
 * @Date:   2022-07-15
 * @Version: V1.0
 */
public interface FlowUserMapper {

    /**
     * 用户id 查询 username
     * @param userIdList 用户id
     * @return
     */
    List<String> getUserNameByUserId(@Param("userIdList") List<String> userIdList);

    /**
     * roleid 查询username
     * @param roleIdList roleid
     * @return
     */
    List<String> getUserNameByRoleId(@Param("roleIdList") List<String> roleIdList);

    /**
     * orgId 查询username
     * @param orgIdList orgId
     * @return
     */
    List<String> getUserNameByOrgId(@Param("orgIdList")List<String> orgIdList);

    /**
     * 根据用户名或者账号或者用户id 获取用户账号
     * @param name  用户名或者账号或者用户id
     * @return
     */
    List<String> getUserName(@Param("name") String name);
}
