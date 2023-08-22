package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.SelectUser;
import com.aiurt.modules.user.getuser.dto.SelectionParameters;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class RelationSelectUser {

    @Resource(name = "defaultSystemVariableSelectUser")
    private SelectUser defaultSystemVariableSelectUser;

    @Resource(name = "customVariableSelectUser")
    private SelectUser customVariableSelectUser;




    public List<String> selectList(ActCustomUser customUser, ProcessInstance processInstance, Map<String, Object> variable) {
        List<String> list = new ArrayList<>();
        SelectionParameters selectionParameters = new SelectionParameters();
        selectionParameters.setCustomUser(customUser);
        selectionParameters.setProcessInstance(processInstance);
        selectionParameters.setVariable(variable);

        list.addAll(defaultSystemVariableSelectUser.getUserList(selectionParameters));
        list.addAll(customVariableSelectUser.getUserList(selectionParameters));
        list = list.stream().distinct().collect(Collectors.toList());
        return list;
    }



}
