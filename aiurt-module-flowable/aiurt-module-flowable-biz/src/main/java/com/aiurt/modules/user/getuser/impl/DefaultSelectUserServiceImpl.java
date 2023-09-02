package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.filters.BaseUserFilter;
import com.aiurt.modules.user.filters.CustomVariableUserFilter;
import com.aiurt.modules.user.filters.SystemVariableUserFilter;
import com.aiurt.modules.user.getuser.DefaultSelectUserService;
import com.aiurt.modules.user.pipeline.FilterChainPipeline;
import com.aiurt.modules.common.pipeline.selector.LocalListBasedFilterSelector;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        filterNames.add(BaseUserFilter.class.getSimpleName());
        filterNames.add(CustomVariableUserFilter.class.getSimpleName());
        filterNames.add(SystemVariableUserFilter.class.getSimpleName());
        LocalListBasedFilterSelector filterSelector = new LocalListBasedFilterSelector(filterNames);

        SelectUserContext context = new SelectUserContext(filterSelector);
        context.setCustomUser(actCustomUser);
        context.setVariable(variableData);
        context.setProcessInstance(processInstance);
        filterChainPipeline.getFilterChain().handle(context);
        return context.getUserList();
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
        filterNames.add(CustomVariableUserFilter.class.getSimpleName());
        filterNames.add(SystemVariableUserFilter.class.getSimpleName());
        LocalListBasedFilterSelector filterSelector = new LocalListBasedFilterSelector(filterNames);

        SelectUserContext context = new SelectUserContext(filterSelector);
        context.setCustomUser(actCustomUser);
        context.setVariable(variableData);
        context.setProcessInstance(processInstance);
        filterChainPipeline.getFilterChain().handle(context);
        return context.getUserList();
    }
}
