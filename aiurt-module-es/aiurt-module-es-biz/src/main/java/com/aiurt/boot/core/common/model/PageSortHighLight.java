package com.aiurt.boot.core.common.model;

import lombok.Data;

/**
 * 分页+排序+高亮对象封装
 */
@Data
public class PageSortHighLight {
    private int pageNo;
    private int pageSize;
    Sort sort = new Sort();
    private HighLight highLight = new HighLight();

    public PageSortHighLight(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageSortHighLight(int pageNo, int pageSize, Sort sort) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.sort = sort;
    }
}
