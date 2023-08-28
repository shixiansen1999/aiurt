package com.aiurt.modules.tree;

/**
 * @author:wgp
 * @create: 2023-08-28 15:38
 * @Description: 树形结构构建工具类
 */

import cn.hutool.core.collection.CollectionUtil;
import org.jeecg.common.system.vo.SelectTreeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreeBuilder {

    /**
     * 构建树形结构
     *
     * @param nodeList 树节点列表
     * @return 构建后的树形结构列表
     */
    public static List<SelectTreeModel> buildTree(List<SelectTreeModel> nodeList) {
        Map<String, SelectTreeModel> SelectTreeModelMap = new HashMap<>(32);

        // 遍历节点列表，建立父子关系并构建树结构
        for (SelectTreeModel item : nodeList) {
            SelectTreeModel parent = SelectTreeModelMap.get(item.getParentId());

            // 如果父节点不存在，则创建一个新的父节点
            if (parent == null) {
                parent = new SelectTreeModel();
                SelectTreeModelMap.put(item.getParentId(), parent);
            }

            SelectTreeModel existingNode = SelectTreeModelMap.get(item.getKey());

            // 如果已存在节点存在，则将当前节点的子节点列表设置为已存在节点的子节点列表
            if (existingNode != null) {
                item.setChildren(existingNode.getChildren());
            }

            SelectTreeModelMap.put(item.getKey(), item);
            parent.addChild(item);
        }

        List<SelectTreeModel> resultList = new ArrayList<>();

        // 获取所有根节点
        List<SelectTreeModel> rootChildren = SelectTreeModelMap.values().stream()
                .filter(entity -> entity.getParentId() == null)
                .collect(Collectors.toList());

        // 遍历根节点，将其添加到结果列表中
        for (SelectTreeModel entity : rootChildren) {
            if (CollectionUtil.isNotEmpty(entity.getChildren())) {
                resultList.addAll(entity.getChildren());
            }
        }

        return resultList;
    }
}
