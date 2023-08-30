package com.aiurt.modules.user.pipeline.context;

import com.aiurt.modules.user.pipeline.selector.FilterSelector;

/**
 * <p>上下文对象</p>
 * @author fgw
 */
public interface UserContext {

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
