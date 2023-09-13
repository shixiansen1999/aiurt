package com.aiurt.modules.user.getuser.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.handler.BaseUserHandler;
import com.aiurt.modules.user.handler.CustomVariableUserHandler;
import com.aiurt.modules.user.handler.SystemVariableUserHandler;
import com.aiurt.modules.user.getuser.service.DefaultSelectUserService;
import com.aiurt.modules.user.pipeline.FilterChainPipeline;
import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class DefaultSelectUserServiceImpl implements DefaultSelectUserService {

    @Autowired
    FilterChainPipeline filterChainPipeline;


    /**
     * 全部人员
     *
     * @param actCustomUser
     * @param variableData
     * @param processInstance
     * @return
     */
    @Override
    public List<String> getAllUserList(ActCustomUser actCustomUser, Map<String, Object> variableData, ProcessInstance processInstance) {
        List<String> filterNames = new ArrayList<>();
        filterNames.add(BaseUserHandler.class.getSimpleName());
        filterNames.add(CustomVariableUserHandler.class.getSimpleName());
        filterNames.add(SystemVariableUserHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);

        SelectUserContext context = new SelectUserContext(filterSelector);
        context.setCustomUser(actCustomUser);
        context.setVariable(variableData);
        context.setProcessInstance(processInstance);
        filterChainPipeline.getFilterChain().handle(context);
        if (CollUtil.isEmpty(context.getUserList())) {
            return Collections.emptyList();
        }
        List<String> list = context.getUserList().stream().distinct().collect(Collectors.toList());
        return list;
    }

    /**
     * 获取关系类型的人员
     *
     * @param actCustomUser
     * @param variableData
     * @param processInstance
     * @return
     */
    @Override
    public List<String> getUserList(ActCustomUser actCustomUser, Map<String, Object> variableData, ProcessInstance processInstance) {
        List<String> filterNames = new ArrayList<>();
        filterNames.add(CustomVariableUserHandler.class.getSimpleName());
        filterNames.add(SystemVariableUserHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);

        SelectUserContext context = new SelectUserContext(filterSelector);
        context.setCustomUser(actCustomUser);
        context.setVariable(variableData);
        context.setProcessInstance(processInstance);
        filterChainPipeline.getFilterChain().handle(context);
        if (CollUtil.isEmpty(context.getUserList())) {
            return Collections.emptyList();
        }
        List<String> list = context.getUserList().stream().distinct().collect(Collectors.toList());
        return list;
    }
}
