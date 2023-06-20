package com.aiurt.boot.core.common.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 排序对象封装
 */
public class Sort {
    private List<Order> orders = null;


    public List<Order> listOrders() {
        return orders;
    }


    public Sort(Order... ods) {
        orders = new ArrayList<>();
        for (Order od : ods) {
            orders.add(od);
        }
    }

    public Sort and(Sort sort) {
        if (orders == null) {
            orders = new ArrayList<>();
        }
        sort.orders.forEach(order -> orders.add(order));
        return this;
    }

    @Getter
    @AllArgsConstructor
    public static class Order implements Serializable {
        private final SortOrder direction;
        private final String property;
    }
}
