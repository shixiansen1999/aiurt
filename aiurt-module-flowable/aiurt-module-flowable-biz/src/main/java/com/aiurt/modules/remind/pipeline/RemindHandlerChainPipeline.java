package com.aiurt.modules.remind.pipeline;

import com.aiurt.modules.common.pipeline.DefaultFlowHandlerChain;
import com.aiurt.modules.common.pipeline.FlowHandler;

/**
 * @author fgw
 */
public class RemindHandlerChainPipeline<T extends FlowHandler> {


    private DefaultFlowHandlerChain last;

    public RemindHandlerChainPipeline() {
    }

    public DefaultFlowHandlerChain getFilterChain() {
        return this.last;
    }

    public RemindHandlerChainPipeline addFirst(T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }

    public RemindHandlerChainPipeline addFirst(String desc, T filter) {
        DefaultFlowHandlerChain newChain = new DefaultFlowHandlerChain(this.last, filter);
        this.last = newChain;
        return this;
    }
}
