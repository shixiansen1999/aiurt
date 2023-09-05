package com.aiurt.modules.remind.pipeline.context;

import com.aiurt.modules.common.pipeline.selector.FilterSelector;

/**
 * 催办上下文
 * @author fgw
 */
public interface RemindContext {

    /**
     * 获取过滤器选择器
     * @return
     */
    FilterSelector getFilterSelector();

    /**
     * 是否继续链
     * @return
     */
    boolean continueChain();
}
