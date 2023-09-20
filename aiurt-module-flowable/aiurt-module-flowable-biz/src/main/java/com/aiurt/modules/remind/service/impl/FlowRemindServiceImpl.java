package com.aiurt.modules.remind.service.impl;

import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.handlers.RemindRecordUpdateHandler;
import com.aiurt.modules.remind.handlers.RemindRuleVerifyHandler;
import com.aiurt.modules.remind.handlers.RemindSendMessageHandler;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;
import com.aiurt.modules.remind.service.IFlowRemindService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Service
public class FlowRemindServiceImpl implements IFlowRemindService {

    @Autowired
    private RemindHandlerChainPipeline remindHandlerChainPipeline;

    /**
     * 手工催办
     *
     * @param processInstanceId
     */
    @Override
    public void manualRemind(String processInstanceId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(BuildContextHandler.class.getSimpleName());
        filterNames.add(RemindRecordUpdateHandler.class.getSimpleName());
        filterNames.add(RemindRuleVerifyHandler.class.getSimpleName());
        filterNames.add(RemindSendMessageHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowRemindContext context = new FlowRemindContext(filterSelector);
        context.setProcessInstanceId(processInstanceId);
        context.setLoginName(loginUser.getUsername());
        context.setRealName(loginUser.getRealname());

        remindHandlerChainPipeline.getFilterChain().handle(context);
    }
}
