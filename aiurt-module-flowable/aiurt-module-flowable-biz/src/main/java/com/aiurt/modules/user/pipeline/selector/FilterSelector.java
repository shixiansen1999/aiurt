package com.aiurt.modules.user.pipeline.selector;

import java.util.List;

/**
 * <p>虚设的选择器</p>
 * @author fgw
 */
public interface FilterSelector {

    /**
     * filter 匹配
     * @param currentFilterName
     * @return
     */
    boolean matchFilter(String currentFilterName);

    /**
     * 获取所有的filterNames
     * @return
     */
    List<String> getFilterNames();
}
