package com.aiurt.modules.recall.service.impl;

import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.recall.context.FlowRecallContext;
import com.aiurt.modules.recall.dto.RecallReqDTO;
import com.aiurt.modules.recall.pipeline.RecallHandlerChainPipeline;
import com.aiurt.modules.recall.service.IFlowRecallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Service
public class FlowRecallServiceImpl implements IFlowRecallService {

    @Autowired
    private RecallHandlerChainPipeline recallHandlerChainPipeline;

    /**
     * 撤回
     *
     * @param recallReqDTO
     */
    @Override
    public void recall(RecallReqDTO recallReqDTO) {
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(FlowDeduplicateContext.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowRecallContext context = new FlowRecallContext(filterSelector);

        recallHandlerChainPipeline.getFilterChain().handle(context);
    }
}
