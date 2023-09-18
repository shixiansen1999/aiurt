package com.aiurt.modules.common.pipeline.context;

import com.aiurt.modules.common.pipeline.selector.HandlerSelector;

/**
 * @author fgw
 */
public interface FlowContext {
    /**
     * 获取过滤器选择器
     * @return
     */
    HandlerSelector getHandlerSelector();

    /**
     * 是否继续链
     * @return
     */
    boolean continueChain();
}
