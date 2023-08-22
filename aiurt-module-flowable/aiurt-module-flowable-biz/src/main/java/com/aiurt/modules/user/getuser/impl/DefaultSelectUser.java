package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.SelectUser;
import com.aiurt.modules.user.getuser.dto.SelectionParameters;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class DefaultSelectUser {

    @Autowired
    private List<SelectUser> selectUserList;

    public List<String> selectAllList(ActCustomUser customUser, ProcessInstance processInstance, Map<String, Object> variable) {
        SelectionParameters selectionParameters = new SelectionParameters();
        selectionParameters.setCustomUser(customUser);
        selectionParameters.setProcessInstance(processInstance);
        selectionParameters.setVariable(variable);
        List<String> resultList = selectUserList.stream().map(selectUser -> {
            List<String> userList = selectUser.getUserList(selectionParameters);
            return userList;
        }).flatMap(Collection::stream).collect(Collectors.toList());
        resultList = resultList.stream().distinct().collect(Collectors.toList());
        return resultList;
    }
}
