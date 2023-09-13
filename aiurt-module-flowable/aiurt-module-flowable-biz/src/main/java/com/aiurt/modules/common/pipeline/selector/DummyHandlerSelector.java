package com.aiurt.modules.common.pipeline.selector;

import java.util.Collections;
import java.util.List;

/**
 * <p>虚设的选择器</p>
 * @author fgw
 */
public class DummyHandlerSelector implements HandlerSelector {
    /**
     * filter 匹配
     *
     * @param currentFilterName
     * @return
     */
    @Override
    public boolean matchHandler(String currentFilterName) {
        return false;
    }

    /**
     * 获取所有的filterNames
     *
     * @return
     */
    @Override
    public List<String> getHandlerNames() {
        return Collections.emptyList();
    }
}
