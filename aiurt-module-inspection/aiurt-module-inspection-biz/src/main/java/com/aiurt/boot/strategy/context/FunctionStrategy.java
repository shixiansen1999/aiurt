package com.aiurt.boot.strategy.context;

/**
 * @author wgp
 * @Title:
 * @Description: 自定义函数式接口
 * @date 2022/12/79:22
 */
@FunctionalInterface
public interface FunctionStrategy<A, B, C, D> {
    void apply(A a, B b, C c, D d);
}
