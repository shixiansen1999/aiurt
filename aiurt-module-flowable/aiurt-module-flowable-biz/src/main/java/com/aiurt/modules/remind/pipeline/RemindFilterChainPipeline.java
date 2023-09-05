package com.aiurt.modules.remind.pipeline;

import com.aiurt.modules.common.pipeline.DefaultFlowHandlerChain;
import com.aiurt.modules.common.pipeline.FlowHandler;

/**
 * @author fgw
 */
public class RemindFilterChainPipeline<T extends FlowHandler> {


    private DefaultFlowHandlerChain last;

    public RemindFilterChainPipeline() {
    }

    public DefaultFlowHandlerChain getFilterChain() {
        return this.last;
    }

    public RemindFilterChainPipeline addFirst(T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }

    public RemindFilterChainPipeline addFirst(String desc, T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }
}
