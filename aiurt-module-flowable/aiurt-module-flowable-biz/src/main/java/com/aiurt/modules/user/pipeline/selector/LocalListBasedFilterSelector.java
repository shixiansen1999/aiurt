package com.aiurt.modules.user.pipeline.selector;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
public class LocalListBasedFilterSelector implements FilterSelector{

    private List<String> filterNames = Lists.newArrayList();

    /**
     * filter 匹配
     *
     * @param classSimpleName
     * @return
     */
    @Override
    public boolean matchFilter(String classSimpleName) {
        return filterNames.stream().anyMatch(s -> Objects.equals(s, classSimpleName));
    }

    /**
     * 获取所有的filterNames
     *
     * @return
     */
    @Override
    public List<String> getFilterNames() {
        return this.filterNames;
    }

    public void addFilter(String clsNames){
        filterNames.add(clsNames);
    }

    public void addFilters(List<String> filterNames){
        filterNames.addAll(filterNames);
    }

    public LocalListBasedFilterSelector(){}

    public LocalListBasedFilterSelector(List<String> filterNames){
        this.filterNames = filterNames;
    }
}
