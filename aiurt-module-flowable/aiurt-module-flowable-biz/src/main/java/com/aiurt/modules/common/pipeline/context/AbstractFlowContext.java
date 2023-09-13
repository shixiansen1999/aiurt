package com.aiurt.modules.common.pipeline.context;

import com.aiurt.modules.common.pipeline.selector.HandlerSelector;

/**
 * @author fgw
 */
public abstract class AbstractFlowContext implements FlowContext {

    private final HandlerSelector selector;

    public AbstractFlowContext(HandlerSelector selector) {
        this.selector = selector;
    }

    /**
     * 获取过滤器选择器
     *
     * @return
     */
    @Override
    public HandlerSelector getHandlerSelector() {
        return selector;
    }
}
