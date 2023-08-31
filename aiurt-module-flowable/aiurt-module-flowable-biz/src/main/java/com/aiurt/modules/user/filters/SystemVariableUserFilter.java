package com.aiurt.modules.user.filters;

import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.impl.DefaultSystemVariableSelectUser;
import com.aiurt.modules.user.pipeline.AbstractUserFilter;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  系统变量
 * @author fgw
 */
@Service
public class SystemVariableUserFilter extends AbstractUserFilter<SelectUserContext> {

    @Autowired
    private DefaultSystemVariableSelectUser systemVariableSelectUser;
    /**
     * @param context
     */
    @Override
    protected void handle(SelectUserContext context) {
        // 工厂 + 策略模型
        ActCustomUser customUser = context.getCustomUser();

        ProcessInstance processInstance = context.getProcessInstance();

        List<String> userList = systemVariableSelectUser.getUserList(customUser, processInstance);

        context.addUserList(userList);
    }
}
