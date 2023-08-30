package com.aiurt.modules.user.pipeline.context;

import com.aiurt.modules.user.pipeline.selector.FilterSelector;

/**
 * @author fgw
 */
public abstract class AbstractUserContext implements UserContext{

    private final FilterSelector selector;

    public AbstractUserContext(FilterSelector selector) {
        this.selector = selector;
    }

    /**
     * 获取过滤器选择器
     *
     * @return
     */
    @Override
    public FilterSelector getFilterSelector() {
        return selector;
    }

}
