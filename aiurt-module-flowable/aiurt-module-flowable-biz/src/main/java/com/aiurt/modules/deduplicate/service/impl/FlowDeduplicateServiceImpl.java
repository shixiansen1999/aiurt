package com.aiurt.modules.deduplicate.service.impl;

import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.deduplicate.pipeline.DeduplicateHandlerChainPipeline;
import com.aiurt.modules.deduplicate.service.IFlowDeduplicateService;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.handlers.BuildContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Service
public class FlowDeduplicateServiceImpl implements IFlowDeduplicateService {

    @Autowired
    private DeduplicateHandlerChainPipeline deduplicateHandlerChainPipeline;
    /**
     * 审批人去重
     *
     * @param processInstanceId
     * @param taskId
     */
    @Override
    public void handler(String processInstanceId, String taskId) {
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(FlowDeduplicateContext.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowDeduplicateContext context = new FlowDeduplicateContext(filterSelector);

        deduplicateHandlerChainPipeline.getFilterChain().handle(context);
    }
}
