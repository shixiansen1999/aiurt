package com.aiurt.modules.tree;


import java.util.List;

/**
 * @author fgw
 */
public interface ITreeFilter<T> {

    /**
     * 获取主键
     * @return
     */
    T getPrimaryKey();

    /**
     * 获取父节点的id
     * @return
     */
    T getParentKey();

    /**
     * 获取子节点
     * @return
     */
    List<? extends ITreeFilter> getChildrenList();
}
