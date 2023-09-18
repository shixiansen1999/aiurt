package com.aiurt.modules.common.pipeline.selector;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
public class LocalListBasedHandlerSelector implements HandlerSelector {

    private List<String> handlerNames = Lists.newArrayList();

    /**
     * filter 匹配
     *
     * @param classSimpleName
     * @return
     */
    @Override
    public boolean matchHandler(String classSimpleName) {
        return handlerNames.stream().anyMatch(s -> Objects.equals(s, classSimpleName));
    }

    /**
     * 获取所有的filterNames
     *
     * @return
     */
    @Override
    public List<String> getHandlerNames() {
        return this.handlerNames;
    }

    public void addHandler(String clsNames){
        handlerNames.add(clsNames);
    }

    public void addHandlers(List<String> filterNames){
        filterNames.addAll(filterNames);
    }

    public LocalListBasedHandlerSelector(){}

    public LocalListBasedHandlerSelector(List<String> handlerNames){
        this.handlerNames = handlerNames;
    }
}
