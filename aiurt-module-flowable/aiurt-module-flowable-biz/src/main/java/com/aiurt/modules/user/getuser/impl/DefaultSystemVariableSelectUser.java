package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.getuser.strategy.InitiatorDepartmentLeaderStrategy;
import com.aiurt.modules.user.getuser.strategy.SuperiorLeaderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author fgw
 */
@Service
public class DefaultSystemVariableSelectUser extends AbsSystemVariableSelectUser {

    @Autowired
    private InitiatorDepartmentLeaderStrategy initiatorDepartmentLeader;

    @Autowired
    private SystemVariableGetUserFactory systemVariableGetUserFactory;

    @Autowired
    private SuperiorLeaderStrategy superiorLeader;

    @PostConstruct
    public void init() {
        systemVariableGetUserFactory.registerBean(FlowUserRelationEnum.INITIATOR_DEPARTMENT_LEADER, initiatorDepartmentLeader);
        systemVariableGetUserFactory.registerBean(FlowUserRelationEnum.SUPERIOR_LEADER_OF_INITIATOR, superiorLeader);
    }
}
