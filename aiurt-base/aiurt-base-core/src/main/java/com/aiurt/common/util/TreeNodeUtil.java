package com.aiurt.common.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 将列表结构组建为树结构的工具类。
 *
 * @param <T> 对象类型。
 * @param <K> 节点之间关联键的类型。
 * @author wgp
 * @date 2023-01-17
 */
@Data
public class TreeNodeUtil<T, K> {

    @ApiModelProperty("节点id")
    private K id;
    @ApiModelProperty("父节点id")
    private K parentId;
    @ApiModelProperty("当前节点数据")
    private T data;
    @ApiModelProperty("孩子节点")
    private List<TreeNodeUtil<T, K>> childList = new ArrayList<>();

    /**
     * 将列表结构组建为树结构的工具方法。
     *
     * @param dataList     数据列表结构。
     * @param idFunc       获取关联id的函数对象。
     * @param parentIdFunc 获取关联ParentId的函数对象。
     * @param root         根节点。
     * @param <T>          数据对象类型。
     * @param <K>          节点之间关联键的类型。
     * @return 源数据对象的树结构存储。
     */
    public static <T, K> List<TreeNodeUtil<T, K>> build(List<T> dataList,
                                                        Function<T, K> idFunc,
                                                        Function<T, K> parentIdFunc,
                                                        K root) {
        List<TreeNodeUtil<T, K>> treeNodeList = new ArrayList<>();
        for (T data : dataList) {
            // 如果存在父id和id相同的节点则不加入
            if (parentIdFunc.apply(data).equals(idFunc.apply(data))) {
                continue;
            }
            TreeNodeUtil<T, K> dataNode = new TreeNodeUtil<>();
            dataNode.setId(idFunc.apply(data));
            dataNode.setParentId(parentIdFunc.apply(data));
            dataNode.setData(data);
            treeNodeList.add(dataNode);
        }
        return root == null ? toBuildTreeWithoutRoot(treeNodeList) : toBuildTree(treeNodeList, root);
    }

    private static <T, K> List<TreeNodeUtil<T, K>> toBuildTreeWithoutRoot(List<TreeNodeUtil<T, K>> treeNodes) {
        Map<K, TreeNodeUtil<T, K>> treeNodeMap = new HashMap<>(treeNodes.size());
        treeNodeMap = treeNodes.stream().collect(Collectors.toMap(TreeNodeUtil::getId, o -> o));

        List<TreeNodeUtil<T, K>> treeNodeList = new ArrayList<>();
        for (TreeNodeUtil<T, K> treeNode : treeNodes) {
            TreeNodeUtil<T, K> parentNode = treeNodeMap.get(treeNode.getParentId());
            if (parentNode == null) {
                treeNodeList.add(treeNode);
            } else {
                parentNode.add(treeNode);
            }
        }
        return treeNodeList;
    }

    private static <T, K> List<TreeNodeUtil<T, K>> toBuildTree(List<TreeNodeUtil<T, K>> treeNodes, K root) {
        List<TreeNodeUtil<T, K>> treeNodeList = new ArrayList<>();
        for (TreeNodeUtil<T, K> treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                treeNodeList.add(treeNode);
            }
            for (TreeNodeUtil<T, K> it : treeNodes) {
                if (it.getParentId() == treeNode.getId()) {
                    if (treeNode.getChildList() == null) {
                        treeNode.setChildList(new ArrayList<>());
                    }
                    treeNode.add(it);
                }
            }
        }
        return treeNodeList;
    }

    private void add(TreeNodeUtil<T, K> node) {
        childList.add(node);
    }
}
