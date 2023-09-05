package com.aiurt.modules.user.getuser.systemvariable.impl;

import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.getuser.systemvariable.impl.AbsSystemVariableSelectUser;
import com.aiurt.modules.user.getuser.systemvariable.impl.InitiatorDepartmentLeaderServiceImpl;
import com.aiurt.modules.user.getuser.systemvariable.impl.SuperiorLeaderServiceImpl;
import com.aiurt.modules.user.getuser.systemvariable.impl.SystemVariableGetUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author fgw
 */
@Service
public class DefaultSystemVariableSelectUser extends AbsSystemVariableSelectUser {

    @Autowired
    private InitiatorDepartmentLeaderServiceImpl initiatorDepartmentLeader;

    @Autowired
    private SystemVariableGetUserFactory systemVariableGetUserFactory;

    @Autowired
    private SuperiorLeaderServiceImpl superiorLeader;

    @PostConstruct
    public void init() {
        systemVariableGetUserFactory.registerBean(FlowUserRelationEnum.INITIATOR_DEPARTMENT_LEADER, initiatorDepartmentLeader);
        systemVariableGetUserFactory.registerBean(FlowUserRelationEnum.SUPERIOR_LEADER_OF_INITIATOR, superiorLeader);
    }
}
