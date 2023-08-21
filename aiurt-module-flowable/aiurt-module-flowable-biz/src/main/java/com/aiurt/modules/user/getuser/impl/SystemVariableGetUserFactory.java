package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.getuser.SystemVariableGetTaskUserStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fgw
 */
@Service
public class SystemVariableGetUserFactory {

    private Map<FlowUserRelationEnum, SystemVariableGetTaskUserStrategy> beanMap = new ConcurrentHashMap<>(16);


    public void registerBean(FlowUserRelationEnum relationEnum, SystemVariableGetTaskUserStrategy getTaskUserStrategy) {
        beanMap.put(relationEnum, getTaskUserStrategy);
    }

    public SystemVariableGetTaskUserStrategy getBean(FlowUserRelationEnum relationEnum) {
        return beanMap.get(relationEnum);
    }
}
