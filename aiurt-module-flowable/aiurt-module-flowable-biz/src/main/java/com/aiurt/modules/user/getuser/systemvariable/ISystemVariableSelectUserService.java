package com.aiurt.modules.user.getuser.systemvariable;

import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;

/**
 * @author fgw
 */
public interface ISystemVariableSelectUserService {

    /**
     * 系统变量
     * @param processInstance
     * @return 用户列表
     */
    List<String> getUser(ProcessInstance processInstance);
}
