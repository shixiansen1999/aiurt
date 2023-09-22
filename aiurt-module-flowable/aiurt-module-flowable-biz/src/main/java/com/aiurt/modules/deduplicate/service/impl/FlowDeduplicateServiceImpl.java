package com.aiurt.modules.deduplicate.service.impl;

import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.deduplicate.handler.*;
import com.aiurt.modules.deduplicate.pipeline.DeduplicateHandlerChainPipeline;
import com.aiurt.modules.deduplicate.service.IFlowDeduplicateService;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.handlers.BuildContextHandler;
import org.flowable.task.api.Task;
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
     * @param task
     */
    @Override
    public void handler(Task task) {
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(BuildDeduplicateContextHandler.class.getSimpleName());
        filterNames.add(MultiInstanceRuleVerifyHandler.class.getSimpleName());
        filterNames.add(DuplicateRuleVerifyHandler.class.getSimpleName());
        filterNames.add(BackNodeRuleVerifyHandler.class.getSimpleName());
        filterNames.add(AutoCompleteHandler.class.getSimpleName());
        filterNames.add(ApprovalRequirementRuleVerifyHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowDeduplicateContext context = new FlowDeduplicateContext(filterSelector);
        context.setTask(task);
        deduplicateHandlerChainPipeline.getFilterChain().handle(context);

    }
}
