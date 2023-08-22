package com.aiurt.modules.user.getuser.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.user.getuser.SystemVariableSelectUserStrategy;
import com.aiurt.modules.user.service.IFlowUserService;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 发起人上级部门领导
 * @author fgw
 */
@Service
public class SuperiorLeaderStrategy implements SystemVariableSelectUserStrategy {

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private IFlowUserService flowUserService;


    /**
     * 系统变量
     *
     * @param processInstance
     * @return
     */
    @Override
    public List<String> getUser(ProcessInstance processInstance) {
        String userName = processInstance.getStartUserId();
        if (StrUtil.isBlank(userName)) {
            return Collections.emptyList();
        }
        LoginUser loginUser = sysBaseApi.queryUser(userName);

        if (Objects.isNull(loginUser)) {
            return Collections.emptyList();
        }
        if (StrUtil.isBlank(loginUser.getOrgId())) {
            return Collections.emptyList();
        }
        return flowUserService.getParentManageUserName(loginUser.getOrgId());
    }
}
