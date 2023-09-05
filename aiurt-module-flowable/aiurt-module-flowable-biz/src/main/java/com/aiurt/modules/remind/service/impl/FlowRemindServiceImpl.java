package com.aiurt.modules.remind.service.impl;

import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.pipeline.RemindFilterChainPipeline;
import com.aiurt.modules.remind.service.IFlowRemindService;
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
    private RemindFilterChainPipeline remindFilterChainPipeline;

    /**
     * 手工催办
     *
     * @param processInstanceId
     */
    @Override
    public void manualRemind(String processInstanceId) {
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(BuildContextHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowRemindContext context = new FlowRemindContext(filterSelector);

        remindFilterChainPipeline.getFilterChain().handle(context);
    }
}
