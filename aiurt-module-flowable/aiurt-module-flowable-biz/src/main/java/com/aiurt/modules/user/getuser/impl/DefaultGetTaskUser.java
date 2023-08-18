package com.aiurt.modules.user.getuser.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.GetTaskUser;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class DefaultGetTaskUser implements GetTaskUser {

    @Autowired
    private ISysBaseAPI sysBaseApi;

    /**
     * 获取用户下
     *
     * @param customUser
     * @return
     */
    @Override
    public List<String> getUser(ActCustomUser customUser) {
        String userName = customUser.getUserName();
        String post = customUser.getPost();
        String orgId = customUser.getOrgId();
        String roleCode = customUser.getRoleCode();

        List<String> resultList = new ArrayList<>();
        List<String> list = StrUtil.split(userName, ',');
        List<String>  userNameList = sysBaseApi.getUserNameByParams(StrUtil.split(roleCode, ','),
                StrUtil.split(orgId, ','), StrUtil.split(post, ','));
        resultList.addAll(list);
        resultList.addAll(userNameList);
        resultList = resultList.stream().distinct().collect(Collectors.toList());
        return resultList;
    }
}
