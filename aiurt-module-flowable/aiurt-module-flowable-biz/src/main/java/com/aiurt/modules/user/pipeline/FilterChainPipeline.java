package com.aiurt.modules.user.pipeline;

import com.aiurt.modules.common.pipeline.DefaultFlowHandlerChain;
import com.aiurt.modules.common.pipeline.FlowHandler;

/**
 * @author fgw
 */
public class FilterChainPipeline<T extends FlowHandler>{

    private DefaultFlowHandlerChain last;

    public FilterChainPipeline() {
    }

    public DefaultFlowHandlerChain getFilterChain() {
        return this.last;
    }

    public FilterChainPipeline addFirst(T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }

    public FilterChainPipeline addFirst(String desc, T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }
}
