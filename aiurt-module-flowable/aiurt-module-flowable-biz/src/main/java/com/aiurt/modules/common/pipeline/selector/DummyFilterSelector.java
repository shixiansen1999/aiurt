package com.aiurt.modules.common.pipeline.selector;

import java.util.Collections;
import java.util.List;

/**
 * <p>虚设的选择器</p>
 * @author fgw
 */
public class DummyFilterSelector implements FilterSelector{
    /**
     * filter 匹配
     *
     * @param currentFilterName
     * @return
     */
    @Override
    public boolean matchFilter(String currentFilterName) {
        return false;
    }

    /**
     * 获取所有的filterNames
     *
     * @return
     */
    @Override
    public List<String> getFilterNames() {
        return Collections.emptyList();
    }
}
