package com.aiurt.modules.common.pipeline.selector;

import java.util.List;

/**
 * <p>过滤器的选择器，确定着责任链由那些拦截器组成</p>
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
