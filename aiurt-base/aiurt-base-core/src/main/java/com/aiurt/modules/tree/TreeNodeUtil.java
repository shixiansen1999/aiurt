package com.aiurt.modules.tree;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;

import java.util.Iterator;
import java.util.List;

/**
 * @author fgw
 */
public  class TreeNodeUtil {


    /**
     * 过滤树
     * @param tree
     * @param list
     * @return
     */
    public static ITreeFilter filterTree(ITreeFilter tree, List list) {
        if(isRemoveNode(tree, list)){
            return null;
        }
        Iterator<ITreeFilter> iterator = tree.getChildrenList().iterator();
        while (iterator.hasNext()){
            ITreeFilter child = iterator.next();
            deleteNode(child, iterator, list);
        }
        return tree;
    }

    /**
     * 删除节点
     * @param child
     * @param iterator
     * @param list
     */
    private static  void deleteNode(ITreeFilter child, Iterator<ITreeFilter> iterator, List list) {
        if(isRemoveNode(child,list)){
            iterator.remove();
            return;
        }
        List<ITreeFilter> childrenList = child.getChildrenList();
        if(CollectionUtils.isEmpty(childrenList)){
            return;
        }
        Iterator<ITreeFilter> children = childrenList.iterator();
        while (children.hasNext()){
            ITreeFilter<T> childChild = children.next();
            deleteNode(childChild, children, list);
        }
    }


    /**
     * 判断该节点是否该删除
     * @param root
     * @param list  命中的节点
     * @return ture 需要删除  false 不能被删除
     */
    private static boolean isRemoveNode(ITreeFilter root, List list) {
        List<ITreeFilter> children = root.getChildrenList();
        // 叶子节点
        if(CollectionUtils.isEmpty(children)){
            return !list.contains(root.getParentKey());
        }
        // 子节点
        if(list.contains(root.getParentKey())){
            return false;
        }
        // 如果存在一个子节点不能删除，那么就不能删除
        boolean bool = true;
        for (ITreeFilter child : children) {
            if(!isRemoveNode(child, list)){
                bool = false;
                break;
            }
        }
        return bool;
    }
}
