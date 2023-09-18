package com.aiurt.modules.user.getuser.systemvariable.impl;

import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.getuser.systemvariable.ISystemVariableSelectUserService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fgw
 */
@Service
public class SystemVariableGetUserFactory {

    private Map<FlowUserRelationEnum, ISystemVariableSelectUserService> beanMap = new ConcurrentHashMap<>(16);


    public void registerBean(FlowUserRelationEnum relationEnum, ISystemVariableSelectUserService getTaskUserStrategy) {
        beanMap.put(relationEnum, getTaskUserStrategy);
    }

    public ISystemVariableSelectUserService getBean(FlowUserRelationEnum relationEnum) {
        return beanMap.get(relationEnum);
    }
}
