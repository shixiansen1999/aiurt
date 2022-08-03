package com.aiurt.modules.utils;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.utils.BaseFlowIdentityExtHelper;
import com.aiurt.modules.utils.FlowCustomExtFactory;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 为流程提供所需的用户身份相关的等扩展信息的帮助类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Component
public class FlowIdentityExtHelper implements BaseFlowIdentityExtHelper {


    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    @PostConstruct
    public void doRegister() {
        flowCustomExtFactory.registerFlowIdentityExtHelper(this);
    }

    @Override
    public Long getLeaderDeptPostId(Long deptId) {
       return null;
    }

    @Override
    public Long getUpLeaderDeptPostId(Long deptId) {
        return null;
    }

    @Override
    public Map<String, String> getDeptPostIdMap(Long deptId, Set<String> postIdSet) {
       return null;
    }

    @Override
    public Map<String, String> getUpDeptPostIdMap(Long deptId, Set<String> postIdSet) {
        return null;
    }

    @Override
    public Set<String> getUsernameListByRoleIds(Set<String> roleIdSet) {
        Set<String> usernameSet = new HashSet<>();
       /* Set<Long> roleIdSet2 = roleIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long roleId : roleIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByRoleId(roleId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }*/
        return usernameSet;
    }

    @Override
    public Set<String> getUsernameListByDeptIds(Set<String> deptIdSet) {
        Set<String> usernameSet = new HashSet<>();
        /*Set<Long> deptIdSet2 = deptIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        for (Long deptId : deptIdSet2) {
            SysUser filter = new SysUser();
            filter.setDeptId(deptId);
            filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
            List<SysUser> userList = sysUserService.getSysUserList(filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }*/
        return usernameSet;
    }

    @Override
    public Set<String> getUsernameListByPostIds(Set<String> postIdSet) {
        Set<String> usernameSet = new HashSet<>();
        /*Set<Long> postIdSet2 = postIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long postId : postIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByPostId(postId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }*/
        return usernameSet;
    }

    @Override
    public Set<String> getUsernameListByDeptPostIds(Set<String> deptPostIdSet) {
        Set<String> usernameSet = new HashSet<>();
       /* Set<Long> deptPostIdSet2 = deptPostIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
        SysUser filter = new SysUser();
        filter.setUserStatus(SysUserStatus.STATUS_NORMAL);
        for (Long deptPostId : deptPostIdSet2) {
            List<SysUser> userList = sysUserService.getSysUserListByDeptPostId(deptPostId, filter, null);
            this.extractAndAppendUsernameList(usernameSet, userList);
        }*/
        return usernameSet;
    }

    @Override
    public Boolean supprtDataPerm() {
        return true;
    }

    @Override
    public Map<String, String> mapUserShowNameByLoginName(Set<String> loginNameSet) {
        if (CollUtil.isEmpty(loginNameSet)) {
            return new HashMap<>(1);
        }
        Map<String, String> resultMap = new HashMap<>(loginNameSet.size());
       /* List<SysUser> userList = sysUserService.getInList("loginName", loginNameSet);
        userList.forEach(user -> resultMap.put(user.getLoginName(), user.getShowName()));*/
        return resultMap;
    }

    private void extractAndAppendUsernameList(Set<String> resultUsernameList, List<LoginUser> userList) {
        List<String> usernameList = userList.stream().map(LoginUser::getUsername).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(usernameList)) {
            resultUsernameList.addAll(usernameList);
        }
    }
}
