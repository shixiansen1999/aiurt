package com.aiurt.common.util;


import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.api.vo.TreeEntity;
import com.aiurt.common.api.vo.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author wgp
 */
public class TreeUtils {


    /**
     * 构造树，有根节点
     *
     * @param treeList
     * @return
     */
    public static List<TreeEntity> selectList(List<TreeEntity> treeList) {
        //1、查出所有分类
        // 查询一级菜单
        List<TreeEntity> level1Menus = treeList.stream().filter(treeEntity -> (
                treeEntity.getParentId() == 0
        )).map((menu) -> {
            menu.setChildren(ObjectUtil.isNotEmpty(getChildrens(menu, treeList))?getChildrens(menu, treeList):new ArrayList<>());
            return menu;
        }).collect(Collectors.toList());
        // 查询子菜单
        return level1Menus;
    }

    public static List<TreeEntity> selectListByTeamId(List<TreeEntity> treeList, Integer teamId) {
        //1、查出所有分类
        // 查询一级菜单
        List<TreeEntity> level1Menus = treeList.stream().filter(treeEntity -> (
                treeEntity.getId().equals(teamId)
        )).map((menu) -> {
            menu.setChildren(getChildrens(menu, treeList));
            return menu;
            //排序
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        // 查询子菜单
        return level1Menus;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root
     * @param all
     * @return
     */
    public static List<TreeEntity> getChildrens(TreeEntity root, List<TreeEntity> all) {
        List<TreeEntity> children = all.stream().filter(treeEntity -> {
            return treeEntity.getParentId().equals(root.getId());
        }).map(treeEntity -> {
            //1、找到子菜单
            treeEntity.setChildren(ObjectUtil.isNotEmpty(getChildrens(treeEntity, all))?getChildrens(treeEntity, all):new ArrayList<>());
            return treeEntity;
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 查询某个节点的父节点
     *
     * @param nodeId   查询节点
     * @param treeList 全部数据
     * @return
     */
    public static List<TreeEntity> selectNode(Integer nodeId, List<TreeEntity> treeList) {
        //1、查出所有分类
        // 查询一级菜单
        List<TreeEntity> level1Menus = treeList.stream().filter(new Predicate<TreeEntity>() {
            @Override
            public boolean test(TreeEntity treeEntity) {
                if (treeEntity.getId().equals(nodeId)) {
                    return true;
                }
                return false;
            }
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu, treeList));
            return menu;
            //排序
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        // 查询子菜单
        return level1Menus;
    }

    /**
     * 获取某个父节点下面的所有子节点
     *
     * @param menuList  全部数据
     * @param pid       要查询的根节点
     * @param childMenu 最后返回的集合
     * @return childMenu 集合里面不包含有pid
     */
    public static List<TreeNode> treeMenuList(List<TreeNode> menuList, Integer pid, List<TreeNode> childMenu) {
        for (TreeNode treeNode : menuList) {
            //遍历出父id等于参数的id，add进子节点集合
            if (Integer.valueOf(treeNode.getPid()).equals(pid)) {
                //递归遍历下一级
                treeMenuList(menuList, Integer.valueOf(treeNode.getId()), childMenu);
                childMenu.add(treeNode);
            }
        }
        return childMenu;
    }

    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<TreeNode> treeFirst(List<TreeNode> list) {
        //这里的Menu是我自己的实体类，参数只需要菜单id和父id即可，其他元素可任意增添
        Map<String, TreeNode> map = new HashMap<>(50);
        for (TreeNode treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }

    /**
     *
     * @param list
     * @param map
     * @return
     */
    private static List<TreeNode> addChildren(List<TreeNode> list, Map<String, TreeNode> map) {
        List<TreeNode> rootNodes = new ArrayList<>();
        for (TreeNode treeNode : list) {
            TreeNode parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<TreeNode>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }
}
