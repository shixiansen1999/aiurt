package com.aiurt.modules.user.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.systemvariable.impl.DefaultSystemVariableSelectUser;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  系统变量
 * @author fgw
 */
@Service
public class SystemVariableUserHandler extends AbstractFlowHandler<SelectUserContext> {

    @Autowired
    private DefaultSystemVariableSelectUser systemVariableSelectUser;
    /**
     * @param context
     */
    @Override
    public void handle(SelectUserContext context) {
        // 工厂
        ActCustomUser customUser = context.getCustomUser();

        ProcessInstance processInstance = context.getProcessInstance();

        List<String> userList = systemVariableSelectUser.getUserList(customUser, processInstance);

        context.addUserList(userList);
    }
}
