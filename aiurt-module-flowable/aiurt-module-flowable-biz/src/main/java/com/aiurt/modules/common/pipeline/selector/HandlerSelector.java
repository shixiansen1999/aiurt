package com.aiurt.modules.common.pipeline.selector;

import java.util.List;

/**
 * <p>handler的选择器，确定着责任链由那些拦截器组成</p>
 * @author fgw
 */
public interface HandlerSelector {

    /**
     * handler 匹配
     * @param currentFilterName
     * @return
     */
    boolean matchHandler(String currentFilterName);

    /**
     * 获取所有的handlerNames
     * @return
     */
    List<String> getHandlerNames();
}
