package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.getuser.strategy.SystemVariableSelectUserStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fgw
 */
@Service
public class SystemVariableGetUserFactory {

    private Map<FlowUserRelationEnum, SystemVariableSelectUserStrategy> beanMap = new ConcurrentHashMap<>(16);


    public void registerBean(FlowUserRelationEnum relationEnum, SystemVariableSelectUserStrategy getTaskUserStrategy) {
        beanMap.put(relationEnum, getTaskUserStrategy);
    }

    public SystemVariableSelectUserStrategy getBean(FlowUserRelationEnum relationEnum) {
        return beanMap.get(relationEnum);
    }
}
